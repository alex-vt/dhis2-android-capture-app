package org.dhis2.usescases.eventsWithoutRegistration.eventCapture.eventCaptureFragment.upg.domain

class GetUPGItems(private val upgRepository: UPGRepository) {
    operator fun invoke(orgUnitUid: String): List<UPGItem> {
        return upgRepository.get(orgUnitUid)
    }
}