package org.dhis2.form.ui.biometrics.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.dhis2.commons.biometrics.BIOMETRICS_FAILURE_PATTERN
import org.dhis2.form.ui.biometrics.components.biometricsTEIRegistration.BiometricsTEIState
import org.dhis2.form.ui.biometrics.components.biometricsTEIRegistration.RegistrationButton


@Composable
internal fun BiometricsRegistration(
    value: String?,
    onBiometricsClick: () -> Unit,
    getIconByState: (BiometricsTEIState) -> Int?
) {
    val biometricsState = remember(value) {
        if (!value.isNullOrEmpty()) {
            if (value.startsWith(BIOMETRICS_FAILURE_PATTERN)) {
                BiometricsTEIState.FAILURE
            } else {
                BiometricsTEIState.SUCCESS
            }
        } else {
            BiometricsTEIState.INITIAL
        }
    }

    RegistrationButton(
        biometricsState=  biometricsState,
        enabled = true,
        onBiometricsClick = onBiometricsClick,
        getIconByState = getIconByState)
}