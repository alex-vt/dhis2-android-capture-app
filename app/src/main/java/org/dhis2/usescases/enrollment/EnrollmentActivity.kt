package org.dhis2.usescases.enrollment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.dhis2.App
import org.dhis2.R
import org.dhis2.commons.Constants.ENROLLMENT_UID
import org.dhis2.commons.Constants.PROGRAM_UID
import org.dhis2.commons.Constants.TEI_UID
import org.dhis2.commons.biometrics.BIOMETRICS_ENROLL_LAST_REQUEST
import org.dhis2.commons.biometrics.BIOMETRICS_ENROLL_REQUEST
import org.dhis2.commons.data.TeiAttributesInfo
import org.dhis2.commons.dialogs.imagedetail.ImageDetailActivity
import org.dhis2.commons.featureconfig.data.FeatureConfigRepository
import org.dhis2.commons.featureconfig.model.Feature
import org.dhis2.commons.resources.ResourceManager
import org.dhis2.data.biometrics.BiometricsClientFactory
import org.dhis2.data.biometrics.RegisterResult
import org.dhis2.data.biometrics.SimprintsItem
import org.dhis2.databinding.EnrollmentActivityBinding
import org.dhis2.form.data.GeometryController
import org.dhis2.form.data.GeometryParserImpl
import org.dhis2.form.model.EnrollmentRecords
import org.dhis2.form.model.EventMode
import org.dhis2.form.ui.FormView
import org.dhis2.form.ui.provider.EnrollmentResultDialogUiProvider
import org.dhis2.maps.views.MapSelectorActivity
import org.dhis2.ui.dialogs.bottomsheet.BottomSheetDialog
import org.dhis2.ui.dialogs.bottomsheet.BottomSheetDialogUiModel
import org.dhis2.ui.dialogs.bottomsheet.DialogButtonStyle
import org.dhis2.usescases.biometrics.duplicates.BiometricsDuplicatesDialog
import org.dhis2.usescases.events.ScheduledEventActivity
import org.dhis2.usescases.eventsWithoutRegistration.eventCapture.EventCaptureActivity
import org.dhis2.usescases.general.ActivityGlobalAbstract
import org.dhis2.usescases.teiDashboard.TeiDashboardMobileActivity
import org.dhis2.utils.granularsync.OPEN_ERROR_LOCATION
import org.hisp.dhis.android.core.common.FeatureType
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import javax.inject.Inject

class EnrollmentActivity : ActivityGlobalAbstract(), EnrollmentView {

    enum class EnrollmentMode { NEW, CHECK }

    private var forRelationship: Boolean = false
    private lateinit var formView: FormView

    @Inject
    lateinit var resourceManager: ResourceManager

    @Inject
    lateinit var presenter: EnrollmentPresenterImpl

    @Inject
    lateinit var enrollmentResultDialogUiProvider: EnrollmentResultDialogUiProvider

    @Inject
    lateinit var featureConfig: FeatureConfigRepository

    lateinit var binding: EnrollmentActivityBinding
    lateinit var mode: EnrollmentMode

    companion object {
        const val ENROLLMENT_UID_EXTRA = "ENROLLMENT_UID_EXTRA"
        const val PROGRAM_UID_EXTRA = "PROGRAM_UID_EXTRA"
        const val MODE_EXTRA = "MODE_EXTRA"
        const val FOR_RELATIONSHIP = "FOR_RELATIONSHIP"
        const val RQ_ENROLLMENT_GEOMETRY = 1023
        const val RQ_INCIDENT_GEOMETRY = 1024
        const val RQ_EVENT = 1025

        fun getIntent(
            context: Context,
            enrollmentUid: String,
            programUid: String,
            enrollmentMode: EnrollmentMode,
            forRelationship: Boolean? = false,
        ): Intent {
            val intent = Intent(context, EnrollmentActivity::class.java)
            intent.putExtra(ENROLLMENT_UID_EXTRA, enrollmentUid)
            intent.putExtra(PROGRAM_UID_EXTRA, programUid)
            intent.putExtra(MODE_EXTRA, enrollmentMode.name)
            intent.putExtra(FOR_RELATIONSHIP, forRelationship)
            return intent
        }
    }

    /*region LIFECYCLE*/

