package org.dhis2.usescases.biometrics.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.dhis2.R



@Composable
internal fun SequentialSearchAction(
    onClick: (() -> Unit),
) {
    OutlinedButton(modifier = Modifier.width(270.dp).height(50.dp),
        border = BorderStroke(
            width = 1.dp,
            color = Color(0xFF0281cb)
        ),
        onClick = onClick) {
        Text(stringResource(R.string.search_with_attributes), color = Color(0xFF0281cb))
    }
}
