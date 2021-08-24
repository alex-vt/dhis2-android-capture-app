package org.dhis2.usescases.biometrics

import org.dhis2.data.forms.dataentry.fields.biometrics.BiometricsViewModel
import org.dhis2.data.forms.dataentry.fields.biometricsVerification.BiometricsVerificationViewModel
import org.dhis2.form.model.FieldUiModel
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute

fun ProgramTrackedEntityAttribute.isBiometricAttribute(): Boolean {
    return name()?.contains(BIOMETRIC_VALUE, true) ?:false
}

fun FieldUiModel.isBiometricModel(): Boolean {
    return this is BiometricsViewModel && this.label.isBiometricText()
}

fun FieldUiModel.isBiometricsVerificationModel(): Boolean {
    return this is BiometricsVerificationViewModel && this.label.isBiometricsVerificationText()
}

fun String.isBiometricText(): Boolean {
    return this.equals(BIOMETRIC_VALUE, true)
}

fun String.isBiometricsVerificationText(): Boolean {
    return this.equals(BIOMETRIC_VERIFICATION_VALUE, true)
}

