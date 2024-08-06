package org.dhis2.usescases.biometrics

import androidx.compose.material.Icon
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import org.dhis2.R
import org.dhis2.form.extensions.isBiometricText
import org.dhis2.usescases.biometrics.ui.bioMatchKey
import org.hisp.dhis.mobile.ui.designsystem.component.AdditionalInfoItem
import org.hisp.dhis.mobile.ui.designsystem.component.AdditionalInfoItemColor
import kotlin.math.ceil

fun addAttrBiometricsIconIfRequired(additionalInfo: List<AdditionalInfoItem>): List<AdditionalInfoItem> {
    return if (BIOMETRICS_ENABLED) additionalInfo.map {
        val key = it.key ?: ""

        if (key.isBiometricText()) {
            it.copy(value = "", icon = {
                Icon(
                    painter = if (it.value.isNotBlank() && it.value != "-") painterResource(R.drawable.ic_bio_available_yes)
                    else painterResource(R.drawable.ic_bio_available_no),
                    tint = Color.Unspecified,
                    contentDescription = null,
                )
            })
        } else {
            it
        }

    } else additionalInfo
}

fun addAttrBiometricsEmojiIfRequired(additionalInfo: List<AdditionalInfoItem>): List<AdditionalInfoItem> {
    return if (BIOMETRICS_ENABLED) additionalInfo.map {
        val key = it.key ?: ""

        if (key.isBiometricText()) {
            if (it.value.isNotBlank() && it.value != "-") {
                it.copy(value = "✔\uFE0F", isConstantItem = true)
            } else {
                it.copy(value = "❌", isConstantItem = true)
            }
        } else {
            it
        }

    } else additionalInfo
}

fun addBiometricsConfidentScoreIfRequired(additionalInfo: List<AdditionalInfoItem>): List<AdditionalInfoItem> {
    return if (BIOMETRICS_ENABLED) additionalInfo.map {
        val key = it.key ?: ""

        if (key == "$bioMatchKey:") {
            val value = ceil( it.value.toFloat() / 20).toInt()
            val startValue = "★".repeat(value) + "☆".repeat(5 - value)
            it.copy(value = startValue, isConstantItem = true, color = AdditionalInfoItemColor.WARNING.color)
        } else {
            it
        }

    } else additionalInfo
}