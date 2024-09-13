package org.dhis2.usescases.searchTrackEntity.listView

import dagger.Module
import dagger.Subcomponent
import org.dhis2.commons.di.dagger.PerFragment
import org.dhis2.usescases.biometrics.ui.SearchHelperFragment

@PerFragment
@Subcomponent(modules = [SearchHelperModule::class])
interface SearchHelperComponent {
    fun inject(fragment: SearchHelperFragment)
}

@Module
class SearchHelperModule()
