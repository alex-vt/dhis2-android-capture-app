package org.dhis2.commons.team

import org.hisp.dhis.android.core.D2

private const val teamProfileDataset = "lXtB2FRocsa"
private const val statusDataElement = "I1CBbsdTg3A"
fun isActiveOrgUnit(d2: D2, orgUnitId: String, period: String): Boolean {
    val dataValues = d2.dataValueModule().dataValues()
        .byDataSetUid(teamProfileDataset)
        .byOrganisationUnitUid().eq(orgUnitId)
        .byPeriod().eq(period).blockingGet()

    val statusDataValue = dataValues.find { it.dataElement() == statusDataElement }

    return if (statusDataValue?.value() != null) {
        statusDataValue.value()!!.lowercase() == "active"
    } else {
        true
    }
}

fun nonActiveOrgUnits(d2: D2, period: String): List<String> {
    val dataValues = d2.dataValueModule().dataValues()
        .byDataSetUid(teamProfileDataset)
        .byPeriod().eq(period)
        .blockingGet()

    val statusDataValues = dataValues.filter {
        it.dataElement() == statusDataElement && (it.value() ?: "").lowercase() != "active"
    }

    return statusDataValues.map { it.organisationUnit() ?: "" }.filter { it != "" }

}
