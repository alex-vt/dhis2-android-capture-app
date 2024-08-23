package org.dhis2.form.ui.biometrics.components.biometricsTEIRegistration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.dhis2.form.R

//TODO: Evaluate move this to a common module
// is a copy of TeiDashboardBioStatus
@Composable
fun BiometricsStatusFlag(
    text: String,
    backgroundColor: String,
    icon: Int
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 0.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.background(Color(android.graphics.Color.parseColor(backgroundColor)))
                .height(40.dp)
                .padding(start = 16.dp)

        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = text,
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp).padding(end = 4.dp)
            )
            Text(
                text.uppercase(),
                color = Color.White,
                modifier = Modifier.padding(end = 42.dp)
            )

        }
        Icon(
            imageVector = Icons.Filled.ArrowLeft,
            contentDescription = "Arrow",
            tint = Color.White,
            modifier = Modifier.size(80.dp).offset(x = (-46.5).dp, y = (0).dp)
        )
    }


}

@Preview
@Composable
fun BiometricsStatusFlagPreview() {
    BiometricsStatusFlag(
        text = "Biometrics",
        backgroundColor = "#4d4d4d",
        icon = R.drawable.ic_bio_face_success
    )
}

