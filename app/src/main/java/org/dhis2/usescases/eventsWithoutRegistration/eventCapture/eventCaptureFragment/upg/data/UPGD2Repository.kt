package org.dhis2.usescases.eventsWithoutRegistration.eventCapture.eventCaptureFragment.upg.data

import org.dhis2.usescases.eventsWithoutRegistration.eventCapture.eventCaptureFragment.upg.domain.UPGItem
import org.dhis2.usescases.eventsWithoutRegistration.eventCapture.eventCaptureFragment.upg.domain.UPGRepository
import org.hisp.dhis.android.core.D2
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit

class UPGD2Repository(
    private val d2: D2,
) : UPGRepository {

    override fun get(orgUnitUid: String): List<UPGItem> {
        val countryUid = getCountry(orgUnitUid)
        //https://cpr-test.samaritanspurse.org/api/trackedEntityInstances.json?totalPages=true&program=lu4r3oPCg6v&ou=D6eqt1FJ7e8&ouMode=SELECTED&fields=trackedEntityInstance,attributes,enrollments&filter=bCGs7tqWUdd:eq:eligible&pageSize=1000000&skipPaging=false
        val teis =  d2.trackedEntityModule().trackedEntityInstances()
            .withTrackedEntityAttributeValues()
            .byProgramUids(listOf("lu4r3oPCg6v"))
            .byOrganisationUnitUid().eq(countryUid)
            .get()
            .blockingGet()

            val eligibleTEIS = teis
            .filter {
                it.trackedEntityAttributeValues()?.any { attribute ->
                    attribute.trackedEntityAttribute() == "bCGs7tqWUdd" &&
                            attribute.value()!!.lowercase() == "eligible"
                } ?: false
            }

            val upgItems = eligibleTEIS
            .map {
                val upgUid = it.uid()

                val upgName = it.trackedEntityAttributeValues()
                    ?.find { attribute -> attribute.trackedEntityAttribute() == "rn2YpLCqksT" }
                    ?.value() ?: ""

                UPGItem(
                    upgUid,
                    upgName
                )
            }.filter { it.name.isNotEmpty() }

        return upgItems.sortedBy { it.name }
    }

    private fun getCountry(orgUnitUid: String): String {
        var orgUnit : OrganisationUnit? = null
        var parentUid: String = orgUnitUid

        while (orgUnit == null || orgUnit.level() != 4){
            orgUnit = d2.organisationUnitModule().organisationUnits()
                .byUid().eq(parentUid)
                .one().blockingGet()

            parentUid = orgUnit?.parent()?.uid() ?: ""
        }

        return orgUnit?.uid() ?:""
    }
}

