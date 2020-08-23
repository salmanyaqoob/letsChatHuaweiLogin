package com.mrspd.letschats.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mrspd.letschats.ui.mainActivity.MainActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //show logo till app finish loading
        startActivity(Intent(this, MainActivity::class.java))
        finish()


    }
}
