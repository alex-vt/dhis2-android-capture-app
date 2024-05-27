package org.dhis2.commons.team

import org.hisp.dhis.android.core.D2

private const val teamProfileDataset = "lXtB2FRocsa"
private const val statusDataElement = "I1CBbsdTg3A"

val programsOrDataSetsToValidate = listOf(
    "JsM6wTUTsL6", // End of season report
    "sPRFZ9fP9w3", // Ministry alignment
    "KvYM1NhKCD6", // ministry training
    "rGXtRfPdES9", // monthly reconciliation
    "v0YLrAILuce", // outreach event
    "BVHxsLhrCde", // resource tracking
    "WCJhvPcJomX", // season plan
    "wGzMn208Oh9", // tgj observation form
    "ywS5wi0Y5em"  // vision meeting
)

fun isActiveOrgUnit(
    d2: D2,
    programOrDataSetUid: String,
    orgUnitId: String,
    period: String
): Boolean {
    if (!programsOrDataSetsToValidate.contains(programOrDataSetUid)) {
        return true
    }

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

data class ValidationData(
    val programOrDataSetUid: String,
    val period: String?,
)

fun nonActiveOrgUnits(d2: D2, validationData: ValidationData): List<String> {
    if (!programsOrDataSetsToValidate.contains(validationData.programOrDataSetUid) ||
        validationData.period == null) {
        return listOf()
    }

    val dataValues = d2.dataValueModule().dataValues()
        .byDataSetUid(teamProfileDataset)
        .byPeriod().eq(validationData.period)
        .blockingGet()

    val statusDataValues = dataValues.filter {
        it.dataElement() == statusDataElement && (it.value() ?: "").lowercase() != "active"
    }

    return statusDataValues.map { it.organisationUnit() ?: "" }.filter { it != "" }

}
