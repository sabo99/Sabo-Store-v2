@file:Suppress("DEPRECATION")

package com.sabo.sabostorev2.ui.SignIn

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
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
import kotlinx.android.synthetic.main.activity_sign_in.tvPassword
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

        val text1 = Common.getColoredSpanned("Don't have an account?", "")
        val text2 = Common.getColoredSpanned("Sign Up", "#33B5E5")
        tvSignUp.text = Html.fromHtml("$text1 $text2")

        etEmailUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                clearColorFieldEmailUsername()
                if (s.toString().isNotEmpty()) tvEmailUsername.visibility = View.VISIBLE
                else tvEmailUsername.visibility = View.GONE
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearColorFieldEmailUsername()
                if (s.toString().isNotEmpty()) tvEmailUsername.visibility = View.VISIBLE
                else tvEmailUsername.visibility = View.GONE
            }

            override fun afterTextChanged(s: Editable?) {
                clearColorFieldEmailUsername()
                if (s.toString().isNotEmpty()) tvEmailUsername.visibility = View.VISIBLE
                else tvEmailUsername.visibility = View.GONE
            }
        })
        etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                clearColorFieldPassword()
                if (s.toString().isNotEmpty()) tvPassword.visibility = View.VISIBLE
                else tvPassword.visibility = View.GONE
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearColorFieldPassword()
                if (s.toString().isNotEmpty()) tvPassword.visibility = View.VISIBLE
                else tvPassword.visibility = View.GONE
            }

            override fun afterTextChanged(s: Editable?) {
                clearColorFieldPassword()
                if (s.toString().isNotEmpty()) tvPassword.visibility = View.VISIBLE
                else tvPassword.visibility = View.GONE
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnSignIn -> signIn()
            R.id.tvSignUp -> {
                clearAllOptionFields()
                startActivity(Intent(this, SignUp::class.java))
                CustomIntent.customType(this, Common.LTR)
            }
            R.id.tvForgotPassword -> forgotPassword()
        }
    }

    private fun clearAllOptionFields() {
        etEmailUsername.clearFocus()
        etPassword.clearFocus()
        etEmailUsername.setText("")
        etPassword.setText("")

        clearAllColorField()
    }

    private fun clearAllColorField() {
        clearColorFieldEmailUsername()
        clearColorFieldPassword()
    }

    private fun clearColorFieldEmailUsername() {
        tilEmailUsername.isHelperTextEnabled = false
        etEmailUsername.background = resources.getDrawable(R.drawable.border_accent)
    }

    private fun clearColorFieldPassword() {
        tilPassword.isHelperTextEnabled = false
        etPassword.background = resources.getDrawable(R.drawable.border_accent)
    }

    private fun signIn() {
        clearAllColorField()

        val emailUsername = etEmailUsername.text.toString()
        val password = etPassword.text.toString()

        if (emailUsername.isEmpty()) {
            etEmailUsername.requestFocus()
            etEmailUsername.background = resources.getDrawable(R.drawable.border_danger)
            tilEmailUsername.isHelperTextEnabled = true
            tilEmailUsername.helperText = "Email or Username is required"
            tilEmailUsername.setHelperTextColor(ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_dark)))
        } else if (password.length < 6) {
            clearColorFieldEmailUsername()

            etPassword.requestFocus()
            etPassword.background = resources.getDrawable(R.drawable.border_danger)
            tilPassword.isHelperTextEnabled = true
            tilPassword.helperText = "Password too short (Minimal 6 digit)"
            tilPassword.setHelperTextColor(ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_dark)))
        } else {
            clearColorFieldEmailUsername()
            clearColorFieldPassword()

            btnSignIn.isEnabled = false
            progressBar.visibility = View.VISIBLE
            cvSignIn.setCardBackgroundColor(ColorStateList.valueOf(resources.getColor(R.color.colorPrimaryDark)))

            mService!!.signIn(emailUsername, emailUsername, password).enqueue(object : Callback<ResponseModel> {
                override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                    val code = response.body()!!.code
                    val message = response.body()!!.message

                    btnSignIn.isEnabled = true
                    progressBar.visibility = View.INVISIBLE
                    cvSignIn.setCardBackgroundColor(ColorStateList.valueOf(resources.getColor(R.color.colorAccent)))

                    if (code == 1) {
                        etEmailUsername.requestFocus()
                        etEmailUsername.background = resources.getDrawable(R.drawable.border_danger)
                        tilEmailUsername.isHelperTextEnabled = true
                        tilEmailUsername.helperText = message
                        tilEmailUsername.setHelperTextColor(ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_dark)))
                    } else if (code == 2) {
                        clearColorFieldEmailUsername()

                        etPassword.requestFocus()
                        etPassword.background = resources.getDrawable(R.drawable.border_danger)
                        tilPassword.isHelperTextEnabled = true
                        tilPassword.helperText = message
                        tilPassword.setHelperTextColor(ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_dark)))
                    } else if (code == 3 || code == 4) {
                        clearAllColorField()

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
                        clearAllOptionFields()
                    } else if (code == 6) {
                        val userModel = response.body()!!.user
                        val i = Intent(this@SignIn, SignInPIN::class.java)
                        i.putExtra("uid", userModel.uid)
                        startActivity(i)
                        CustomIntent.customType(this@SignIn, Common.FINFOUT)
                        clearAllOptionFields()
                    }
                }

                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                    clearAllColorField()
                    btnSignIn.isEnabled = true
                    progressBar.visibility = View.INVISIBLE
                    cvSignIn.setCardBackgroundColor(ColorStateList.valueOf(resources.getColor(R.color.colorAccent)))

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
        val tvEmail = view.findViewById(R.id.tvEmail) as TextView
        val tvPassword = view.findViewById(R.id.tvPassword) as TextView
        val tvRetypePassword = view.findViewById(R.id.tvRetypePassword) as TextView
        val llEmail = view.findViewById(R.id.llEmail) as LinearLayout
        val llPassword = view.findViewById(R.id.llPassword) as LinearLayout
        val tilEmail = view.findViewById(R.id.tilEmail) as TextInputLayout
        val tilPassword = view.findViewById(R.id.tilPassword) as TextInputLayout
        val tilRetypePassword = view.findViewById(R.id.tilRetypePassword) as TextInputLayout
        val etEmail = view.findViewById(R.id.etEmail) as EditText
        val etPassword = view.findViewById(R.id.etPassword) as EditText
        val etRetypePassword = view.findViewById(R.id.etRetypePassword) as EditText
        val btnContinue = view.findViewById(R.id.btnContinue) as Button
        val cvContinue = view.findViewById(R.id.cvContinue) as CardView
        val progressBar = view.findViewById(R.id.progressBar) as ProgressBar

        val sweetForgotPassword = SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
        sweetForgotPassword.titleText = "Forgot password!"
        sweetForgotPassword.setCustomImage(R.drawable.logo_forgot)
        sweetForgotPassword.setOnShowListener {

            etEmail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    tilEmail.isHelperTextEnabled = false
                    etEmail.background = resources.getDrawable(R.drawable.border_accent)
                    if (s.toString().isNotEmpty()) tvEmail.visibility = View.VISIBLE
                    else tvEmail.visibility = View.GONE
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    tilEmail.isHelperTextEnabled = false
                    etEmail.background = resources.getDrawable(R.drawable.border_accent)
                    if (s.toString().isNotEmpty()) tvEmail.visibility = View.VISIBLE
                    else tvEmail.visibility = View.GONE
                }

                override fun afterTextChanged(s: Editable?) {
                    tilEmail.isHelperTextEnabled = false
                    etEmail.background = resources.getDrawable(R.drawable.border_accent)
                    if (s.toString().isNotEmpty()) tvEmail.visibility = View.VISIBLE
                    else tvEmail.visibility = View.GONE
                }
            })

            etPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    tilPassword.isHelperTextEnabled = false
                    etPassword.background = resources.getDrawable(R.drawable.border_accent)
                    if (s.toString().isNotEmpty()) tvPassword.visibility = View.VISIBLE
                    else tvPassword.visibility = View.GONE
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    tilPassword.isHelperTextEnabled = false
                    etPassword.background = resources.getDrawable(R.drawable.border_accent)
                    if (s.toString().isNotEmpty()) tvPassword.visibility = View.VISIBLE
                    else tvPassword.visibility = View.GONE
                }

                override fun afterTextChanged(s: Editable?) {
                    tilPassword.isHelperTextEnabled = false
                    etPassword.background = resources.getDrawable(R.drawable.border_accent)
                    if (s.toString().isNotEmpty()) tvPassword.visibility = View.VISIBLE
                    else tvPassword.visibility = View.GONE
                }
            })

            etRetypePassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    tilRetypePassword.isHelperTextEnabled = false
                    etRetypePassword.background = resources.getDrawable(R.drawable.border_accent)
                    if (s.toString().isNotEmpty()) tvRetypePassword.visibility = View.VISIBLE
                    else tvRetypePassword.visibility = View.GONE
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    tilRetypePassword.isHelperTextEnabled = false
                    etRetypePassword.background = resources.getDrawable(R.drawable.border_accent)
                    if (s.toString().isNotEmpty()) tvRetypePassword.visibility = View.VISIBLE
                    else tvRetypePassword.visibility = View.GONE
                }

                override fun afterTextChanged(s: Editable?) {
                    tilRetypePassword.isHelperTextEnabled = false
                    etRetypePassword.background = resources.getDrawable(R.drawable.border_accent)
                    if (s.toString().isNotEmpty()) tvRetypePassword.visibility = View.VISIBLE
                    else tvRetypePassword.visibility = View.GONE
                }
            })

            btnContinue.setOnClickListener {
                val email = etEmail.text.toString()
                /** State 0 */
                if (state == 0) {
                    if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        tilEmail.isHelperTextEnabled = true
                        tilEmail.helperText = "Invalid E-mail format"
                        tilEmail.setHelperTextColor(ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_dark)))
                        etEmail.background = resources.getDrawable(R.drawable.border_danger)
                        etEmail.requestFocus()
                    } else {
                        btnContinue.isEnabled = false
                        tilEmail.isHelperTextEnabled = false
                        etEmail.background = resources.getDrawable(R.drawable.border_accent)
                        progressBar.visibility = View.VISIBLE
                        cvContinue.setCardBackgroundColor(ColorStateList.valueOf(resources.getColor(R.color.colorPrimaryDark)))
                        Handler().postDelayed({
                            mService!!.checkEmailExist(email).enqueue(object : Callback<ResponseModel> {
                                override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                                    val code = response.body()!!.code
                                    val message = response.body()!!.message
                                    btnContinue.isEnabled = true
                                    progressBar.visibility = View.INVISIBLE
                                    cvContinue.setCardBackgroundColor(ColorStateList.valueOf(resources.getColor(R.color.colorPrimary)))
                                    if (code == 0) {
                                        etEmail.requestFocus()
                                        etEmail.background = resources.getDrawable(R.drawable.border_danger)
                                        tilEmail.isHelperTextEnabled = true
                                        tilEmail.setHelperTextColor(ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_dark)))
                                        tilEmail.helperText = message
                                    }
                                    if (code == 1) {
                                        tilEmail.isHelperTextEnabled = false
                                        etEmail.background = resources.getDrawable(R.drawable.border_accent)

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
                                    cvContinue.setCardBackgroundColor(ColorStateList.valueOf(resources.getColor(R.color.colorPrimary)))
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
                        etPassword.background = resources.getDrawable(R.drawable.border_danger)
                        etPassword.requestFocus()
                    } else if (retype != password) {
                        tilPassword.isHelperTextEnabled = false
                        etPassword.background = resources.getDrawable(R.drawable.border_accent)

                        tilRetypePassword.isHelperTextEnabled = true
                        tilRetypePassword.helperText = "Password not matched"
                        tilRetypePassword.setHelperTextColor(ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_dark)))
                        etRetypePassword.background = resources.getDrawable(R.drawable.border_danger)
                        etRetypePassword.requestFocus()
                    } else {
                        tilPassword.isHelperTextEnabled = false
                        etPassword.background = resources.getDrawable(R.drawable.border_accent)

                        tilRetypePassword.isHelperTextEnabled = false
                        etRetypePassword.background = resources.getDrawable(R.drawable.border_accent)

                        btnContinue.isEnabled = false
                        progressBar.visibility = View.VISIBLE
                        cvContinue.setCardBackgroundColor(ColorStateList.valueOf(resources.getColor(R.color.colorPrimaryDark)))
                        Handler().postDelayed({
                            mService!!.forgotPassword(email, password).enqueue(object : Callback<ResponseModel> {
                                override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                                    val code = response.body()!!.code
                                    val message = response.body()!!.message
                                    btnContinue.isEnabled = true
                                    progressBar.visibility = View.INVISIBLE
                                    cvContinue.setCardBackgroundColor(ColorStateList.valueOf(resources.getColor(R.color.colorPrimary)))
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
                                    cvContinue.setCardBackgroundColor(ColorStateList.valueOf(resources.getColor(R.color.colorPrimary)))
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
        if (state == 1) {
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