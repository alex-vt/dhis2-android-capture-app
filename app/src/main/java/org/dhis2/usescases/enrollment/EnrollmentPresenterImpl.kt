package org.dhis2.usescases.enrollment

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.FlowableProcessor
import io.reactivex.processors.PublishProcessor
import org.dhis2.Bindings.profilePicturePath
import org.dhis2.commons.matomo.Actions.Companion.CREATE_TEI
import org.dhis2.commons.matomo.Categories.Companion.TRACKER_LIST
import org.dhis2.commons.matomo.Labels.Companion.CLICK
import org.dhis2.commons.matomo.MatomoAnalyticsController
import org.dhis2.commons.schedulers.SchedulerProvider
import org.dhis2.commons.schedulers.defaultSubscribe
import org.dhis2.form.data.EnrollmentRepository

import org.dhis2.form.model.FieldUiModel
import org.dhis2.form.model.RowAction
import org.dhis2.form.model.biometrics.BiometricsUiModelImpl
import org.dhis2.usescases.biometrics.BIOMETRICS_ENABLED
import org.dhis2.commons.biometrics.BIOMETRICS_FAILURE_PATTERN
import org.dhis2.utils.analytics.AnalyticsHelper
import org.dhis2.utils.analytics.DELETE_AND_BACK
import org.hisp.dhis.android.core.D2
import org.hisp.dhis.android.core.arch.repositories.`object`.ReadOnlyOneObjectRepositoryFinalImpl
import org.hisp.dhis.android.core.common.FeatureType
import org.hisp.dhis.android.core.common.Geometry
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.common.ValueType
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.EnrollmentAccess
import org.hisp.dhis.android.core.enrollment.EnrollmentObjectRepository
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.event.EventCollectionRepository
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceObjectRepository
import timber.log.Timber
import java.util.UUID

private const val TAG = "EnrollmentPresenter"

