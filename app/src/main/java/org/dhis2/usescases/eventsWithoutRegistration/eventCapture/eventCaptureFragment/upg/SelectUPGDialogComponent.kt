package org.dhis2.usescases.eventsWithoutRegistration.eventCapture.eventCaptureFragment.upg

import dagger.Subcomponent
import org.dhis2.usescases.eventsWithoutRegistration.eventCapture.eventCaptureFragment.upg.ui.SelectUPGDialog

@Subcomponent(modules = [SelectUPGDialogModule::class])
interface SelectUPGDialogComponent {
    fun inject(dialog: SelectUPGDialog)
}