    override fun onCreate(savedInstanceState: Bundle?) {
        val enrollmentUid = intent.getStringExtra(ENROLLMENT_UID_EXTRA) ?: ""
        val programUid = intent.getStringExtra(PROGRAM_UID_EXTRA) ?: ""
        val enrollmentMode = intent.getStringExtra(MODE_EXTRA)?.let { EnrollmentMode.valueOf(it) }
            ?: EnrollmentMode.NEW
        val openErrorLocation = intent.getBooleanExtra(OPEN_ERROR_LOCATION, false)
        (applicationContext as App).userComponent()!!.plus(
            EnrollmentModule(
                this,
                enrollmentUid,
                programUid,
                enrollmentMode,
                context,
            ),
        ).inject(this)

        formView = FormView.Builder()
            .locationProvider(locationProvider)
            .onItemChangeListener { action -> presenter.updateFields(action) }
            .onLoadingListener { loading ->
                if (loading) {
                    showProgress()
                } else {
                    hideProgress()
                    //presenter.showOrHideSaveButton()
                }
            }
            .onFinishDataEntry {
                presenter.checkIfBiometricValueValid()
                presenter.finish(mode)
            }
            .resultDialogUiProvider(enrollmentResultDialogUiProvider)
            .factory(supportFragmentManager)
            .setRecords(
                EnrollmentRecords(
                    enrollmentUid = enrollmentUid,
                    enrollmentMode = org.dhis2.form.model.EnrollmentMode.valueOf(
                        enrollmentMode.name,
                    ),
                ),
            )
            .openErrorLocation(openErrorLocation)
            .useComposeForm(
                featureConfig.isFeatureEnable(Feature.COMPOSE_FORMS),
            )
            .onFieldsLoadedListener {
                presenter.onFieldsLoaded(it)
            }
            .onFieldsLoadingListener {
                presenter.onFieldsLoading(it)
            }
            .build()

        super.onCreate(savedInstanceState)

        if (presenter.getEnrollment() == null ||
            presenter.getEnrollment()?.trackedEntityInstance() == null
        ) {
            finish()
        }

        forRelationship = intent.getBooleanExtra(FOR_RELATIONSHIP, false)
        binding = DataBindingUtil.setContentView(this, R.layout.enrollment_activity)
        binding.view = this

        mode = enrollmentMode

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.formViewContainer, formView)
        fragmentTransaction.commit()

        binding.save.setOnClickListener {
            performSaveClick()
        }

