package org.dhis2.usescases.biometrics.ui

import android.graphics.Color.parseColor
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.dhis2.R
import org.hisp.dhis.mobile.ui.designsystem.component.Button
import org.hisp.dhis.mobile.ui.designsystem.component.ButtonStyle


@Composable
internal fun SequentialNextSearchAction(
    sequentialSearchAction: SequentialSearchAction,
    onClick: (() -> Unit),
) {
    when(sequentialSearchAction){
        is SequentialSearchAction.SearchWithBiometrics -> {
            OutlinedButton(modifier = Modifier.defaultMinSize(minHeight = 50.dp, minWidth = 270.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = Color(0xFF0281cb)
                ),
                onClick = onClick) {
                Text(stringResource(R.string.biometrics_search), color = Color(0xFF0281cb))
            }
        }
        is SequentialSearchAction.SearchWithAttributes -> {
            OutlinedButton(modifier = Modifier.defaultMinSize(minHeight = 50.dp, minWidth = 270.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = Color(0xFF0281cb)
                ),
                onClick = onClick) {
                Text(stringResource(R.string.search_with_attributes), color = Color(0xFF0281cb))
            }
        }
        is SequentialSearchAction.RegisterNew -> {
            Button(
                text = stringResource(R.string.register_new_patient),
                style = ButtonStyle.TEXT_LIGHT,
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.register_new_patient),
                        tint = Color.White,
                    )
                },
                modifier = Modifier.background(Color(parseColor(defaultButtonColor)))
                    .defaultMinSize(minHeight = 50.dp),
                onClick = onClick,
            )
        }
    }
}
