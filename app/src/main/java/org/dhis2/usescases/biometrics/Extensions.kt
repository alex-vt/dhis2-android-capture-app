package org.dhis2.usescases.biometrics

import org.dhis2.data.forms.dataentry.fields.biometrics.BiometricsViewModel
import org.dhis2.form.model.FieldUiModel
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute

fun ProgramTrackedEntityAttribute.isBiometricAttribute(): Boolean {
    return name()?.contains(BIOMETRIC_VALUE, true) ?:false
}

fun FieldUiModel.isBiometricModel(): Boolean {
    return this is BiometricsViewModel && this.label.isBiometricText()
}

fun String.isBiometricText(): Boolean {
    return this.equals(BIOMETRIC_VALUE, true)
}

