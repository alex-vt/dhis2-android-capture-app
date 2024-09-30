package org.dhis2.data.biometrics.utils

import org.hisp.dhis.android.core.D2

fun isTeiInNoOtherProgram(d2: D2, teiUid: String?, programUid: String?): Boolean =
    d2.enrollmentModule().enrollments()
        .byTrackedEntityInstance().eq(teiUid)
        .byProgram().neq(programUid)
        .blockingCount() == 0
