package org.dhis2.usescases.biometrics.duplicates

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import io.reactivex.functions.Consumer
import org.dhis2.usescases.searchTrackEntity.adapters.SearchTeiModel
import org.hisp.dhis.android.core.arch.call.D2Progress

interface BiometricsDuplicatesDialogView {
    fun setLiveData(liveData: LiveData<PagedList<SearchTeiModel>>)
    fun getContext(): Context?
    fun openDashboard(teiUid: String, programUid: String, enrollmentUid: String)
    fun downloadProgress(): Consumer<D2Progress>
    fun couldNotDownload(displayName: String)
    fun enrollNew(biometricsSessionId: String)
}
