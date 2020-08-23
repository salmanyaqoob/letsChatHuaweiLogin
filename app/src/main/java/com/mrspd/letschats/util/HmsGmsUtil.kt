package com.mrspd.letschats.util

import android.content.Context
import android.util.Log
import com.google.android.gms.common.GoogleApiAvailability
import com.huawei.hms.api.ConnectionResult
import com.huawei.hms.api.HuaweiApiAvailability

object HmsGmsUtil {
    private const val TAG = "HmsGmsUtil"

    /**
     * Whether the HMS service on the device is available.
     *
     * @param context android context
     * @return true:HMS service is available; false:HMS service is not available;
     */
    fun isHmsAvailable(context: Context?): Boolean {
        var isAvailable = false
        if (null != context) {
            val result =
                HuaweiApiAvailability.getInstance().isHuaweiMobileServicesAvailable(context)
            isAvailable = ConnectionResult.SUCCESS == result
        }
        Log.i(TAG, "isHmsAvailable: $isAvailable")
        return isAvailable
    }

    /**
     * Whether the GMS service on the device is available.
     *
     * @param context android context
     * @return true:GMS service is available; false:GMS service is not available;
     */
    fun isGmsAvailable(context: Context?): Boolean {
        var isAvailable = false
        if (null != context) {
            val result = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)
            isAvailable = com.google.android.gms.common.ConnectionResult.SUCCESS == result
        }
        Log.i(TAG, "isGmsAvailable: $isAvailable")
        return isAvailable
    }

    fun isOnlyHms(context: Context?): Boolean {
        return isHmsAvailable(context) && !isGmsAvailable(context)
    }
}
