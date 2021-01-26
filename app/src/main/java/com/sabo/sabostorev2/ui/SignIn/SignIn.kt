@file:Suppress("DEPRECATION")

package com.sabo.sabostorev2.ui.SignIn

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import com.sabo.sabostorev2.API.APIRequestData
import com.sabo.sabostorev2.Common.Common
import com.sabo.sabostorev2.Common.Preferences
import com.sabo.sabostorev2.Model.ResponseModel
import com.sabo.sabostorev2.R
import com.sabo.sabostorev2.ui.Home.HomeActivity
import com.sabo.sabostorev2.ui.SignUp.SignUp
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_in.etPassword
import kotlinx.android.synthetic.main.activity_sign_in.progressBar
import kotlinx.android.synthetic.main.activity_sign_in.tilPassword
import kotlinx.android.synthetic.main.dialog_forgot_password.*
import maes.tech.intentanim.CustomIntent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignIn : AppCompatActivity(), View.OnClickListener {

    private var mService: APIRequestData? = null
    private var state = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        mService = Common.getAPI()
        initViews()
    }

    private fun initViews() {
        findViewById<Button>(R.id.btnSignIn).setOnClickListener(this)
        findViewById<TextView>(R.id.tvSignUp).setOnClickListener(this)
        findViewById<TextView>(R.id.tvForgotPassword).setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnSignIn -> signIn()
            R.id.tvSignUp -> {
                clearColorFields()
                startActivity(Intent(this, SignUp::class.java))
                CustomIntent.customType(this, Common.LTR)
            }
            R.id.tvForgotPassword -> forgotPassword()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun clearColorFields() {
        etEmailUsername.clearFocus()
        etPassword.clearFocus()
        etEmailUsername.setText("")
        etPassword.setText("")
        tilEmailUsername.isHelperTextEnabled = false
        tilPassword.isHelperTextEnabled = false
        etEmailUsername.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.colorAccent))
        etPassword.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.colorAccent))
        tilEmailUsername.hintTextColor = ColorStateList.valueOf(resources.getColor(R.color.colorAccent))
        tilPassword.hintTextColor = ColorStateList.valueOf(resources.getColor(R.color.colorAccent))
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun signIn() {
        val emailUsername = etEmailUsername.text.toString()
        val password = etPassword.text.toString()

        if (emailUsername.isNullOrEmpty()) {
            etEmailUsername.error = "Email or Username is required"
        } else if (password.length < 6) {
            tilPassword.isHelperTextEnabled = true
            tilPassword.helperText = "Password too short (Minimal 6 digit)"
            tilPassword.setHelperTextColor(ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_dark)))
        } else {
            tilPassword.isHelperTextEnabled = false
            tilPassword.setHelperTextColor(ColorStateList.valueOf(resources.getColor(R.color.colorAccent)))

            btnSignIn.isEnabled = false
            progressBar.visibility = View.VISIBLE

            mService!!.signIn(emailUsername, emailUsername, password).enqueue(object : Callback<ResponseModel> {
                override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                    val code = response.body()!!.code
                    val message = response.body()!!.message

                    btnSignIn.isEnabled = true
                    progressBar.visibility = View.INVISIBLE

                    if (code == 1) {
                        etEmailUsername.requestFocus()
                        etEmailUsername.backgroundTintList = ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_dark))
                        tilEmailUsername.isHelperTextEnabled = true
                        tilEmailUsername.helperText = message
                        tilEmailUsername.setHelperTextColor(ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_dark)))
                        tilEmailUsername.hintTextColor = ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_dark))
                    } else if (code == 2) {
                        tilEmailUsername.isHelperTextEnabled = false
                        tilEmailUsername.setHelperTextColor(ColorStateList.valueOf(resources.getColor(R.color.colorAccent)))
                        tilEmailUsername.hintTextColor = ColorStateList.valueOf(resources.getColor(R.color.colorAccent))

                        etPassword.requestFocus()
                        etPassword.backgroundTintList = ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_dark))
                        tilPassword.isHelperTextEnabled = true
                        tilPassword.helperText = message
                        tilPassword.setHelperTextColor(ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_dark)))
                        tilPassword.hintTextColor = ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_dark))
                    } else if (code == 3 || code == 4) {
                        clearColorFields()

                        val sweet = SweetAlertDialog(this@SignIn, SweetAlertDialog.WARNING_TYPE)
                        sweet.titleText = "Oops!"
                        sweet.contentText = message
                        sweet.show()
                    } else if (code == 5) {
                        val userModel = response.body()!!.user
                        Preferences.setUID(this@SignIn, userModel.uid)
                        Preferences.setIsLogIn(this@SignIn, true)
                        startActivity(Intent(this@SignIn, HomeActivity::class.java))
                        CustomIntent.customType(this@SignIn, Common.FINFOUT)
                        finish()
                        clearColorFields()
                    } else if (code == 6) {
                        val userModel = response.body()!!.user
                        val i = Intent(this@SignIn, SignInPIN::class.java)
                        i.putExtra("uid", userModel.uid)
                        startActivity(i)
                        CustomIntent.customType(this@SignIn, Common.FINFOUT)
                        clearColorFields()
                    }
                }

                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                    clearColorFields()
                    btnSignIn.isEnabled = true
                    progressBar.visibility = View.INVISIBLE

                    val sweet = SweetAlertDialog(this@SignIn, SweetAlertDialog.WARNING_TYPE)
                    sweet.titleText = "Oops!"
                    sweet.contentText = t.message
                    sweet.show()
                }
            })
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun forgotPassword() {
        state = 0
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_forgot_password, null)
        val llEmail = view.findViewById(R.id.llEmail) as LinearLayout
        val llPassword = view.findViewById(R.id.llPassword) as LinearLayout
        val tilEmail = view.findViewById(R.id.tilEmail) as TextInputLayout
        val tilPassword = view.findViewById(R.id.tilPassword) as TextInputLayout
        val tilRetypePassword = view.findViewById(R.id.tilRetypePassword) as TextInputLayout
        val etEmail = view.findViewById(R.id.etEmail) as EditText
        val etPassword = view.findViewById(R.id.etPassword) as EditText
        val etRetypePassword = view.findViewById(R.id.etRetypePassword) as EditText
        val btnContinue = view.findViewById(R.id.btnContinue) as Button
        val progressBar = view.findViewById(R.id.progressBar) as ProgressBar

        val sweetForgotPassword = SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
        sweetForgotPassword.titleText = "Forgot password!"
        sweetForgotPassword.setCustomImage(R.drawable.logo_forgot)
        sweetForgotPassword.setOnShowListener {

            btnContinue.setOnClickListener {
                val email = etEmail.text.toString()
                /** State 0 */
                if (state == 0) {
                    if (email.isEmpty()) {
                        tilEmail.isHelperTextEnabled = true
                        tilEmail.helperText = "Email is required"
                        tilEmail.setHelperTextColor(ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_dark)))
                        tilEmail.hintTextColor = ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_dark))
                        etEmail.backgroundTintList = ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_dark))
                        etEmail.requestFocus()
                    } else {
                        btnContinue.isEnabled = false
                        progressBar.visibility = View.VISIBLE
                        Handler().postDelayed({
                            mService!!.checkEmailExist(email).enqueue(object : Callback<ResponseModel> {
                                override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                                    val code = response.body()!!.code
                                    val message = response.body()!!.message
                                    btnContinue.isEnabled = true
                                    progressBar.visibility = View.INVISIBLE
                                    if (code == 0) {
                                        tilEmail.setHelperTextColor(ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_dark)))
                                        tilEmail.helperText = message
                                    }
                                    if (code == 1) {
                                        btnContinue.text = "Change password"
                                        val animation1 = AnimationUtils.loadAnimation(this@SignIn, R.anim.slide_to_left)
                                        llEmail.animation = animation1
                                        llEmail.visibility = View.GONE

                                        val animation2 = AnimationUtils.loadAnimation(this@SignIn, R.anim.slide_from_right_to_left)
                                        llPassword.animation = animation2
                                        llPassword.visibility = View.VISIBLE

                                        /** Change State */
                                        state = 1
                                    }
                                }

                                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                                    btnContinue.isEnabled = true
                                    progressBar.visibility = View.INVISIBLE
                                    val sweet = SweetAlertDialog(this@SignIn, SweetAlertDialog.ERROR_TYPE)
                                    sweet.titleText = "Oops!"
                                    sweet.contentText = t.message
                                    sweet.show()
                                }
                            })
                        }, 1000)
                    }
                }

                /** State = 1 */
                if (state == 1) {
                    val password = etPassword.text.toString()
                    val retype = etRetypePassword.text.toString()
                    if (password.length < 6) {
                        tilPassword.isHelperTextEnabled = true
                        tilPassword.helperText = "Password too short (Minimal 6 digit)"
                        tilPassword.setHelperTextColor(ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_dark)))
                        tilPassword.hintTextColor = ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_dark))
                        etPassword.backgroundTintList = ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_dark))
                        etPassword.requestFocus()
                    } else if (retype != password) {
                        tilPassword.isHelperTextEnabled = false
                        tilPassword.setHelperTextColor(ColorStateList.valueOf(resources.getColor(R.color.colorAccent)))
                        etPassword.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.colorAccent))

                        tilRetypePassword.isHelperTextEnabled = true
                        tilRetypePassword.helperText = "Password not matched"
                        tilRetypePassword.setHelperTextColor(ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_dark)))
                        tilRetypePassword.hintTextColor = ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_dark))
                        etRetypePassword.backgroundTintList = ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_dark))
                        etRetypePassword.requestFocus()
                    } else {
                        tilPassword.isHelperTextEnabled = false
                        tilPassword.setHelperTextColor(ColorStateList.valueOf(resources.getColor(R.color.colorAccent)))
                        etPassword.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.colorAccent))

                        tilRetypePassword.isHelperTextEnabled = false
                        tilRetypePassword.setHelperTextColor(ColorStateList.valueOf(resources.getColor(R.color.colorAccent)))
                        etRetypePassword.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.colorAccent))

                        btnContinue.isEnabled = false
                        progressBar.visibility = View.VISIBLE
                        Handler().postDelayed({
                            mService!!.forgotPassword(email, password).enqueue(object : Callback<ResponseModel> {
                                override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                                    val code = response.body()!!.code
                                    val message = response.body()!!.message
                                    btnContinue.isEnabled = true
                                    progressBar.visibility = View.INVISIBLE
                                    if (code == 0) {
                                        val sweet = SweetAlertDialog(this@SignIn, SweetAlertDialog.WARNING_TYPE)
                                        sweet.titleText = "Oops!"
                                        sweet.contentText = message
                                        sweet.show()
                                    }
                                    if (code == 1) {
                                        /** Change State */
                                        state = 0
                                        val sweet = SweetAlertDialog(this@SignIn, SweetAlertDialog.SUCCESS_TYPE)
                                        sweet.titleText = "Success!"
                                        sweet.contentText = message
                                        sweet.setConfirmClickListener {
                                            sweet.dismissWithAnimation()
                                            sweetForgotPassword.dismissWithAnimation()
                                        }
                                        sweet.show()
                                    }
                                }

                                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                                    btnContinue.isEnabled = true
                                    progressBar.visibility = View.INVISIBLE
                                    val sweet = SweetAlertDialog(this@SignIn, SweetAlertDialog.ERROR_TYPE)
                                    sweet.titleText = "Oops!"
                                    sweet.contentText = t.message
                                    sweet.show()
                                }
                            })
                        }, 1000)
                    }
                }
            }
        }
        sweetForgotPassword.setCanceledOnTouchOutside(false)
        sweetForgotPassword.show()

        val linearLayout = sweetForgotPassword.findViewById(R.id.loading) as LinearLayout
        val confirm = sweetForgotPassword.findViewById(R.id.confirm_button) as Button
        confirm.visibility = View.GONE
        val index = linearLayout.indexOfChild(linearLayout.findViewById(R.id.content_text))
        linearLayout.addView(view, index + 1)
    }

    override fun onBackPressed() {
        if (state == 0 || state == -1) super.onBackPressed()
        if (state == 1){
            val animation1 = AnimationUtils.loadAnimation(this, R.anim.slide_to_right)
            llPassword.animation = animation1
            llPassword.visibility = View.GONE

            val animation2 = AnimationUtils.loadAnimation(this, R.anim.slide_from_left_to_right)
            llEmail.animation = animation2
            llEmail.visibility = View.VISIBLE

            state = 0
        }
    }
}