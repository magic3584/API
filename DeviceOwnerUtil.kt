package com.example.cabinet

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.UserManager
import android.util.Log

class DeviceOwnerUtil {

    companion object {
        private const val TAG = "DeviceOwnerUtil"

        fun setDeviceOwner(context: Context) {
            val adminComponent = ComponentName(context, AdminReceiver::class.java) // 替换为你的 DeviceAdminReceiver 类名

            if (isDeviceOwner(context, adminComponent)) {
                Log.d(TAG, "Already device owner")
            } else {
                val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Enable device admin for management")
                context.startActivity(intent)
            }

            if (isDeviceOwner(context, adminComponent)) {
                Log.d(TAG, "Setting device owner")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    setLockTaskPackages(context, adminComponent)
                }
                setUserRestrictions(context, adminComponent)
            }
        }

        private fun isDeviceOwner(context: Context, adminComponent: ComponentName): Boolean {
            val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            return devicePolicyManager.isDeviceOwnerApp(context.packageName)
        }

        private fun setLockTaskPackages(context: Context, adminComponent: ComponentName) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
                devicePolicyManager.setLockTaskPackages(adminComponent, arrayOf(context.packageName))
            }
        }

        private fun setUserRestrictions(context: Context, adminComponent: ComponentName) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
                val userHandle = android.os.Process.myUserHandle()
                devicePolicyManager.addUserRestriction(adminComponent, UserManager.DISALLOW_FACTORY_RESET)
                devicePolicyManager.addUserRestriction(adminComponent, UserManager.DISALLOW_ADD_USER)
                devicePolicyManager.addUserRestriction(adminComponent, UserManager.DISALLOW_REMOVE_USER)
                devicePolicyManager.addUserRestriction(adminComponent, UserManager.DISALLOW_CONFIG_WIFI)
                // 添加其他需要的用户限制
            }
        }

        fun clearDeviceOwner(context: Context) {
            val adminComponent = ComponentName(context, AdminReceiver::class.java) // 替换为你的 DeviceAdminReceiver 类名
            val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            devicePolicyManager.clearUserRestriction(adminComponent,UserManager.DISALLOW_FACTORY_RESET)
            devicePolicyManager.clearUserRestriction(adminComponent, UserManager.DISALLOW_ADD_USER)
            devicePolicyManager.clearUserRestriction(adminComponent, UserManager.DISALLOW_REMOVE_USER)
            devicePolicyManager.clearUserRestriction(adminComponent, UserManager.DISALLOW_CONFIG_WIFI)
        }
    }
}
