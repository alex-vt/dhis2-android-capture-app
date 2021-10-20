package org.dhis2.usescases.biometrics.duplicates

import dagger.Subcomponent

@Subcomponent(modules = [BiometricsDuplicatesDialogModule::class])
interface BiometricsDuplicatesDialogComponent {
    fun inject(biometricsDuplicatesDialog: BiometricsDuplicatesDialog)
}
