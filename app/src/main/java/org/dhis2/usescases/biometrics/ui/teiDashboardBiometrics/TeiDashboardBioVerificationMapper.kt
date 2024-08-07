package org.dhis2.usescases.biometrics.ui.teiDashboardBiometrics

import org.dhis2.R
import org.dhis2.commons.biometrics.getBioIconFailed
import org.dhis2.commons.biometrics.getBioIconSuccess
import org.dhis2.commons.biometrics.getBioIconWarning
import org.dhis2.commons.resources.ResourceManager
import org.dhis2.data.biometrics.VerifyResult

class TeiDashboardBioVerificationMapper(
    val resourceManager: ResourceManager,
) {
    fun map(
        verifyResult: VerifyResult?,
        actionCallback: () -> Unit,
    ): TeiDashboardBioModel {
        return TeiDashboardBioModel(
            text = getText(verifyResult),
            backgroundColor = getBackgroundColor(verifyResult),
            icon = getIcon(verifyResult) ,
            onActionClick = actionCallback
        )
    }

    private fun getText(
        verifyResult: VerifyResult?
    ): String {
        if (verifyResult == null) {
            return resourceManager.getString(R.string.biometrics_verification_not_done)
        } else {
            return when (verifyResult) {
                is VerifyResult.Match ->
                    resourceManager.getString(R.string.biometrics_verification_match)

                is VerifyResult.NoMatch ->
                    resourceManager.getString(R.string.biometrics_verification_no_match)

                is VerifyResult.Failure ->
                    resourceManager.getString(R.string.biometrics_verification_failed)
            }
        }
    }

    private fun getIcon(
        verifyResult: VerifyResult?
    ):Int {
        if (verifyResult == null) {
            return resourceManager.context.getBioIconSuccess()
        } else {
            return when (verifyResult) {
                is VerifyResult.Match ->resourceManager.context.getBioIconSuccess()
                is VerifyResult.NoMatch ->resourceManager.context.getBioIconFailed()
                is VerifyResult.Failure ->resourceManager.context.getBioIconWarning()
            }
        }
    }

    private fun getBackgroundColor(
        verifyResult: VerifyResult?
    ): String {
        if (verifyResult == null) {
            return "#4d4d4d"
        } else {
            return when (verifyResult) {
                is VerifyResult.Match -> "#FF35835D"
                is VerifyResult.NoMatch ->"#a14545"
                is VerifyResult.Failure ->"#C57704"
            }
        }
    }
}
