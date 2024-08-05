package org.dhis2.form.ui.biometrics.components.biometricsTEIRegistration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.dhis2.form.R
import org.hisp.dhis.mobile.ui.designsystem.component.Button
import org.hisp.dhis.mobile.ui.designsystem.component.ButtonStyle

enum class BiometricsState {
    INITIAL,
    SUCCESS,
    FAILURE,
}

@Composable
internal fun RegistrationResult(
    onBiometricsClick: (() -> Unit),
    resultText: Int,
    resultIcon: Int,
    resultColor: String,
    showRetake: Boolean,
) {


    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Button(
            text = stringResource(resultText).uppercase(),
            style = ButtonStyle.TEXT_LIGHT,
            icon = {
                Icon(
                    painter = painterResource(resultIcon),
                    contentDescription = stringResource(resultText),
                    tint = Color.Unspecified,
                )
            },
            modifier = Modifier.background(Color(android.graphics.Color.parseColor(resultColor)))
                .weight(1f)
                .height(50.dp),
            onClick = {
                if (!showRetake) {
                    onBiometricsClick()
                }
            },
        )

        if (showRetake) {
            Spacer(modifier = Modifier.width(16.dp))

            Button(
                text = stringResource(R.string.biometrics_retake).uppercase(),
                style = ButtonStyle.TEXT_LIGHT,
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_bio_retry),
                        contentDescription = stringResource(R.string.biometrics_retake),
                        tint = Color.Unspecified,
                    )
                },
                modifier = Modifier.background(
                    Color(
                        android.graphics.Color.parseColor(
                            "#4d4d4d"
                        )
                    )
                )
                    .width(200.dp)
                    .height(50.dp),
                onClick = { onBiometricsClick() },

                )
        }
    }
}