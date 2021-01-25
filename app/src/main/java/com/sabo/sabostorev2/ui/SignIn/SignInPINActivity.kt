package com.sabo.sabostorev2.ui.SignIn

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import com.sabo.sabostorev2.API.APIRequestData
import com.sabo.sabostorev2.Common.Common
import com.sabo.sabostorev2.Common.Preferences
import com.sabo.sabostorev2.Model.ResponseModel
import com.sabo.sabostorev2.R
import com.sabo.sabostorev2.ui.Home.HomeActivity
import kotlinx.android.synthetic.main.activity_sign_in_pin.*
import maes.tech.intentanim.CustomIntent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInPINActivity : AppCompatActivity(), View.OnClickListener {

    private var mService: APIRequestData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_pin)

        mService = Common.getAPI()
        initViews()
    }

    override fun finish() {
        super.finish()
        CustomIntent.customType(this, Common.RTL)
    }

    private fun initViews() {
        supportActionBar!!.title = "Two-step verification"

        findViewById<Button>(R.id.btnConfirm).setOnClickListener(this)

        btnConfirm.isEnabled = false
        cvConfirm.setCardBackgroundColor(ColorStateList.valueOf(resources.getColor(R.color.colorPrimaryDark)))
        etPIN.requestFocus()
        etPIN.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkPIN(s)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun checkPIN(s: CharSequence?) {
        if (s.toString().length < 6) {
            btnConfirm.isEnabled = false
            cvConfirm.setCardBackgroundColor(ColorStateList.valueOf(resources.getColor(R.color.colorPrimaryDark)))
        } else {
            btnConfirm.isEnabled = true
            cvConfirm.setCardBackgroundColor(ColorStateList.valueOf(resources.getColor(R.color.colorAccent)))
            Handler().postDelayed({
                confirmSignInWithPIN()
            }, 1000)
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnConfirm -> {
                confirmSignInWithPIN()
            }
        }
    }

    private fun confirmSignInWithPIN() {
        val uid = intent.getStringExtra("uid")
        val PIN = etPIN!!.text.toString()
        if (PIN.isNullOrBlank()) {
            tilPIN.isHelperTextEnabled = true
            tilPIN.helperText = "Please input PIN"
            tilPIN.setHelperTextColor(ColorStateList.valueOf(resources.getColor(R.color.red)))
        } else {
            tilPIN.isHelperTextEnabled = false

            val sweetLoading = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            sweetLoading.progressHelper.barColor = resources.getColor(R.color.colorAccent)
            sweetLoading.titleText = "Please wait..."
            sweetLoading.setCanceledOnTouchOutside(false)
            sweetLoading.show()

            mService!!.signInWithPIN(uid, PIN).enqueue(object : Callback<ResponseModel> {
                override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                    val code = response.body()!!.code
                    val message = response.body()!!.message

                    if (code == 4) {
                        val userModel = response.body()!!.user
                        Preferences.setUID(this@SignInPINActivity, userModel.uid)
                        Preferences.setIsLogIn(this@SignInPINActivity, true)
                        Preferences.setIsPIN(this@SignInPINActivity, true)

                        Handler().postDelayed({
                            sweetLoading.dismissWithAnimation()
                            startActivity(Intent(this@SignInPINActivity, HomeActivity::class.java))
                            finish()
                        }, 1000)
                    } else
                        sweetLoading.setTitleText("Oops!")
                                .setContentText(message)
                                .setConfirmText("Close")
                                .setConfirmClickListener {
                                    sweetLoading.dismissWithAnimation()
                                }.changeAlertType(SweetAlertDialog.WARNING_TYPE)

                }

                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                    sweetLoading.setTitleText("Oops!")
                            .setContentText(t.message)
                            .setConfirmText("Close")
                            .setConfirmClickListener {
                                sweetLoading.dismissWithAnimation()
                            }.changeAlertType(SweetAlertDialog.WARNING_TYPE)
                }
            })
        }
    }
}