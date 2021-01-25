package com.sabo.sabostorev2.ui.Account.Menu

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.sabo.sabostorev2.ui.Account.Menu.SubMenu.ChangePhone
import com.sabo.sabostorev2.ui.Account.Menu.SubMenu.DeleteAccount
import com.sabo.sabostorev2.ui.Account.Menu.SubMenu.ResetPassword
import com.sabo.sabostorev2.ui.Account.Menu.SubMenu.TwoStepVerification.TwoStepVerification
import com.sabo.sabostorev2.Common.Common
import com.sabo.sabostorev2.Common.Preferences
import com.sabo.sabostorev2.R
import maes.tech.intentanim.CustomIntent

class AccountSettings : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        initViews()
    }

    private fun initViews() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Account Settings"

        findViewById<LinearLayout>(R.id.twoStepVerify).setOnClickListener(this)
        findViewById<LinearLayout>(R.id.resetPassword).setOnClickListener(this)
        findViewById<LinearLayout>(R.id.changeNumber).setOnClickListener(this)
        findViewById<LinearLayout>(R.id.deleteAccount).setOnClickListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    override fun finish() {
        super.finish()
        CustomIntent.customType(this, Common.RTL)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.twoStepVerify -> {
                if (Preferences.getIsPIN(this)) {
                    Common.twoStepVerification(this, TwoStepVerification::class.java)
                    CustomIntent.customType(this, Common.LTR)
                } else {
                    startActivity(Intent(this, TwoStepVerification::class.java))
                    CustomIntent.customType(this, Common.LTR)
                }
            }
            R.id.resetPassword -> {
                if(Preferences.getIsPIN(this)){
                    Common.twoStepVerification(this, ResetPassword::class.java)
                    CustomIntent.customType(this, Common.LTR)
                }else{
                    startActivity(Intent(this, ResetPassword::class.java))
                    CustomIntent.customType(this, Common.LTR)
                }

            }
            R.id.changeNumber -> {
                if(Preferences.getIsPIN(this)){
                    Common.twoStepVerification(this, ChangePhone::class.java)
                    CustomIntent.customType(this, Common.LTR)
                }else{
                    startActivity(Intent(this, ChangePhone::class.java))
                    CustomIntent.customType(this, Common.LTR)
                }
            }
            R.id.deleteAccount -> {
                if(Preferences.getIsPIN(this)){
                    Common.twoStepVerification(this, DeleteAccount::class.java)
                    CustomIntent.customType(this, Common.LTR)
                }else{
                    startActivity(Intent(this, DeleteAccount::class.java))
                    CustomIntent.customType(this, Common.LTR)
                }
            }
        }
    }
}