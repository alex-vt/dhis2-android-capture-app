package org.dhis2.usescases.biometrics.ui.teiDashboardBiometrics

import org.dhis2.R
import org.dhis2.commons.biometrics.getBioIconFailed
import org.dhis2.commons.biometrics.getBioIconSuccess
import org.dhis2.commons.resources.ResourceManager
import org.dhis2.data.biometrics.RegisterResult
import org.dhis2.usescases.biometrics.ui.defaultButtonColor
import org.dhis2.usescases.biometrics.ui.failedButtonColor
import org.dhis2.usescases.biometrics.ui.successButtonColor

class TeiDashboardBioRegistrationMapper(
    val resourceManager: ResourceManager,
) {
    fun map(
        registerResult: RegisterResult?,
        actionCallback: () -> Unit,
    ): TeiDashboardBioModel {
        return TeiDashboardBioModel(
            verificationStatusModel = null,
            buttonModel = BioButtonModel(
                text = getText(registerResult),
                backgroundColor = getBackgroundColor(registerResult),
                icon = getIcon(registerResult),
                onActionClick = actionCallback
            )
        )
    }

    private fun getText(
        registerResult: RegisterResult?,
    ): String {
        return if (registerResult == null) {
            resourceManager.getString(R.string.enroll_biometrics)
        } else {
            when (registerResult) {
                is RegisterResult.Completed ->
                    resourceManager.getString(R.string.biometrics_completed)

                is RegisterResult.Failure ->
                    resourceManager.getString(R.string.biometrics_declined)

                is RegisterResult.PossibleDuplicates ->
                    resourceManager.getString(R.string.biometrics_declined)

                is RegisterResult.AgeGroupNotSupported -> resourceManager.getString(R.string.age_group_not_supported)
            }
        }
    }

    private fun getIcon(
        registerResult: RegisterResult?,
    ): Int {
        return if (registerResult == null) {
            R.drawable.ic_bio_fingerprint
        } else {
            when (registerResult) {
                is RegisterResult.Completed -> resourceManager.context.getBioIconSuccess()
                is RegisterResult.Failure -> resourceManager.context.getBioIconFailed()
                is RegisterResult.PossibleDuplicates -> resourceManager.context.getBioIconFailed()
                is RegisterResult.AgeGroupNotSupported -> resourceManager.context.getBioIconFailed()
            }
        }
    }

    private fun getBackgroundColor(
        registerResult: RegisterResult?,
    ): String {
        if (registerResult == null) {
            return defaultButtonColor
        } else {
            return when (registerResult) {
                is RegisterResult.Completed -> successButtonColor
                is RegisterResult.Failure -> failedButtonColor
                is RegisterResult.PossibleDuplicates -> failedButtonColor
                is RegisterResult.AgeGroupNotSupported -> failedButtonColor
            }
        }
    }
}