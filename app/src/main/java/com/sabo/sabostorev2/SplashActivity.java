package com.sabo.sabostorev2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.sabo.sabostorev2.API.APIRequestData;
import com.sabo.sabostorev2.Common.Common;
import com.sabo.sabostorev2.Common.Preferences;

public class SplashActivity extends AppCompatActivity {

    private ImageView ivLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ivLogo = findViewById(R.id.ivLogo);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        ivLogo.setAnimation(animation);

        new Handler().postDelayed(() -> {
            if (Preferences.getIsLogIn(this))
                startActivity(new Intent(this, HomeActivity.class));
            else
                startActivity(new Intent(this, SignInActivity.class));
            finish();
        }, 2000);
    }
}