package org.dhis2.usescases.biometrics.duplicates

import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import org.dhis2.data.schedulers.SchedulerProvider
import org.dhis2.data.search.SearchParametersModel
import org.dhis2.usescases.searchTrackEntity.SearchRepository
import org.dhis2.utils.NetworkUtils
import org.hisp.dhis.android.core.D2
import timber.log.Timber

class BiometricsDuplicatesDialogPresenter(
    private val d2: D2,
    private val searchRepository: SearchRepository,
    private val schedulerProvider: SchedulerProvider
) {
    lateinit var view: BiometricsDuplicatesDialogView
    lateinit var biometricsGuids: List<String>
    lateinit var biometricsSessionId: String
    lateinit var programUid: String
    lateinit var trackedEntityTypeUid: String
    lateinit var biometricsAttributeUid: String

    var disposable: CompositeDisposable = CompositeDisposable()

    fun init(
        view: BiometricsDuplicatesDialogView,
        biometricsGuids: List<String>,
        biometricsSessionId: String,
        programUid: String,
        trackedEntityTypeUid: String,
        biometricsAttributeUid: String
    ) {
        this.view = view
        this.biometricsGuids = biometricsGuids
        this.biometricsSessionId = biometricsSessionId
        this.programUid = programUid
        this.trackedEntityTypeUid = trackedEntityTypeUid
        this.biometricsAttributeUid = biometricsAttributeUid

        loadData()
    }

    private fun loadData() {
        val program = d2.programModule().programs().uid(programUid).blockingGet()

        disposable.add(
            Flowable.just(
                searchRepository.searchTrackedEntities(
                    SearchParametersModel(
                        program,
                        trackedEntityTypeUid,
                        hashMapOf(biometricsAttributeUid to biometricsGuids.joinToString(separator = ";"))
                    ),
                    NetworkUtils.isOnline(view.getContext())
                )
            ).doOnError { Timber.e(it) }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(
                    { liveData ->
                        view.setLiveData(liveData)
                    },
                    { Timber.e(it) })
        )
    }

    fun onDetach() {
        disposable.clear()
    }

    fun onTEIClick(teiUid: String, enrollmentUid: String, isOnline: Boolean) {
        if (!isOnline) {
            openDashboard(teiUid, enrollmentUid)
        } else {
            downloadTei(teiUid, enrollmentUid)
        }
    }

    private fun openDashboard(teiUid: String, enrollmentUid: String) {
        view.openDashboard(
            teiUid,
            programUid,
            enrollmentUid
        )
    }

    private fun downloadTei(teiUid: String?, enrollmentUid: String?) {
        disposable.add(
            searchRepository.downloadTei(teiUid)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(
                    { view.downloadProgress() }, { Timber.d(it) },
                    {
                        if (d2.trackedEntityModule().trackedEntityInstances().uid(teiUid)
                                .blockingExists()
                        ) {
                            openDashboard(teiUid!!, enrollmentUid!!)
                        } else {
                            val trackedEntityType = d2.trackedEntityModule().trackedEntityTypes()
                                .uid(trackedEntityTypeUid).blockingGet()
                            view.couldNotDownload(trackedEntityType.displayName()!!)
                        }
                    })
        )
    }

    fun enrollNewClick() {
        view.enrollNew(biometricsSessionId)
    }
}
