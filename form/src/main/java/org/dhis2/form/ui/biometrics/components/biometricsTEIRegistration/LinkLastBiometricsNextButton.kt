package org.dhis2.form.ui.biometrics.components.biometricsTEIRegistration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.dhis2.commons.biometrics.gradientButtonColor
import org.dhis2.form.R
import org.hisp.dhis.mobile.ui.designsystem.theme.SurfaceColor

@Composable
internal fun LinkLastBiometricsNextButton(
    enabled: Boolean,
    onClick: (() -> Unit),
) {
    val background = getColor(enabled)

    val modifier = Modifier.fillMaxWidth().height(50.dp)

    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(backgroundColor = if (background is BiometricsBackground.Solid) background.value else Color.Transparent),
        contentPadding = PaddingValues(),
        onClick = onClick,
        enabled = enabled
    ) {
        val boxModifier = when (background) {
            is BiometricsBackground.Gradient -> Modifier
                .background(background.value)
                .then(modifier)

            is BiometricsBackground.Solid -> Modifier
        }

        Box(
            modifier = boxModifier,
            contentAlignment = Alignment.Center,

            ) {
            Text(text = stringResource(R.string.next), color = Color.White)
        }
    }
}

private fun getColor(enabled: Boolean): BiometricsBackground {
    return if (enabled) {
        BiometricsBackground.Gradient(gradientButtonColor)
    } else {
        BiometricsBackground.Solid(SurfaceColor.DisabledSurface)
    }
}
