package org.dhis2.data.biometrics

import org.dhis2.usescases.biometrics.entities.BiometricsTEI
import org.dhis2.usescases.biometrics.entities.BiometricsTEIRelationShip
import org.dhis2.usescases.biometrics.repositories.BiometricsTEIRepository
import org.hisp.dhis.android.core.D2
import org.hisp.dhis.android.core.relationship.RelationshipItem
import org.hisp.dhis.android.core.relationship.RelationshipItemTrackedEntityInstance

class BiometricsTEIRepositoryImpl(
    private val d2: D2
) : BiometricsTEIRepository {

    override fun getByUid(uid: String): BiometricsTEI? {
        val teiResult =
            d2.trackedEntityModule().trackedEntityInstances()
                .byUid().eq(uid).blockingGet()

        if (teiResult.isEmpty()){
            return null
        } else {
            val tei =  teiResult.first()

            val relationships = d2.relationshipModule().relationships().getByItem(
                RelationshipItem.builder().trackedEntityInstance(
                    RelationshipItemTrackedEntityInstance.builder().trackedEntityInstance(uid)
                        .build()
                ).build()
            )

            return BiometricsTEI(
                uid = tei.uid(),
                relationships = relationships.map {
                    BiometricsTEIRelationShip(
                        relationshipTypeUid = it.relationshipType()!!,
                        fromUid = it.from()?.elementUid() ?: "",
                        toUid = it.to()?.elementUid() ?: ""
                    )
                } ?: listOf()
            )
        }
    }
}