package org.dhis2.usescases.biometrics

import org.dhis2.BuildConfig

const val BIOMETRICS_ENABLED = BuildConfig.FLAVOR == "simprints"
const val BIOMETRIC_VALUE = "biometrics"
const val BIOMETRIC_VERIFICATION_VALUE = "biometrics verification"

const val SIMPRINTS_ENROLL_REQUEST = 99
const val SIMPRINTS_IDENTIFY_REQUEST = 199
const val SIMPRINTS_VERIFY_REQUEST = 299
const val BIOMETRICS_FAILURE_PATTERN = "$$$\$BIOMETRICS_FAILED$$$$"

const val BIOMETRICS_GUID = "BIOMETRICS_GUID"
const val BIOMETRICS_VERIFICATION_STATUS = "BIOMETRICS_VERIFICATION_STATUS"

