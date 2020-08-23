package com.mrspd.letschats.util

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.huawei.agconnect.auth.AGConnectAuth

object AuthUtil {

    val firebaseAuthInstance: FirebaseAuth by lazy {
        println("firebaseAuthInstance.:")
        FirebaseAuth.getInstance()
    }

    val huaweiAuthInstance: AGConnectAuth by lazy {
        println("firebaseAuthInstance.:")
        AGConnectAuth.getInstance()
    }


    fun getAuthId(): String {
        val context: Context? = LetsChatActivity.context
        if(HmsGmsUtil.isOnlyHms(context)){
            return huaweiAuthInstance.currentUser?.uid.toString()
        } else {
            return firebaseAuthInstance.currentUser?.uid.toString()
        }
    }
}