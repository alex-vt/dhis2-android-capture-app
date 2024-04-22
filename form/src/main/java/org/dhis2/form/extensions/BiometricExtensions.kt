package org.dhis2.form.extensions

const val BIOMETRIC_VALUE = "biometrics"

fun String.isBiometricText(): Boolean {
    return this.replace(":","").equals(BIOMETRIC_VALUE, true)
}