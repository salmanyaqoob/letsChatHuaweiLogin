package com.mrspd.letschats.fragments.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.huawei.agconnect.auth.AGConnectAuth
import com.huawei.agconnect.auth.AGConnectAuthCredential
import com.huawei.agconnect.auth.EmailAuthProvider
import com.mrspd.letschats.util.ErrorMessage
import com.mrspd.letschats.util.LoadState
import java.util.regex.Matcher
import java.util.regex.Pattern


class LoginViewModel : ViewModel() {


    private val loadingState = MutableLiveData<LoadState>()

    val emailMatch = MutableLiveData<Boolean>()

    private val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)\$"

    fun isEmailFormatCorrect(it: String): LiveData<Boolean> {

        val pattern: Pattern = Pattern.compile(emailRegex)
        val matcher: Matcher = pattern.matcher(it)
        emailMatch.value = matcher.matches()

        return emailMatch
    }


    fun login(auth: FirebaseAuth, email: String, password: String): LiveData<LoadState> {
        loadingState.value = LoadState.LOADING

        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            loadingState.value = LoadState.SUCCESS
        }.addOnFailureListener {
            ErrorMessage.errorMessage = it.message
            loadingState.value = LoadState.FAILURE
        }
        return loadingState
    }

    fun loginHuawei(auth: AGConnectAuth, email: String, password: String): LiveData<LoadState> {
        loadingState.value = LoadState.LOADING

        val credential: AGConnectAuthCredential =
            EmailAuthProvider.credentialWithPassword(email, password)

        auth.signIn(credential).addOnSuccessListener {
            loadingState.value = LoadState.SUCCESS
        }.addOnFailureListener {
            ErrorMessage.errorMessage = it.message
            loadingState.value = LoadState.FAILURE
        }
        return loadingState
    }

    fun doneNavigating() {
        loadingState.value = null
    }

}
