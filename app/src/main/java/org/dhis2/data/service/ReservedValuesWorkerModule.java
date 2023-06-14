package org.dhis2.data.service;

import android.app.NotificationManager;
import android.content.Context;

import androidx.annotation.NonNull;

import org.dhis2.commons.di.dagger.PerService;
import org.dhis2.commons.prefs.PreferenceProvider;
import org.dhis2.data.biometrics.BiometricsConfigApi;
import org.dhis2.data.biometrics.BiometricsConfigRepositoryImpl;;
import org.dhis2.data.service.workManager.WorkManagerController;
import org.dhis2.usescases.biometrics.BiometricsConfigRepository;
import org.dhis2.utils.analytics.AnalyticsHelper;
import org.hisp.dhis.android.core.D2;

import dagger.Module;
import dagger.Provides;

@Module
public class ReservedValuesWorkerModule {

    @Provides
    @PerService
    NotificationManager notificationManager(@NonNull Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Provides
    @PerService
    SyncRepository syncRepository(@NonNull D2 d2) {
        return new SyncRepositoryImpl(d2);
    }

    @Provides
    @PerService
    BiometricsConfigRepository biometricsConfigRepository(
            @NonNull D2 d2,
            @NonNull PreferenceProvider preferences
    ) {
        BiometricsConfigApi biometricsConfigApi = d2.retrofit().create(BiometricsConfigApi.class);
        return new BiometricsConfigRepositoryImpl(d2, preferences, biometricsConfigApi);
    }

    @Provides
    @PerService
    SyncPresenter syncPresenter(
            @NonNull D2 d2,
            @NonNull PreferenceProvider preferences,
            @NonNull WorkManagerController workManagerController,
            @NonNull AnalyticsHelper analyticsHelper,
            @NonNull SyncStatusController syncStatusController,
            @NonNull SyncRepository syncRepository,
            @NonNull BiometricsConfigRepository biometricsConfigRepository
    ) {
        return new SyncPresenterImpl(d2, preferences, workManagerController, analyticsHelper, syncStatusController, syncRepository,biometricsConfigRepository);
    }
}
