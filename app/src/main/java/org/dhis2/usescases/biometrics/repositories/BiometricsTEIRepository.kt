package org.dhis2.usescases.biometrics.repositories

import org.dhis2.usescases.biometrics.entities.BiometricsTEI

interface BiometricsTEIRepository {
    fun getByUid(uid:String): BiometricsTEI?
}