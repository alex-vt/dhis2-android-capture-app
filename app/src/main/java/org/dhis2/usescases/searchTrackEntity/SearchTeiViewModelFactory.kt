package org.dhis2.usescases.searchTrackEntity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.dhis2.commons.network.NetworkUtils
import org.dhis2.commons.viewmodel.DispatcherProvider
import org.dhis2.maps.usecases.MapStyleConfiguration
import org.dhis2.usescases.biometrics.usecases.GetRelatedTEIUIdsByUid

@Suppress("UNCHECKED_CAST")
class SearchTeiViewModelFactory(
    val presenter: SearchTEContractsModule.Presenter,
    val searchRepository: SearchRepository,
    private val searchNavPageConfigurator: SearchPageConfigurator,
    private val initialProgramUid: String?,
    private val initialQuery: MutableMap<String, String>?,
    private val mapDataRepository: MapDataRepository,
    private val networkUtils: NetworkUtils,
    private val dispatchers: DispatcherProvider,
    private val mapStyleConfig: MapStyleConfiguration,
    private val getRelatedTEIUidsByUid: GetRelatedTEIUIdsByUid,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchTEIViewModel(
            initialProgramUid,
            initialQuery,
            presenter,
            searchRepository,
            searchNavPageConfigurator,
            mapDataRepository,
            networkUtils,
            dispatchers,
            mapStyleConfig,
            getRelatedTEIUidsByUid
        ) as T
    }
}
