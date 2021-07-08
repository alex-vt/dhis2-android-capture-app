package org.dhis2.utils.simprints

import com.simprints.libsimprints.SimHelper

object SimprintsHelper {

    private const val PROJECT_ID = "Ma9wi0IBdo215PKRXOf5"
    private const val USER_ID = "android"

    @JvmField
    val simHelper = SimHelper(PROJECT_ID, USER_ID)
}