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
        val buttonModel = if (verifyResult == null) {
            BioButtonModel(
                text = resourceManager.getString(R.string.biometrics_verification_not_done),
                backgroundColor = null,
                icon = resourceManager.context.getBioIconSuccess(),
                onActionClick = actionCallback
            )
        } else {
            if (verifyResult == VerifyResult.NoMatch) {
                BioButtonModel(
                    text = resourceManager.context.getString(R.string.reverify_biometrics),
                    backgroundColor = null,
                    icon = R.drawable.ic_bio_fingerprint,
                    onActionClick = actionCallback
                )
            } else {
                null
            }
        }

        return TeiDashboardBioModel(
            statusModel = verifyResult?.let {
                BioStatus(
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

            is VerifyResult.AgeGroupNotSupported -> resourceManager.getString(R.string.age_group_not_supported)
        }
    }

    private fun getIcon(
        verifyResult: VerifyResult
    ): Int {
        return when (verifyResult) {
            is VerifyResult.Match -> resourceManager.context.getBioIconSuccess()
            is VerifyResult.NoMatch -> resourceManager.context.getBioIconFailed()
            is VerifyResult.Failure -> resourceManager.context.getBioIconWarning()
            is VerifyResult.AgeGroupNotSupported -> resourceManager.context.getBioIconFailed()
        }
    }

    private fun getBackgroundColor(
        verifyResult: VerifyResult
    ): String {
        return when (verifyResult) {
            is VerifyResult.Match -> "#34835d"
            is VerifyResult.NoMatch -> "#e30613"
            is VerifyResult.Failure -> "#a6a5a4"
            is VerifyResult.AgeGroupNotSupported -> "#a6a5a4"
        }
    }
}
