package org.dhis2.usescases.biometrics.ui.teiDashboardBiometrics

import org.dhis2.R
import org.dhis2.commons.biometrics.getBioIconFailed
import org.dhis2.commons.biometrics.getBioIconSuccess
import org.dhis2.commons.biometrics.getBioIconWarning
import org.dhis2.commons.resources.ResourceManager
import org.dhis2.data.biometrics.VerifyResult
import org.dhis2.usescases.biometrics.ui.defaultButtonColor

class TeiDashboardBioVerificationMapper(
    val resourceManager: ResourceManager,
) {
    fun map(
        verifyResult: VerifyResult?,
        actionCallback: () -> Unit,
    ): TeiDashboardBioModel {
        return TeiDashboardBioModel(
            verificationStatusModel = verifyResult?.let {
                BioVerificationStatus(
                    text = getText(verifyResult),
                    backgroundColor = getBackgroundColor(verifyResult),
                    icon = getIcon(verifyResult)
                )
            },
            buttonModel = BioButtonModel(
                text = if (verifyResult == null)
                    resourceManager.getString(R.string.biometrics_verification_not_done)
                else resourceManager.context.getString(R.string.retake_biometrics),
                backgroundColor = defaultButtonColor,
                icon = R.drawable.ic_bio_fingerprint,
                onActionClick = actionCallback
            )
        )
    }

    private fun getText(
        verifyResult: VerifyResult
    ): String {
        return when (verifyResult) {
            is VerifyResult.Match ->
                resourceManager.getString(R.string.biometrics_verified)

            is VerifyResult.NoMatch ->
                resourceManager.getString(R.string.verification_failed)

            is VerifyResult.Failure ->
                resourceManager.getString(R.string.verification_declined)
        }
    }

    private fun getIcon(
        verifyResult: VerifyResult
    ): Int {
        return when (verifyResult) {
            is VerifyResult.Match -> resourceManager.context.getBioIconSuccess()
            is VerifyResult.NoMatch -> resourceManager.context.getBioIconFailed()
            is VerifyResult.Failure -> resourceManager.context.getBioIconWarning()
        }
    }

    private fun getBackgroundColor(
        verifyResult: VerifyResult
    ): String {
        return when (verifyResult) {
            is VerifyResult.Match -> "#FF35835D"
            is VerifyResult.NoMatch -> "#a14545"
            is VerifyResult.Failure -> "#C57704"
        }
    }
}