class EnrollmentPresenterImpl(
    val view: EnrollmentView,
    val d2: D2,
    private val enrollmentObjectRepository: EnrollmentObjectRepository,
    private val dataEntryRepository: EnrollmentRepository,
    private val teiRepository: TrackedEntityInstanceObjectRepository,
    private val programRepository: ReadOnlyOneObjectRepositoryFinalImpl<Program>,
    private val schedulerProvider: SchedulerProvider,
    private val enrollmentFormRepository: EnrollmentFormRepository,
    private val analyticsHelper: AnalyticsHelper,
    private val matomoAnalyticsController: MatomoAnalyticsController,
    private val eventCollectionRepository: EventCollectionRepository
) {
    private var finishing: Boolean = false
    private val disposable = CompositeDisposable()
    private val backButtonProcessor: FlowableProcessor<Boolean> = PublishProcessor.create()
    private var hasShownIncidentDateEditionWarning = false
    private var hasShownEnrollmentDateEditionWarning = false
    private var biometricsUiModel: BiometricsUiModelImpl? = null

    fun init() {
        view.setSaveButtonVisible(false)

        disposable.add(
            teiRepository.get()
                .flatMap { tei ->
                    d2.trackedEntityModule().trackedEntityTypeAttributes()
                        .byTrackedEntityTypeUid().eq(tei.trackedEntityType()).get()
                        .map { list ->
                            val attrList = list.filter {
                                d2.trackedEntityModule().trackedEntityAttributes()
                                    .uid(it.trackedEntityAttribute()?.uid())
                                    .blockingGet().valueType() != ValueType.IMAGE
                            }.sortedBy {
                                it.sortOrder()
                            }.map {
                                d2.trackedEntityModule().trackedEntityAttributeValues()
                                    .byTrackedEntityInstance().eq(tei.uid())
                                    .byTrackedEntityAttribute().eq(
                                        it.trackedEntityAttribute()?.uid()
                                    )
                                    .one()
                                    .blockingGet()?.value() ?: ""
                            }
                            val icon =
                                tei.profilePicturePath(d2, programRepository.blockingGet().uid())
                            Pair(attrList, icon)
                        }
                }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(
                    { mainAttributes ->
                        view.displayTeiInfo(mainAttributes.first, mainAttributes.second)
                    },
                    { Timber.tag(TAG).e(it) }
                )
        )

        disposable.add(
            programRepository.get()
                .map { it.access()?.data()?.write() }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(
                    { view.setAccess(it) },
                    { Timber.tag(TAG).e(it) }
                )
        )

        disposable.add(
            enrollmentObjectRepository.get()
                .map { it.status() }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(
                    { view.renderStatus(it!!) },
                    { Timber.tag(TAG).e(it) }
                )
        )
    }

    private fun shouldShowDateEditionWarning(uid: String): Boolean {
        return if (uid == EnrollmentRepository.ENROLLMENT_DATE_UID &&
            dataEntryRepository.hasEventsGeneratedByEnrollmentDate() &&
            !hasShownEnrollmentDateEditionWarning
        ) {
            hasShownEnrollmentDateEditionWarning = true
            true
        } else if (uid == EnrollmentRepository.INCIDENT_DATE_UID &&
            dataEntryRepository.hasEventsGeneratedByIncidentDate() &&
            !hasShownIncidentDateEditionWarning
        ) {
            hasShownIncidentDateEditionWarning = true
            true
        } else {
            false
        }
    }

    fun subscribeToBackButton() {
        disposable.add(
            backButtonProcessor
                .observeOn(schedulerProvider.ui())
                .subscribe(
                    { view.performSaveClick() },
                    { t -> Timber.e(t) }
                )
        )
    }

    fun finish(enrollmentMode: EnrollmentActivity.EnrollmentMode) {
        when (enrollmentMode) {
            EnrollmentActivity.EnrollmentMode.NEW -> {
                matomoAnalyticsController.trackEvent(TRACKER_LIST, CREATE_TEI, CLICK)
                disposable.add(
                    enrollmentFormRepository.generateEvents()
                        .defaultSubscribe(
                            schedulerProvider,
                            {
                                it.second?.let { eventUid ->
                                    view.openEvent(eventUid)
                                } ?: view.openDashboard(it.first)
                            },
                            { Timber.tag(TAG).e(it) }
                        )
                )
            }
            EnrollmentActivity.EnrollmentMode.CHECK -> view.setResultAndFinish()
        }
    }

    fun updateFields(action: RowAction? = null) {
        action?.let {
            if (shouldShowDateEditionWarning(it.id)) {
                view.showDateEditionWarning()
            }
        }
        if (finishing) {
            view.performSaveClick()
        }
        finishing = false
    }

    fun backIsClicked() {
        backButtonProcessor.onNext(true)
    }

    fun openInitial(eventUid: String): Boolean {
        val catComboUid = getProgram().categoryComboUid()
        val event = d2.eventModule().events().uid(eventUid).blockingGet()
        val stage = d2.programModule().programStages().uid(event.programStage()).blockingGet()
        val needsCatCombo = programRepository.blockingGet().categoryComboUid() != null &&
            d2.categoryModule().categoryCombos().uid(catComboUid).blockingGet().isDefault == false
        val needsCoordinates =
            stage.featureType() != null && stage.featureType() != FeatureType.NONE

        return needsCatCombo || needsCoordinates
    }

    fun getEnrollment(): Enrollment? {
        return enrollmentObjectRepository.blockingGet()
    }

    fun getProgram(): Program {
        return programRepository.blockingGet()
    }

    fun updateEnrollmentStatus(newStatus: EnrollmentStatus): Boolean {
        return try {
            if (getProgram().access()?.data()?.write() == true) {
                enrollmentObjectRepository.setStatus(newStatus)
                view.renderStatus(newStatus)
                true
            } else {
                view.displayMessage(null)
                false
            }
        } catch (error: D2Error) {
            false
        }
    }

    fun hasAccess() = getProgram().access()?.data()?.write() ?: false

    fun saveEnrollmentGeometry(geometry: Geometry?) {
        enrollmentObjectRepository.setGeometry(geometry)
    }

    fun saveTeiGeometry(geometry: Geometry?) {
        teiRepository.setGeometry(geometry)
    }

    fun deleteAllSavedData() {
        if (teiRepository.blockingGet().syncState() == State.TO_POST) {
            teiRepository.blockingDelete()
        } else {
            enrollmentObjectRepository.blockingDelete()
        }
        analyticsHelper.setEvent(DELETE_AND_BACK, CLICK, DELETE_AND_BACK)
    }

    fun onDettach() {
        disposable.clear()
    }

    fun displayMessage(message: String?) {
        view.displayMessage(message)
    }

    /*fun dataIntegrityCheck(): Boolean {
        return when {
            uniqueFields.isNotEmpty() -> {
                view.showInfoDialog(
                    view.context.getString(R.string.error),
                    view.context.getString(R.string.unique_coincidence_found)
                )
                false
            }
            mandatoryFields.isNotEmpty() -> {
                showErrors = Pair(true, showErrors.second)
                fieldsFlowable.onNext(true)
                view.showMissingMandatoryFieldsMessage(mandatoryFields)
                false
            }
            this.errorFields.isNotEmpty() -> {
                showErrors = Pair(showErrors.first || warningFields.isNotEmpty(), true)
                fieldsFlowable.onNext(true)
                view.showErrorFieldsMessage(errorFields.values.toList())
                false
            }
            warningFields.isNotEmpty() -> {
                showErrors = Pair(true, showErrors.second)
                fieldsFlowable.onNext(true)
                view.showWarningFieldsMessage(warningFields.values.toList())
                false
            }
            else -> {
                analyticsHelper.setEvent(SAVE_ENROLL, CLICK, SAVE_ENROLL)
                true
            }
        }
    }*/

    fun onTeiImageHeaderClick() {
        val picturePath = enrollmentFormRepository.getProfilePicture()
        if (picturePath.isNotEmpty()) {
            view.displayTeiPicture(picturePath)
        }
    }

    fun setFinishing() {
        finishing = true
    }

    fun getEventStage(eventUid: String) =
        enrollmentFormRepository.getProgramStageUidFromEvent(eventUid)

    fun showOrHideSaveButton() {
        val teiUid = teiRepository.blockingGet().uid()
        val programUid = getProgram().uid()
        val hasEnrollmentAccess = d2.enrollmentModule().enrollmentService()
            .blockingGetEnrollmentAccess(teiUid, programUid)
        if (hasEnrollmentAccess == EnrollmentAccess.WRITE_ACCESS) {
            view.setSaveButtonVisible(visible = true)
        } else {
            view.setSaveButtonVisible(visible = false)
        }
    }

    fun isEventScheduleOrSkipped(eventUid: String): Boolean {
        val event = eventCollectionRepository.uid(eventUid).blockingGet()
        return event.status() == EventStatus.SCHEDULE ||
            event.status() == EventStatus.SKIPPED ||
            event.status() == EventStatus.OVERDUE
    }

    fun onBiometricsCompleted(guid: String) {
        saveBiometricValue(guid)
    }

    fun onBiometricsFailure() {
        val uuid: UUID = UUID.randomUUID()
        saveBiometricValue("${BIOMETRICS_FAILURE_PATTERN}_${uuid}")
    }

    fun checkIfBiometricValueValid() {
        if ( BIOMETRICS_ENABLED) {
            if (biometricsUiModel != null && biometricsUiModel!!.value != null && biometricsUiModel!!.value!!.startsWith(
                    BIOMETRICS_FAILURE_PATTERN
                )
            ) {
                biometricsUiModel!!.onTextChange(null)
                biometricsUiModel!!.onSave(null)
            }
        }
    }

    private fun saveBiometricValue(value: String) {
        if (biometricsUiModel != null) {
            biometricsUiModel!!.onTextChange(value)
            biometricsUiModel!!.onSave(value)
        }
    }

    fun onBiometricsPossibleDuplicates(guids: List<String>, sessionId: String) {
        val program = getProgram().uid()
        val biometricsAttUid = biometricsUiModel!!.uid
        val teiUid = getEnrollment()!!.trackedEntityInstance()

        val teiTypeUid = d2.trackedEntityModule().trackedEntityInstances().uid(teiUid).blockingGet()
            .trackedEntityType()!!

        if (guids.size == 1 && guids[0] == biometricsUiModel!!.value){
            view.registerLast(sessionId)
        } else {
            val finalGuids = guids.filter { it != biometricsUiModel!!.value  }

            view.hideProgress()
            view.showPossibleDuplicatesDialog(finalGuids, sessionId, program, teiTypeUid, biometricsAttUid)
        }
    }

    fun onFieldsLoaded(fields: List<FieldUiModel>) {
        if (BIOMETRICS_ENABLED) {
            biometricsUiModel = fields.firstOrNull {
                it is BiometricsUiModelImpl
            }?.let { it as BiometricsUiModelImpl }

            biometricsUiModel?.setBiometricsRegisterListener(
                object : BiometricsUiModelImpl.BiometricsOnRegisterClickListener {
                    override fun onClick() {
                        val orgUnit = enrollmentObjectRepository.get().blockingGet()
                            .organisationUnit()!!
                        view.registerBiometrics(orgUnit)

                    }
                })
        }
    }
}
