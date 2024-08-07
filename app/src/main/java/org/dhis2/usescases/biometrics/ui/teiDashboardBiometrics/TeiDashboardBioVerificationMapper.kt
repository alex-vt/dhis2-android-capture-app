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
        val buttonModel = if (verifyResult == null){
            BioButtonModel(
                text = resourceManager.getString(R.string.biometrics_verification_not_done),
                backgroundColor = defaultButtonColor,
                icon = resourceManager.context.getBioIconSuccess(),
                onActionClick = actionCallback
            )
        } else {
            if (verifyResult == VerifyResult.NoMatch) {
                BioButtonModel(
                    text = resourceManager.context.getString(R.string.retake_biometrics),
                    backgroundColor = defaultButtonColor,
                    icon = R.drawable.ic_bio_fingerprint,
                    onActionClick = actionCallback)
            } else {
                null
            }
        }

        return TeiDashboardBioModel(
            verificationStatusModel = verifyResult?.let {
                BioVerificationStatus(
                    text = getText(verifyResult),
                    backgroundColor = getBackgroundColor(verifyResult),
                    icon = getIcon(verifyResult)
                )
            },
            buttonModel = buttonModel
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
            is VerifyResult.Match -> "#34835d"
            is VerifyResult.NoMatch -> "#e30613"
            is VerifyResult.Failure -> "#a6a5a4"
        }
    }
}
