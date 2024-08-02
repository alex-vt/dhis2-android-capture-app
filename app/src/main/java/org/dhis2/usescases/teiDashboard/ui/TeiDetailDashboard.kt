package org.dhis2.usescases.teiDashboard.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.dhis2.R
import org.dhis2.form.extensions.isBiometricText
import org.dhis2.usescases.biometrics.BIOMETRICS_ENABLED
import org.dhis2.commons.data.EventCreationType
import org.dhis2.usescases.teiDashboard.ui.model.InfoBarUiModel
import org.dhis2.usescases.teiDashboard.ui.model.TeiBiometricsVerificationModel
import org.dhis2.usescases.teiDashboard.ui.model.TeiCardUiModel
import org.hisp.dhis.mobile.ui.designsystem.component.AdditionalInfoItem
import org.dhis2.usescases.teiDashboard.ui.model.TimelineEventsHeaderModel
import org.hisp.dhis.mobile.ui.designsystem.component.CardDetail
import org.hisp.dhis.mobile.ui.designsystem.component.InfoBar
import org.hisp.dhis.mobile.ui.designsystem.component.InfoBarData
import org.hisp.dhis.mobile.ui.designsystem.theme.Spacing

@Composable
fun TeiDetailDashboard(
    syncData: InfoBarUiModel?,
    followUpData: InfoBarUiModel?,
    enrollmentData: InfoBarUiModel?,
    card: TeiCardUiModel?,
    timelineEventHeaderModel: TimelineEventsHeaderModel,
    isGrouped: Boolean = true,
    timelineOnEventCreationOptionSelected: (EventCreationType) -> Unit,
    verification: TeiBiometricsVerificationModel?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
    ) {
        if (syncData?.showInfoBar == true) {
            InfoBar(
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .testTag(SYNC_INFO_BAR_TEST_TAG),
                infoBarData =
                InfoBarData(
                    text = syncData.text,
                    icon = syncData.icon,
                    color = syncData.textColor,
                    backgroundColor = syncData.backgroundColor,
                    actionText = syncData.actionText,
                    onClick = syncData.onActionClick,
                ),
            )
            if (followUpData?.showInfoBar == true || enrollmentData?.showInfoBar == true) {
                Spacer(modifier = Modifier.padding(top = 8.dp))
            }
        }

        if (followUpData?.showInfoBar == true) {
            InfoBar(
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .testTag(FOLLOWUP_INFO_BAR_TEST_TAG),
                infoBarData = InfoBarData(
                    text = followUpData.text,
                    icon = followUpData.icon,
                    color = followUpData.textColor,
                    backgroundColor = followUpData.backgroundColor,
                    actionText = followUpData.actionText,
                    onClick = followUpData.onActionClick,
                ),
            )
            if (enrollmentData?.showInfoBar == true) {
                Spacer(modifier = Modifier.padding(top = 8.dp))
            }
        }

        if (enrollmentData?.showInfoBar == true) {
            InfoBar(
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .testTag(STATE_INFO_BAR_TEST_TAG),
                infoBarData = InfoBarData(
                    text = enrollmentData.text,
                    icon = enrollmentData.icon,
                    color = enrollmentData.textColor,
                    backgroundColor = enrollmentData.backgroundColor,
                    actionText = enrollmentData.actionText,
                ),
            )
        }

        card?.let {
            // EyeSeeTea customization
            val additionalInfoList = mapBiometricAttrInAdditionalInfo(card.additionalInfo)

            CardDetail(
                title = card.title,
                additionalInfoList = additionalInfoList,
                avatar = card.avatar,
                actionButton = card.actionButton,
                expandLabelText = card.expandLabelText,
                shrinkLabelText = card.shrinkLabelText,
                showLoading = card.showLoading,
            )
        }

        if (!isGrouped) {
            Spacer(modifier = Modifier.size(Spacing.Spacing16))
            TimelineEventsHeader(
                timelineEventsHeaderModel = timelineEventHeaderModel,
                onOptionSelected = timelineOnEventCreationOptionSelected,
            )
            Spacer(modifier = Modifier.size(Spacing.Spacing8))
        }

        if (verification != null){
            TeiVerificationButton(verification)
        }
    }
}

const val SYNC_INFO_BAR_TEST_TAG = "sync"
const val FOLLOWUP_INFO_BAR_TEST_TAG = "followUp"
const val STATE_INFO_BAR_TEST_TAG = "state"

private fun mapBiometricAttrInAdditionalInfo(additionalInfo: List<AdditionalInfoItem>): List<AdditionalInfoItem> {
    return if (BIOMETRICS_ENABLED) additionalInfo.map {
        val key = it.key ?: ""

        if (key.isBiometricText()) {
            it.copy(value = "", icon = {
                Icon(
                    painter = if (it.value.isNotBlank()) painterResource(R.drawable.ic_bio_available_yes)
                    else painterResource(R.drawable.ic_bio_available_no),
                    tint = Color.Unspecified,
                    contentDescription = null,
                )
            })
        } else {
            it
        }

    } else additionalInfo
}


