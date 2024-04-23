package org.dhis2.usescases.teiDashboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.dhis2.R
import org.dhis2.usescases.teiDashboard.ui.model.TeiBiometricsVerificationModel
import org.hisp.dhis.mobile.ui.designsystem.component.Button
import org.hisp.dhis.mobile.ui.designsystem.component.ButtonStyle


@Composable
fun TeiVerificationButton(
    verification: TeiBiometricsVerificationModel
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            text = verification.text.uppercase(),
            style = ButtonStyle.TEXT_LIGHT,
            icon = {
                Icon(
                    painter = painterResource(verification.icon),
                    contentDescription = verification.text,
                    tint = Color.Unspecified,
                )
            },
            modifier = Modifier.background(Color(android.graphics.Color.parseColor(verification.backgroundColor)))
                .width(200.dp)
                .height(40.dp),
            onClick = verification.onActionClick,
        )
    }
}

@Preview
@Composable
fun TeiVerificationButtonPreview() {
    TeiVerificationButton(verification = TeiBiometricsVerificationModel(
        text = "Biometrics",
        backgroundColor = "#4d4d4d",
        icon = R.drawable.ic_bio_face_success,
        onActionClick = {}))
}

