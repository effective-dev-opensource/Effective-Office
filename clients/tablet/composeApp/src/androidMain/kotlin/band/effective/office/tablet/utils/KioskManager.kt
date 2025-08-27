package band.effective.office.tablet.utils

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.Context
import band.effective.office.tablet.AdminReceiver

class KioskManager(private val context: Context) {

    private val devicePolicyManager: DevicePolicyManager =
        context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

    private val adminComponent = AdminReceiver.Companion.getComponentName(context)
    private val packageName = context.packageName


    fun enableKioskMode(activity: Activity) {
        if (!devicePolicyManager.isDeviceOwnerApp(packageName)) return
        devicePolicyManager.setLockTaskPackages(adminComponent, arrayOf(packageName))
        activity.startLockTask()
    }

    fun disableKioskMode(activity: Activity) {
        if (!devicePolicyManager.isDeviceOwnerApp(packageName)) return
        activity.stopLockTask()
    }
}