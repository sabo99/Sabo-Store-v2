@file:Suppress("DEPRECATION")

package com.sabo.sabostorev2.ui.Account.Menu.SubMenu

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import com.sabo.sabostorev2.API.APIRequestData
import com.sabo.sabostorev2.Common.Common
import com.sabo.sabostorev2.Common.Preferences
import com.sabo.sabostorev2.Model.ResponseModel
import com.sabo.sabostorev2.R
import kotlinx.android.synthetic.main.activity_reset_password.*
import maes.tech.intentanim.CustomIntent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResetPassword : AppCompatActivity(), View.OnClickListener {

    var mService: APIRequestData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        mService = Common.getAPI()
        initViews()
    }

    private fun initViews() {
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener(this)
        findViewById<Button>(R.id.btnConfirm).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnBack -> finish()
            R.id.btnConfirm -> resetPassword()
        }
    }

    private fun resetPassword() {
        val uid = Preferences.getUID(this)
        val newPassword = etPassword.text.toString()
        val cPassword = etCPassword.text.toString()

        if (newPassword.isEmpty() || newPassword.length < 6) {
            tilPassword.isHelperTextEnabled = true
            tilPassword.helperText = "Password too short (Min.6)"
            tilPassword.setHelperTextColor(ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_dark)))
        } else if (cPassword != newPassword) {
            tilPassword.isHelperTextEnabled = false
            tilCPassword.isHelperTextEnabled = true
            tilCPassword.helperText = "Password does not match"
            tilCPassword.setHelperTextColor(ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_dark)))
        } else {
            tilPassword.isHelperTextEnabled = false
            tilCPassword.isHelperTextEnabled = false

            val sweetLoading = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            sweetLoading.progressHelper.barColor = resources.getColor(R.color.colorAccent)
            sweetLoading.setTitleText("Please wait...").setCanceledOnTouchOutside(false)
            sweetLoading.show()

            mService!!.resetPassword(uid, newPassword).enqueue(object : Callback<ResponseModel> {
                override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                    val code = response.body()!!.code
                    val message = response.body()!!.message

                    if (code == 1) {
                        sweetLoading.dismissWithAnimation()
                        SweetAlertDialog(this@ResetPassword, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Oops!")
                                .setContentText(message)
                                .show()
                    }
                    if (code == 2) {
                        etPassword.setText("")
                        etCPassword.setText("")
                        Handler().postDelayed({
                            sweetLoading.dismissWithAnimation()
                            SweetAlertDialog(this@ResetPassword, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Success!")
                                    .setContentText(message)
                                    .show()
                        }, 2000)
                    }
                }

                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                    sweetLoading.dismissWithAnimation()
                    SweetAlertDialog(this@ResetPassword, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Oops!")
                            .setContentText(t.message)
                            .show()
                }
            })
        }
    }

    override fun finish() {
        super.finish()
        CustomIntent.customType(this, Common.RTL)
    }
}