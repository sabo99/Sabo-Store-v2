package com.sabo.sabostorev2.ui.Splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.sabo.sabostorev2.Common.Common
import com.sabo.sabostorev2.Common.Preferences
import com.sabo.sabostorev2.R
import com.sabo.sabostorev2.ui.Home.HomeActivity
import com.sabo.sabostorev2.ui.SignIn.SignIn
import kotlinx.android.synthetic.main.activity_splash.*
import maes.tech.intentanim.CustomIntent

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val animation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        ivLogo.animation = animation

        Handler().postDelayed({
            if (Preferences.getIsLogIn(this))
                startActivity(Intent(this, HomeActivity::class.java))
            else
                startActivity(Intent(this, SignIn::class.java))

            CustomIntent.customType(this, Common.FINFOUT)
            finish()
        }, 2000)
    }
}