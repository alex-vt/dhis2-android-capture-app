package org.dhis2.usescases.eventsWithoutRegistration.eventCapture.eventCaptureFragment.upg.domain

interface UPGRepository {
    fun get(orgUnitUid: String): List<UPGItem>
}