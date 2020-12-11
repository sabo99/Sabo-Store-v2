package com.sabo.sabostorev2.Account.Menu.SubMenu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.sabo.sabostorev2.Common.Common
import com.sabo.sabostorev2.R
import maes.tech.intentanim.CustomIntent

class ResetPassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        initViews()
    }

    private fun initViews() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Reset Password"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                CustomIntent.customType(this, Common.RTL)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun finish() {
        super.finish()
        CustomIntent.customType(this, Common.RTL)
    }
}