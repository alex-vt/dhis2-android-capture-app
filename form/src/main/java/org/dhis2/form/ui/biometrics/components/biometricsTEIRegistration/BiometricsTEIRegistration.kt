package org.dhis2.form.ui.biometrics.components.biometricsTEIRegistration

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import org.dhis2.commons.biometrics.BIOMETRICS_FAILURE_PATTERN
import org.dhis2.form.R

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
    getIconByState: (BiometricsTEIState) -> Int
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

    when (biometricsState) {
        BiometricsTEIState.INITIAL -> {
            RegistrationResult(
                onBiometricsClick = onBiometricsClick,
                resultText =  R.string.biometrics_get,
                resultIcon = getIconByState(BiometricsTEIState.INITIAL),
                resultColor = "#4d4d4d",
                showRetake = false
            )
        }

        BiometricsTEIState.SUCCESS -> {
            RegistrationResult(
                onBiometricsClick = onBiometricsClick,
                resultText =  R.string.biometrics_completed,
                resultIcon = getIconByState(BiometricsTEIState.SUCCESS),
                resultColor = "#FF35835D",
                showRetake = true
            )
        }

        BiometricsTEIState.FAILURE -> {
            RegistrationResult(
                onBiometricsClick = onBiometricsClick,
                resultText =  R.string.biometrics_declined,
                resultIcon = getIconByState(BiometricsTEIState.FAILURE),
                resultColor = "#C57704",
                showRetake = true
            )
        }
    }
}

@Preview(showBackground = true, name = "Initial State")
@Composable
fun PreviewBiometricsTEIRegistrationInitial() {
    val onBiometricsClick = remember { {} }

    BiometricsTEIRegistration(
        value = null,
        onBiometricsClick = onBiometricsClick,
        getIconByState = { R.drawable.ic_bio_face_new }
    )
}

@Preview(showBackground = true, name = "Success State")
@Composable
fun PreviewBiometricsTEIRegistrationSuccess() {
    val onBiometricsClick = remember { {} }

    BiometricsTEIRegistration(
        value = "927232-2-323-2-32-32-32",
        onBiometricsClick = onBiometricsClick,
        getIconByState = { R.drawable.ic_bio_face_success }
    )
}

@Preview(showBackground = true, name = "Failure State")
@Composable
fun PreviewBiometricsTEIRegistrationFailure() {
    BiometricsTEIRegistration(
        value = BIOMETRICS_FAILURE_PATTERN,
        onBiometricsClick = { },
        getIconByState = { R.drawable.ic_bio_face_failed }
    )
}


