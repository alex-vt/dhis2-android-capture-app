package org.dhis2.form.ui.biometrics.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.dhis2.form.R
import org.dhis2.form.model.biometrics.BiometricsDataElementStatus
import org.hisp.dhis.mobile.ui.designsystem.component.Button
import org.hisp.dhis.mobile.ui.designsystem.component.ButtonStyle


@Composable
internal fun BiometricsVerification(
    status: BiometricsDataElementStatus,
    onBiometricsClick: () -> Unit,
) {

    val icon = remember(status) {
        when (status) {
            BiometricsDataElementStatus.SUCCESS -> R.drawable.ic_bio_available_yes
            BiometricsDataElementStatus.FAILURE -> R.drawable.ic_bio_available_no
            BiometricsDataElementStatus.NOT_DONE -> R.drawable.ic_bio_available_no
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1.0f).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ){
            Text(
                text = "Biometrics Verification",
                style = MaterialTheme.typography.body1,
                color = Color.DarkGray

            )

            if (status != BiometricsDataElementStatus.NOT_DONE) {
                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    painter = painterResource(icon),
                    contentDescription = "Biometrics Verification",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified,
                )
            }
        }


        if (status == BiometricsDataElementStatus.FAILURE) {
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
                ).width(150.dp)
                    .padding(0.dp),
                onClick = { onBiometricsClick() },

                )
        }


    }
}

@Preview(showBackground = true)
@Composable
fun BiometricsVerificationPreview() {
    BiometricsVerification(
        status = BiometricsDataElementStatus.FAILURE,
        onBiometricsClick = { /*TODO: Handle click here*/ }
    )
}