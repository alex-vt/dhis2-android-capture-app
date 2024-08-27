package org.dhis2.form.ui.biometrics.components.biometricsTEIRegistration

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.dhis2.form.R
import org.hisp.dhis.mobile.ui.designsystem.theme.SurfaceColor

@Composable
internal fun RegistrationResult(
    onBiometricsClick: (() -> Unit),
    enabled: Boolean,
    resultText: Int,
    resultIcon: Int?,
    resultColor: String,
    showRetake: Boolean,
) {

    val color = if (enabled) Color(
        android.graphics.Color.parseColor(
            resultColor
        )
    ) else SurfaceColor.DisabledSurface


    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
    ) {
        Button(
            modifier = Modifier.weight(1f).height(50.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = color
            ),
            contentPadding = PaddingValues(),
            onClick = {
                if (!showRetake) {
                    onBiometricsClick()
                }
            },
            enabled = enabled
        ) {
            Row (verticalAlignment = Alignment.CenterVertically) {
                resultIcon?.let {
                    Icon(
                        painter = painterResource(resultIcon),
                        contentDescription = stringResource(resultText),
                        tint = Color.Unspecified,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(resultText), color = Color.White)
            }

        }

        if (showRetake) {
            Spacer(modifier = Modifier.width(16.dp))

            Button(
                modifier = Modifier.width(200.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor =  Color(
                        android.graphics.Color.parseColor(
                            "#4d4d4d"
                        )
                    )
                ),
                contentPadding = PaddingValues(),
                onClick = onBiometricsClick,
                enabled = enabled,

            ) {
                Row (verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_bio_retry),
                        contentDescription = stringResource(R.string.biometrics_retake),
                        tint = Color.Unspecified,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(R.string.biometrics_retake), color = Color.White)
                }

            }
        }
    }
}