package org.dhis2.form.ui.provider.inputfield

import androidx.compose.runtime.Composable
import org.dhis2.commons.resources.ResourceManager
import org.dhis2.form.model.biometrics.BiometricsAttributeUiModelImpl
import org.dhis2.form.ui.biometrics.components.BiometricsRegistration

@Composable
internal fun ProvideBiometricsAttribute(
    fieldUiModel: BiometricsAttributeUiModelImpl,
    resources: ResourceManager,
) {
    BiometricsRegistration(fieldUiModel.value, fieldUiModel::onBiometricsClick, resources)
}