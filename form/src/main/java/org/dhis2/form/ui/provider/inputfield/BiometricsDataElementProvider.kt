package org.dhis2.form.ui.provider.inputfield

import androidx.compose.runtime.Composable
import org.dhis2.commons.biometrics.getBioIconSuccess
import org.dhis2.commons.biometrics.getBioIconWarning
import org.dhis2.commons.resources.ResourceManager
import org.dhis2.form.model.biometrics.BiometricsDataElementUiModelImpl
import org.dhis2.form.ui.biometrics.components.BiometricsRegistration
import org.dhis2.form.ui.biometrics.components.BiometricsVerification
import org.dhis2.form.ui.biometrics.components.biometricsTEIRegistration.BiometricsTEIState

@Composable
internal fun ProvideBiometricsDataElement(
    fieldUiModel: BiometricsDataElementUiModelImpl,
    resources: ResourceManager,
) {
    if (!fieldUiModel.value.isNullOrEmpty()) {
        BiometricsVerification(fieldUiModel.status, fieldUiModel::onRetryVerificationClick)
    } else {
        BiometricsRegistration(fieldUiModel.value, fieldUiModel::onBiometricsClick) { state ->
            when (state) {
                BiometricsTEIState.INITIAL -> null
                BiometricsTEIState.SUCCESS -> resources.context.getBioIconSuccess()
                BiometricsTEIState.FAILURE -> resources.context.getBioIconWarning()
            }
        }
    }
}