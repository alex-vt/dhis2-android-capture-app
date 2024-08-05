package org.dhis2.form.ui.biometrics.components.biometricsTEIRegistration

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import org.dhis2.commons.biometrics.BIOMETRICS_FAILURE_PATTERN
import org.dhis2.form.R
import org.dhis2.form.ui.biometrics.components.defaultButtonColor
import org.dhis2.form.ui.biometrics.components.failedButtonColor
import org.dhis2.form.ui.biometrics.components.successButtonColor

enum class BiometricsTEIState {
    INITIAL,
    SUCCESS,
    FAILURE,
}

//BIOMETRICS_SEARCH_PATTERN

@Composable
fun BiometricsTEIRegistration(
    value: String?,
    onBiometricsClick: () -> Unit,
    onSaveWithoutBiometrics: () -> Unit,
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

    Column {
        RegistrationButton(biometricsState, onBiometricsClick, getIconByState)
        SaveWithoutBiometricsButton(onSaveWithoutBiometrics)
    }
}

@Composable
fun RegistrationButton(
    biometricsState: BiometricsTEIState,
    onBiometricsClick: () -> Unit,
    getIconByState: (BiometricsTEIState) -> Int?
) {

    when (biometricsState) {
        BiometricsTEIState.INITIAL -> {
            RegistrationResult(
                onBiometricsClick = onBiometricsClick,
                resultText =  R.string.biometrics_get,
                resultIcon = getIconByState(BiometricsTEIState.INITIAL),
                resultColor = defaultButtonColor,
                showRetake = false
            )
        }

        BiometricsTEIState.SUCCESS -> {
            RegistrationResult(
                onBiometricsClick = onBiometricsClick,
                resultText =  R.string.biometrics_completed,
                resultIcon = getIconByState(BiometricsTEIState.SUCCESS),
                resultColor = successButtonColor,
                showRetake = true
            )
        }

        BiometricsTEIState.FAILURE -> {
            RegistrationResult(
                onBiometricsClick = onBiometricsClick,
                resultText =  R.string.biometrics_declined,
                resultIcon = getIconByState(BiometricsTEIState.FAILURE),
                resultColor = failedButtonColor,
                showRetake = true
            )
        }
    }
}

@Preview(showBackground = true, name = "Initial State")
@Composable
fun PreviewBiometricsTEIRegistrationInitial() {

    BiometricsTEIRegistration(
        value = null,
        onBiometricsClick = { },
        onSaveWithoutBiometrics = {},
        getIconByState = { null }
    )
}

@Preview(showBackground = true, name = "Success State")
@Composable
fun PreviewBiometricsTEIRegistrationSuccess() {
    BiometricsTEIRegistration(
        value = "927232-2-323-2-32-32-32",
        onBiometricsClick = { },
        onSaveWithoutBiometrics = {},
        getIconByState = { R.drawable.ic_bio_face_success }
    )
}

@Preview(showBackground = true, name = "Failure State")
@Composable
fun PreviewBiometricsTEIRegistrationFailure() {
    BiometricsTEIRegistration(
        value = BIOMETRICS_FAILURE_PATTERN,
        onBiometricsClick = { },
        onSaveWithoutBiometrics = {},
        getIconByState = { R.drawable.ic_bio_face_failed }
    )
}


