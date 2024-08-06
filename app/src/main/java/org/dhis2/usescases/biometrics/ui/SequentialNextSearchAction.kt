package org.dhis2.usescases.biometrics.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import org.dhis2.R



@Composable
internal fun SequentialNextSearchAction(
    sequentialSearchAction: SequentialSearchAction,
    onClick: (() -> Unit),
) {
    when(sequentialSearchAction){
        is SequentialSearchAction.SearchWithBiometrics -> {
            OutlinedButton(modifier = Modifier.width(270.dp).height(50.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = Color(0xFF0281cb)
                ),
                onClick = onClick) {
                Row() {
                    Icon(
                        painter = painterResource(org.dhis2.form.R.drawable.ic_bio_fingerprint),
                        contentDescription = stringResource(org.dhis2.form.R.string.biometrics_get),
                        tint = Color(0xFF0281cb),
                    )
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSize))
                    Text(stringResource(R.string.biometrics_search), color = Color(0xFF0281cb))
                }
            }
        }
        is SequentialSearchAction.SearchWithAttributes -> {
            OutlinedButton(modifier = Modifier.width(270.dp).height(50.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = Color(0xFF0281cb)
                ),
                onClick = onClick) {
                Text(stringResource(R.string.search_with_attributes), color = Color(0xFF0281cb))
            }
        }
        is SequentialSearchAction.RegisterNew -> {
            TextButton(
                onClick = onClick) {
                Text(
                    stringResource(R.string.register_new_patient),
                    color = Color(0xFF0281cb),
                    style = TextStyle(
                        textDecoration = TextDecoration.Underline,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}
