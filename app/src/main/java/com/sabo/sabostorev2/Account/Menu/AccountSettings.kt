package com.sabo.sabostorev2.Account.Menu

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.sabo.sabostorev2.Account.Menu.SubMenu.ChangeEmail
import com.sabo.sabostorev2.Account.Menu.SubMenu.DeleteAccount
import com.sabo.sabostorev2.Account.Menu.SubMenu.ResetPassword
import com.sabo.sabostorev2.Common.Common
import com.sabo.sabostorev2.R
import kotlinx.android.synthetic.main.activity_account_settings.*
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

        findViewById<LinearLayout>(R.id.resetPassword).setOnClickListener(this)
        findViewById<LinearLayout>(R.id.changeEmail).setOnClickListener(this)
        findViewById<LinearLayout>(R.id.deleteAccount).setOnClickListener(this)
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

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.resetPassword -> {
                startActivity(Intent(this, ResetPassword::class.java))
                CustomIntent.customType(this, Common.LTR)
            }
            R.id.changeEmail -> {
                startActivity(Intent(this, ChangeEmail::class.java))
                CustomIntent.customType(this, Common.LTR)
            }
            R.id.deleteAccount -> {
                startActivity(Intent(this, DeleteAccount::class.java))
                CustomIntent.customType(this, Common.LTR)
            }
        }
    }
}