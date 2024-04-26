package org.dhis2.form.ui.provider.inputfield

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.dhis2.commons.biometrics.BIOMETRICS_FAILURE_PATTERN
import org.dhis2.commons.biometrics.getBioIconNew
import org.dhis2.commons.biometrics.getBioIconSuccess
import org.dhis2.commons.biometrics.getBioIconWarning
import org.dhis2.commons.resources.ResourceManager
import org.dhis2.form.R
import org.dhis2.form.model.biometrics.BiometricsAttributeUiModelImpl
import org.hisp.dhis.mobile.ui.designsystem.component.Button
import org.hisp.dhis.mobile.ui.designsystem.component.ButtonStyle

enum class BiometricsState {
    INITIAL,
    SUCCESS,
    FAILURE,
}

@Composable
internal fun ProvideBiometricsAttribute(
    fieldUiModel: BiometricsAttributeUiModelImpl,
    resources: ResourceManager,
) {
    val biometricsState = remember(fieldUiModel.value) {
        if (!fieldUiModel.value.isNullOrEmpty()) {
            if (fieldUiModel.value.startsWith(BIOMETRICS_FAILURE_PATTERN)) {
                BiometricsState.FAILURE
            } else {
                BiometricsState.SUCCESS
            }
        } else {
            BiometricsState.INITIAL
        }
    }

    val biometricsText = remember(biometricsState) {
        when (biometricsState) {
            BiometricsState.INITIAL -> R.string.biometrics_get
            BiometricsState.SUCCESS -> R.string.biometrics_completed
            BiometricsState.FAILURE -> R.string.biometrics_declined
        }
    }

    val biometricsIcon = remember(biometricsState) {
        when (biometricsState) {
            BiometricsState.INITIAL -> resources.context.getBioIconNew()
            BiometricsState.SUCCESS -> resources.context.getBioIconSuccess()
            BiometricsState.FAILURE -> resources.context.getBioIconWarning()
        }
    }

    val biometricsColor = remember(biometricsState) {
        when (biometricsState) {
            BiometricsState.INITIAL -> "#4d4d4d"
            BiometricsState.SUCCESS -> "#FF35835D"
            BiometricsState.FAILURE -> "#C57704"
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Button(
            text = stringResource(biometricsText).uppercase(),
            style = ButtonStyle.TEXT_LIGHT,
            icon = {
                Icon(
                    painter = painterResource(biometricsIcon),
                    contentDescription = stringResource(biometricsText),
                    tint = Color.Unspecified,
                )
            },
            modifier = Modifier.background(Color(android.graphics.Color.parseColor(biometricsColor)))
                .weight(1f)
                .height(50.dp),
            onClick = {
                if (biometricsState == BiometricsState.INITIAL) {
                    fieldUiModel.onBiometricsClick()
                }
            },
        )

        if (biometricsState != BiometricsState.INITIAL) {
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
                onClick = { fieldUiModel.onBiometricsClick() },

                )
        }


    }
}