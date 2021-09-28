package org.dhis2.usescases.biometrics.duplicates

import io.reactivex.disposables.CompositeDisposable

class BiometricsDuplicatesDialogPresenter() {

    lateinit var view: BiometricsDuplicatesDialogView
    lateinit var guids: List<String>
    lateinit var sessionId: String

    var disposable: CompositeDisposable = CompositeDisposable()

    fun init(view: BiometricsDuplicatesDialogView, guids: List<String>, sessionId: String) {
        this.view = view
        this.guids = guids
        this.sessionId = sessionId

        loadData()
    }

    private fun loadData() {
        view.setData(guids.map { BiometricsDuplicatesDialogItem(it) })
    }

    private fun getCategoryOptionCombos() {
/*        disposable.add(
            d2.categoryModule().categoryCombos().uid(uid).get()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(
                    { view.setTitle(it.displayName() ?: "-") },
                    { Timber.e(it) }
                )
        )*/
    }

    fun onDetach() {
        disposable.clear()
    }
}
