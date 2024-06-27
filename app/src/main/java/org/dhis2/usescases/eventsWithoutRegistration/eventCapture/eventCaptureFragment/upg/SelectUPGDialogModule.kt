package org.dhis2.usescases.eventsWithoutRegistration.eventCapture.eventCaptureFragment.upg

import dagger.Module
import dagger.Provides
import org.dhis2.usescases.eventsWithoutRegistration.eventCapture.eventCaptureFragment.upg.data.UPGD2Repository
import org.dhis2.usescases.eventsWithoutRegistration.eventCapture.eventCaptureFragment.upg.domain.GetUPGItems
import org.dhis2.usescases.eventsWithoutRegistration.eventCapture.eventCaptureFragment.upg.domain.UPGRepository
import org.dhis2.usescases.eventsWithoutRegistration.eventCapture.eventCaptureFragment.upg.ui.SelectUPGDialogPresenter
import org.hisp.dhis.android.core.D2

@Module
class SelectUPGDialogModule() {
    @Provides
    fun providesPresenter(
        getUPGItems: GetUPGItems
    ): SelectUPGDialogPresenter {
        return SelectUPGDialogPresenter(getUPGItems)
    }

    @Provides
    fun provideGetUPGItems(upgRepository: UPGRepository): GetUPGItems {
        return GetUPGItems(upgRepository)
    }

    @Provides
    fun provideUPGRepository(
        d2: D2,
    ): UPGRepository {
        return UPGD2Repository(d2)
    }


}
