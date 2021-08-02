package org.dhis2.data.biometrics

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.simprints.libsimprints.SimHelper
import org.dhis2.usescases.biometrics.SIMPRINTS_ENROLL_REQUEST
import org.dhis2.usescases.biometrics.SIMPRINTS_IDENTIFY_REQUEST
import org.dhis2.usescases.biometrics.SIMPRINTS_VERIFY_REQUEST
import timber.log.Timber

object BiometricsClient {

    private const val PROJECT_ID = "Ma9wi0IBdo215PKRXOf5"
    private const val USER_ID = "android"
    private const val MODULE_ID = "MODULE ID"

    @JvmField
    val simHelper = SimHelper(PROJECT_ID, USER_ID)

    fun register(activity: Activity) {
        val intent = simHelper.register(MODULE_ID)

        if (checkSimprintsApp(activity, intent)) {
            activity.startActivityForResult(intent, SIMPRINTS_ENROLL_REQUEST)
        }
    }

    fun identify(activity: Activity) {
        val intent = simHelper.identify(MODULE_ID)

        if (checkSimprintsApp(activity, intent)) {
            activity.startActivityForResult(intent, SIMPRINTS_IDENTIFY_REQUEST)
        }
    }

    fun verify(activity: Activity, guid: String) {
        if (guid == null) {
            Timber.i("Simprints Verification - Guid is Null - Please check again!")
            return
        }

        val intent = simHelper.verify(MODULE_ID, guid)

        if (checkSimprintsApp(activity, intent)) {
            activity.startActivityForResult(intent, SIMPRINTS_VERIFY_REQUEST)
        }
    }

    fun verify(fragment: Fragment, guid: String) {
        if (guid == null) {
            Timber.i("Simprints Verification - Guid is Null - Please check again!")
            return
        }

        val intent = simHelper.verify(MODULE_ID, guid)

        if (fragment.context != null && checkSimprintsApp(fragment.requireContext(), intent)) {
            fragment.startActivityForResult(intent, SIMPRINTS_VERIFY_REQUEST)
        }
    }

    private fun checkSimprintsApp(context: Context, intent: Intent): Boolean {
        val manager: PackageManager = context.packageManager
        val info = manager.queryIntentActivities(intent, 0)
        return if (info.size > 0) {
            true
        } else {
            Toast.makeText(context, "Please download simprints app!", Toast.LENGTH_SHORT).show()
            false
        }
    }
}