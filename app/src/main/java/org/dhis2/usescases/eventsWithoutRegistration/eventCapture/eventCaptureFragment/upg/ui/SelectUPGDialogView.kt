package org.dhis2.usescases.eventsWithoutRegistration.eventCapture.eventCaptureFragment.upg.ui

import org.dhis2.usescases.eventsWithoutRegistration.eventCapture.eventCaptureFragment.upg.domain.UPGItem

interface SelectUPGDialogView {
    fun renderList(liveData: List<UPGItem>)
    fun onUPGSelected(item: UPGItem)
}