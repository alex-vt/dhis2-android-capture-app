package org.dhis2.data.service;

import androidx.annotation.NonNull;

import org.dhis2.data.biometrics.BiometricsConfigApi;
import org.dhis2.data.biometrics.BiometricsConfigRepositoryImpl;
import org.dhis2.data.dagger.PerService;
import org.dhis2.data.prefs.PreferenceProvider;
import org.dhis2.data.service.workManager.WorkManagerController;
import org.dhis2.usescases.biometrics.BiometricsConfigRepository;
import org.dhis2.utils.analytics.AnalyticsHelper;
import org.hisp.dhis.android.core.D2;

import dagger.Module;
import dagger.Provides;

@Module
@PerService
public class SyncMetadataWorkerModule {

    @Provides
    @PerService
    BiometricsConfigRepository biometricsConfigRepository(
            @NonNull D2 d2,
            @NonNull PreferenceProvider preferences
    ) {
        BiometricsConfigApi biometricsConfigApi = d2.retrofit().create(BiometricsConfigApi.class);
        return new BiometricsConfigRepositoryImpl(preferences, biometricsConfigApi);
    }

    @Provides
    @PerService
    SyncPresenter syncPresenter(
            @NonNull D2 d2,
            @NonNull PreferenceProvider preferences,
            @NonNull WorkManagerController workManagerController,
            @NonNull AnalyticsHelper analyticsHelper,
            @NonNull BiometricsConfigRepository biometricsConfigRepository
    ) {
        return new SyncPresenterImpl(d2, preferences, workManagerController, analyticsHelper,biometricsConfigRepository);
    }
}
