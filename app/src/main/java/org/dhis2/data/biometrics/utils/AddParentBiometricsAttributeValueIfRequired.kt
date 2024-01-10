package org.dhis2.data.biometrics.utils

import org.dhis2.commons.prefs.BasicPreferenceProvider
import org.dhis2.usescases.teiDashboard.TeiAttributesProvider
import org.hisp.dhis.android.core.D2
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue

fun addParentBiometricsAttributeValueIfRequired(
    d2: D2,
    teiAttributesProvider: TeiAttributesProvider,
    basicPreferenceProvider: BasicPreferenceProvider,
    attributeValues: MutableList<TrackedEntityAttributeValue>,
    programUid: String,
    teiUid: String
) {
    val trackedEntityAttributeValue = getParentBiometricsAttributeValueIfRequired(
        d2,
        teiAttributesProvider,
        basicPreferenceProvider,
        attributeValues,
        programUid,
        teiUid
    )

    if (trackedEntityAttributeValue != null) {
        attributeValues.add(trackedEntityAttributeValue)
    }
}

