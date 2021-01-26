@file:Suppress("DEPRECATION")

package com.sabo.sabostorev2.ui.SignUp

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import com.sabo.sabostorev2.API.APIRequestData
import com.sabo.sabostorev2.Common.Common
import com.sabo.sabostorev2.Common.Preferences
import com.sabo.sabostorev2.Model.ResponseModel
import com.sabo.sabostorev2.R
import com.sabo.sabostorev2.ui.Home.HomeActivity
import kotlinx.android.synthetic.main.activity_sign_up.*
import maes.tech.intentanim.CustomIntent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUp : AppCompatActivity(), View.OnClickListener {

    private var mService: APIRequestData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        mService = Common.getAPI()
        initViews()
    }

    private fun initViews() {
        findViewById<Button>(R.id.btnSignUp).setOnClickListener(this)
        findViewById<TextView>(R.id.tvSignIn).setOnClickListener(this)
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnSignUp -> signUp()
            R.id.tvSignIn -> finish()
            R.id.btnBack -> finish()
        }
    }

    private fun signUp() {
        val email = etEmail.text.toString()
        val username = etUsername.text.toString()
        val password = etPassword.text.toString()

        if (username.isNullOrEmpty()) {
            etUsername.error = "Username is required"
        } else if (email.isNullOrEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.isHelperTextEnabled = true
            tilEmail.helperText = "Invalid email format"
            tilEmail.setHelperTextColor(ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_dark)))
        } else if (password.length < 6) {
            tilEmail.isHelperTextEnabled = false
            tilPassword.isHelperTextEnabled = true
            tilPassword.helperText = "Password too short (Minimal 6 digit)"
            tilPassword.setHelperTextColor(ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_dark)))
        } else {
            tilEmail.isHelperTextEnabled = false
            tilPassword.isHelperTextEnabled = false

            progressBar.visibility = View.VISIBLE
            btnSignUp.isEnabled = false

            mService!!.signUp(email, username, password).enqueue(object : Callback<ResponseModel> {
                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                    val code = response.body()!!.code
                    val message = response.body()!!.message
                    progressBar.visibility = View.INVISIBLE
                    btnSignUp.isEnabled = true
                    if (code == 1) {
                        etUsername.requestFocus()
                        etUsername.backgroundTintList = ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_dark))
                        tilUsername.isHelperTextEnabled = true
                        tilUsername.helperText = message
                        tilUsername.setHelperTextColor(ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_dark)))
                        tilUsername.hintTextColor = ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_dark))
                    } else if (code == 2) {
                        tilUsername.isHelperTextEnabled = false
                        tilUsername.setHelperTextColor(ColorStateList.valueOf(resources.getColor(R.color.colorAccent)))
                        tilUsername.hintTextColor = ColorStateList.valueOf(resources.getColor(R.color.colorAccent))

                        etEmail.requestFocus()
                        etEmail.backgroundTintList = ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_dark))
                        tilEmail.isHelperTextEnabled = true
                        tilEmail.helperText = message
                        tilEmail.setHelperTextColor(ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_dark)))
                        tilEmail.hintTextColor = ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_dark))
                    } else if (code == 3 || code == 4) {
                        clearColorFields()
                        val sweet = SweetAlertDialog(this@SignUp, SweetAlertDialog.WARNING_TYPE)
                        sweet.titleText = "Oops!"
                        sweet.contentText = message
                        sweet.show()
                    } else {
                        clearColorFields()
                        val userModel = response.body()!!.user
                        Preferences.setUID(this@SignUp, userModel.uid)
                        Preferences.setIsLogIn(this@SignUp, true)

                        val sweet = SweetAlertDialog(this@SignUp, SweetAlertDialog.SUCCESS_TYPE)
                        sweet.titleText = "Success!"
                        sweet.contentText = message
                        sweet.setConfirmClickListener {
                            sweet.dismissWithAnimation()
                            startActivity(Intent(this@SignUp, HomeActivity::class.java))
                            CustomIntent.customType(this@SignUp, Common.FINFOUT)
                            finish()
                        }
                        sweet.show()
                    }
                }

                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                    progressBar.visibility = View.INVISIBLE
                    btnSignUp.isEnabled = true
                    val sweet = SweetAlertDialog(this@SignUp, SweetAlertDialog.WARNING_TYPE)
                    sweet.titleText = "Oops!"
                    sweet.contentText = t.message
                    sweet.show()
                }
            })
        }
    }

    private fun clearColorFields() {
        etUsername.setHintTextColor(ColorStateList.valueOf(resources.getColor(R.color.colorAccent)))
        etEmail.setHintTextColor(ColorStateList.valueOf(resources.getColor(R.color.colorAccent)))
        tilUsername.setHelperTextColor(ColorStateList.valueOf(resources.getColor(R.color.colorAccent)))
        tilEmail.setHelperTextColor(ColorStateList.valueOf(resources.getColor(R.color.colorAccent)))
        tilUsername.hintTextColor = ColorStateList.valueOf(resources.getColor(R.color.colorAccent))
        tilEmail.hintTextColor = ColorStateList.valueOf(resources.getColor(R.color.colorAccent))
        tilUsername.isHelperTextEnabled = false
        tilEmail.isHelperTextEnabled = false
    }

    override fun finish() {
        super.finish()
        CustomIntent.customType(this, Common.RTL)
    }
}