        presenter.init()
    }

    override fun onResume() {
        presenter.subscribeToBackButton()
        super.onResume()
    }

    override fun onDestroy() {
        presenter.onDettach()
        super.onDestroy()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            RQ_INCIDENT_GEOMETRY, RQ_ENROLLMENT_GEOMETRY -> {
                if (resultCode == Activity.RESULT_OK  && data?.hasExtra(MapSelectorActivity.DATA_EXTRA) == true) {
                    handleGeometry(
                        FeatureType.valueOfFeatureType(
                            data.getStringExtra(MapSelectorActivity.LOCATION_TYPE_EXTRA),
                        ),
                        data.getStringExtra(MapSelectorActivity.DATA_EXTRA)!!,
                        requestCode,
                    )
                }
            }

            BIOMETRICS_ENROLL_REQUEST -> {
                if (data != null) {
                    when (val result = BiometricsClientFactory.get(this).handleRegisterResponse(
                        resultCode, data
                    )) {
                        is RegisterResult.Completed -> {
                            presenter.onBiometricsCompleted(result.guid)
                        }

                        is RegisterResult.Failure -> {
                            presenter.onBiometricsFailure()
                        }

                        is RegisterResult.PossibleDuplicates -> {
                            presenter.onBiometricsPossibleDuplicates(
                                result.guids,
                                result.sessionId
                            )
                        }

                        is RegisterResult.AgeGroupNotSupported -> {
                            Toast.makeText(
                                context, getString(R.string.age_group_not_supported),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

            BIOMETRICS_ENROLL_LAST_REQUEST -> {
                if (data != null) {
                    when (val result =
                        BiometricsClientFactory.get(this)
                            .handleRegisterResponse(resultCode, data)) {
                        is RegisterResult.Completed -> {
                            presenter.onBiometricsCompleted(result.guid)
                        }

                        is RegisterResult.AgeGroupNotSupported -> {
                            Toast.makeText(
                                context, getString(R.string.age_group_not_supported),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {
                            presenter.onBiometricsFailure()
                        }
                    }
                }

            }

            RQ_EVENT -> if (resultCode == Activity.RESULT_OK)  openDashboard(presenter.getEnrollment()!!.uid()!!)
        }
    }

    override fun openEvent(eventUid: String) {
        val biometricsGuid = presenter.getBiometricsGuid()

        if (presenter.isEventScheduleOrSkipped(eventUid)) {
            val scheduleEventIntent = ScheduledEventActivity.getIntent(
                this,
                eventUid,
                biometricsGuid,
                -1,
                presenter.getEnrollment()!!.organisationUnit()!!
            )
            openEventForResult.launch(scheduleEventIntent)
        } else {
            val eventCreationIntent = Intent(abstracContext, EventCaptureActivity::class.java)
            eventCreationIntent.putExtras(
                EventCaptureActivity.getActivityBundle(
                    eventUid,
                    presenter.getProgram()?.uid() ?: "",
                    EventMode.NEW,
                ),
            )
            startActivityForResult(eventCreationIntent, RQ_EVENT)
        }
    }

    private val openEventForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) {
        openDashboard(presenter.getEnrollment()!!.uid()!!)
    }

    override fun openDashboard(enrollmentUid: String) {
        if (forRelationship) {
            val intent = Intent()
            intent.putExtra("TEI_A_UID", presenter.getEnrollment()!!.trackedEntityInstance())
            setResult(Activity.RESULT_OK, intent)
            finish()
        } else {
            val bundle = Bundle()
            bundle.putString(PROGRAM_UID, presenter.getProgram()?.uid())
            bundle.putString(TEI_UID, presenter.getEnrollment()!!.trackedEntityInstance())
            bundle.putString(ENROLLMENT_UID, enrollmentUid)
            startActivity(TeiDashboardMobileActivity::class.java, bundle, true, false, null)
        }
    }

    override fun goBack() {
        onBackPressed()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        formView.onEditionFinish()
        attemptFinish()
    }

    private fun attemptFinish() {
        if (mode == EnrollmentMode.CHECK) {
            formView.onBackPressed()
        } else {
            showDeleteDialog()
        }
    }

    private fun showDeleteDialog() {
        BottomSheetDialog(
            bottomSheetDialogUiModel = BottomSheetDialogUiModel(
                title = getString(R.string.not_saved),
                message = getString(R.string.discard_go_back),
                iconResource = R.drawable.ic_error_outline,
                mainButton = DialogButtonStyle.MainButton(R.string.keep_editing),
                secondaryButton = DialogButtonStyle.DiscardButton(),
            ),
            onSecondaryButtonClicked = {
                presenter.deleteAllSavedData()
                finish()
            },
        ).show(supportFragmentManager, BottomSheetDialogUiModel::class.java.simpleName)
    }

    private fun handleGeometry(featureType: FeatureType, dataExtra: String, requestCode: Int) {
        val geometry = GeometryController(GeometryParserImpl()).generateLocationFromCoordinates(
            featureType,
            dataExtra,
        )

        if (geometry != null) {
            when (requestCode) {
                RQ_ENROLLMENT_GEOMETRY -> {
                    presenter.saveEnrollmentGeometry(geometry)
                }

                RQ_INCIDENT_GEOMETRY -> {
                    presenter.saveTeiGeometry(geometry)
                }
            }
        }
    }

    override fun setResultAndFinish() {
        setResult(RESULT_OK)
        finish()
    }

    /*endregion*/

    /*region TEI*/
    override fun displayTeiInfo(teiInfo: TeiAttributesInfo) {
        if (mode != EnrollmentMode.NEW) {
            binding.title.text =
                resourceManager.defaultEnrollmentLabel(
                    programUid = presenter.getProgram()?.uid()!!,
                    true,
                    1,
                )
        } else {
            binding.title.text =
                resourceManager.formatWithEnrollmentLabel(
                    programUid = presenter.getProgram()?.uid()!!,
                    R.string.new_enrollment,
                    1,
                )
        }
    }

    override fun displayTeiPicture(picturePath: String) {
        val intent = ImageDetailActivity.intent(
            context = this,
            title = null,
            imagePath = picturePath,
        )

        startActivity(intent)
    }
    /*endregion*/
    /*region ACCESS*/

    override fun setAccess(access: Boolean?) {
        if (access == false) {
            binding.save.visibility = View.GONE
        }
    }
    /*endregion*/

    /*region STATUS*/

    override fun renderStatus(status: EnrollmentStatus) {
        binding.enrollmentStatus = status
    }

    /*endregion*/

    override fun requestFocus() {
        binding.root.requestFocus()
    }

    override fun setSaveButtonVisible(visible: Boolean) {
        if (visible) {
            binding.save.show()
        } else {
            binding.save.hide()
        }
    }

    override fun performSaveClick() {
        formView.onSaveClick()
    }

    override fun showProgress() {
        runOnUiThread {
            binding.toolbarProgress.show()
        }
    }

    override fun hideProgress() {
        runOnUiThread {
            binding.toolbarProgress.hide()
        }
    }

    override fun showDateEditionWarning() {
        val dialog = MaterialAlertDialogBuilder(this, R.style.DhisMaterialDialog)
            .setMessage(R.string.enrollment_date_edition_warning)
            .setPositiveButton(R.string.button_ok, null)
        dialog.show()
    }

    override fun registerBiometrics(orgUnit: String, ageInMonths: Long) {
        BiometricsClientFactory.get(this).register(this, orgUnit, ageInMonths)
    }

    override fun showPossibleDuplicatesDialog(
        possibleDuplicates: List<SimprintsItem>, sessionId: String, programUid: String,
        trackedEntityTypeUid: String,
        biometricsAttributeUid: String,
        enrollNewVisible: Boolean
    ) {
        val dialog = BiometricsDuplicatesDialog.newInstance(
            possibleDuplicates, sessionId, programUid,
            trackedEntityTypeUid,
            biometricsAttributeUid,
            enrollNewVisible
        )

        dialog.setOnOpenTeiDashboardListener { teiUid: String, programUid: String, enrollmentUid: String ->
            presenter.deleteAllSavedData()
            finish()
            startActivity(
                TeiDashboardMobileActivity.intent(
                    this,
                    teiUid,
                    programUid,
                    enrollmentUid
                )
            )
        }

        dialog.setOnEnrollNewListener { biometricsSessionId ->
            BiometricsClientFactory.get(this).registerLast(this, biometricsSessionId)
        }

        dialog.show(
            supportFragmentManager,
            BiometricsDuplicatesDialog.TAG
        )
    }

    override fun registerLast(sessionId: String) {
        BiometricsClientFactory.get(this).registerLast(this, sessionId)
    }
}
