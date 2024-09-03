package org.dhis2.form.ui.provider.inputfield

import androidx.compose.runtime.Composable
import org.dhis2.form.model.biometrics.BiometricsDataElementUiModelImpl
import org.dhis2.form.ui.biometrics.components.BiometricsRegistration
import org.dhis2.form.ui.biometrics.components.BiometricsVerification

@Composable
internal fun ProvideBiometricsDataElement(
    fieldUiModel: BiometricsDataElementUiModelImpl
) {
    if (!fieldUiModel.value.isNullOrEmpty()) {
        BiometricsVerification(fieldUiModel.status, fieldUiModel::onRetryVerificationClick)
    } else {
        BiometricsRegistration(fieldUiModel.value, fieldUiModel::onBiometricsClick)
    }
}