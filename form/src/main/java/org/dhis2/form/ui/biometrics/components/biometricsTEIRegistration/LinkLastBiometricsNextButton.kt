package org.dhis2.form.ui.biometrics.components.biometricsTEIRegistration

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.dhis2.commons.biometrics.defaultButtonColor
import org.dhis2.form.R

@Composable
internal fun LinkLastBiometricsNextButton(
    enabled: Boolean,
    onClick: (() -> Unit),
) {
    val color = Color(
        android.graphics.Color.parseColor(
            defaultButtonColor
        ))

    Button(
        modifier =  Modifier.fillMaxWidth().height(50.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = color),
        contentPadding = PaddingValues(),
        onClick = onClick,
        enabled = enabled
    ) {
        Text(text = stringResource(R.string.next), color = Color.White)
    }
}
