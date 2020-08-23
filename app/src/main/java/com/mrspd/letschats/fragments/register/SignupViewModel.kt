package com.mrspd.letschats.fragments.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.google.firebase.auth.FirebaseAuth
import com.huawei.agconnect.auth.AGConnectAuth
import com.huawei.agconnect.auth.EmailUser
import com.mrspd.letschats.models.User
import com.mrspd.letschats.util.AuthUtil
import com.mrspd.letschats.util.ErrorMessage
import com.mrspd.letschats.util.FirestoreUtil
import com.mrspd.letschats.util.LoadState


class SignupViewModel : ViewModel() {

    val navigateToHomeMutableLiveData = MutableLiveData<Boolean?>()
    val loadingState = MutableLiveData<LoadState>()


    fun registerEmail(
        auth: FirebaseAuth,
        email: String,
        password: String,
        username: String
    ) {

        loadingState.value = LoadState.LOADING

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                storeUserInFirestore(User(it.user?.uid, username, email))
            }.addOnFailureListener {
                ErrorMessage.errorMessage = it.message
                loadingState.value = LoadState.FAILURE
            }

    }

    fun registerEmailHuawei(
        auth: AGConnectAuth,
        email: String,
        password: String,
        username: String,
        verificationCode: String
    ) {

        loadingState.value = LoadState.LOADING

        val emailUser =
            EmailUser.Builder()
                .setEmail(email)
                .setVerifyCode(verificationCode)
                .setPassword(password)
                // Optional. If this parameter is set, the current user has created a password and can use the password to sign in.
                // If this parameter is not set, the user can only sign in using a verification code.
                .build()

        auth.createUser(emailUser)
            .addOnCompleteListener { auth ->
                if (auth.isSuccessful) {
                    val currentUserId = AuthUtil.huaweiAuthInstance.currentUser.uid
                    storeUserInFirestore(User(currentUserId, username, email))
                } else {
                    ErrorMessage.errorMessage = auth.exception.message
                    loadingState.value = LoadState.FAILURE
                }
            }
    }


    fun storeUserInFirestore(user: User) {
        val db = FirestoreUtil.firestoreInstance
        user.uid?.let { uid ->
            db.collection("users").document(uid).set(user).addOnSuccessListener {
                navigateToHomeMutableLiveData.value = true
            }.addOnFailureListener {
                loadingState.value = LoadState.FAILURE
                ErrorMessage.errorMessage = it.message
            }
        }

    }


    fun doneNavigating() {
        navigateToHomeMutableLiveData.value = null
    }

}