package org.dhis2.usescases.eventsWithoutRegistration.eventCapture.eventCaptureFragment.upg.domain

private const val UPG_OTHER = "Other"

class GetUPGItems(private val upgRepository: UPGRepository) {
    operator fun invoke(orgUnitUid: String): List<UPGItem> {
        return upgRepository.get(orgUnitUid) + listOf(UPGItem(guid = "", name = UPG_OTHER))
    }
}