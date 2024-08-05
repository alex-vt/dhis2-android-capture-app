package org.dhis2.form.ui.biometrics.components.biometricsTEIRegistration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.dhis2.form.R
import org.dhis2.form.ui.biometrics.components.defaultButtonColor
import org.hisp.dhis.mobile.ui.designsystem.component.Button
import org.hisp.dhis.mobile.ui.designsystem.component.ButtonStyle

@Composable
internal fun LinkLastBiometricsNextButton(
    onClick: (() -> Unit),
) {
    val color = android.graphics.Color.parseColor(
        defaultButtonColor
    )

    Button(
        text = stringResource(R.string.next).uppercase(),
        style = ButtonStyle.TEXT_LIGHT,
        modifier = Modifier.fillMaxWidth().background(Color(color))
            .height(50.dp),
        onClick = onClick,
    )
}
