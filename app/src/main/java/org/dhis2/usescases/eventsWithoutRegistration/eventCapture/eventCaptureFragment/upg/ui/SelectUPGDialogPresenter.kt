package org.dhis2.usescases.eventsWithoutRegistration.eventCapture.eventCaptureFragment.upg.ui

import org.dhis2.usescases.eventsWithoutRegistration.eventCapture.eventCaptureFragment.upg.domain.GetUPGItems
import org.dhis2.usescases.eventsWithoutRegistration.eventCapture.eventCaptureFragment.upg.domain.UPGItem

class SelectUPGDialogPresenter(
    private val getUPGItems: GetUPGItems
) {
    lateinit var view: SelectUPGDialogView

    fun init(
        view: SelectUPGDialogView,
        orgUnitUid: String
    ) {
        this.view = view

        loadData(orgUnitUid)
    }

    private fun loadData(orgUnitUid: String) {
        view.renderList(getUPGItems(orgUnitUid))
    }

    fun onDetach() {

    }

    fun onItemClick(item: UPGItem) {
        view.onUPGSelected(item)
    }
}
