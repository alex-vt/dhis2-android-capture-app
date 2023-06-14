package org.dhis2.usescases.biometrics

import android.content.Context
import org.dhis2.R
import org.dhis2.commons.prefs.PreferenceProviderImpl
import org.dhis2.data.biometrics.BiometricsPreference
/*import org.dhis2.data.forms.dataentry.fields.biometrics.BiometricsViewModel
import org.dhis2.data.forms.dataentry.fields.biometricsVerification.BiometricsVerificationViewModel*/
import org.dhis2.form.model.FieldUiModel
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute

fun ProgramTrackedEntityAttribute.isBiometricAttribute(): Boolean {
    return name()?.contains(BIOMETRIC_VALUE, true) ?: false
}

// TODO: simprints
fun FieldUiModel.isBiometricModel(): Boolean {
  //  return this is BiometricsViewModel && this.label.isBiometricText()
    return false
}

fun FieldUiModel.isBiometricsVerificationModel(): Boolean {
   // return this is BiometricsVerificationViewModel && this.label.isBiometricsVerificationText()
    return false
}

fun String.isBiometricText(): Boolean {
    return this.equals(BIOMETRIC_VALUE, true)
}

fun String.isBiometricsVerificationText(): Boolean {
    return this.equals(BIOMETRIC_VERIFICATION_VALUE, true)
}

fun Context.getBioIconBasic() =
    if (getBioIcon(this) == BiometricsIcon.FINGERPRINT) R.drawable.ic_bio_fingerprint
    else R.drawable.ic_bio_face

fun Context.getBioIconSearch() =
    if (getBioIcon(this) == BiometricsIcon.FINGERPRINT) R.drawable.ic_bio_fingerprint_search
    else R.drawable.ic_bio_face_search

fun Context.getBioIconFunnel() =
    if (getBioIcon(this) == BiometricsIcon.FINGERPRINT) R.drawable.ic_bio_fingerprint_funnel
    else R.drawable.ic_bio_face_funnel

fun Context.getBioIconNew() =
    if (getBioIcon(this) == BiometricsIcon.FINGERPRINT) R.drawable.ic_bio_fingerprint_new
    else R.drawable.ic_bio_face_new

fun Context.getBioIconSuccess() =
    if (getBioIcon(this) == BiometricsIcon.FINGERPRINT) R.drawable.ic_bio_fingerprint_success
    else R.drawable.ic_bio_face_success

fun Context.getBioIconFailed() =
    if (getBioIcon(this) == BiometricsIcon.FINGERPRINT) R.drawable.ic_bio_fingerprint_failed
    else R.drawable.ic_bio_face_failed

fun Context.getBioIconWarning() =
    if (getBioIcon(this) == BiometricsIcon.FINGERPRINT) R.drawable.ic_bio_fingerprint_warning_orange
    else R.drawable.ic_bio_face_warning_orange

fun Context.getBioIconNoneOfTheAbove() =
    if (getBioIcon(this) == BiometricsIcon.FINGERPRINT) R.drawable.ic_bio_fingerprint_warning_red
    else R.drawable.ic_bio_face_warning_red

private fun getBioIcon(context: Context):BiometricsIcon{
    val preferences = PreferenceProviderImpl(context)

    val iconText = preferences.getString(
        BiometricsPreference.ICON,
        BiometricsIcon.FINGERPRINT.name
    )!!

    return BiometricsIcon.valueOf(iconText)
}
