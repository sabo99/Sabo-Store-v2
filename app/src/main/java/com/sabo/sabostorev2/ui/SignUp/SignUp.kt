@file:Suppress("DEPRECATION")

package com.sabo.sabostorev2.ui.SignUp

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
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

        val text1 = Common.getColoredSpanned("Already have an account?", "")
        val text2 = Common.getColoredSpanned("Sign Up", "#33B5E5")
        tvSignIn.text = Html.fromHtml("$text1 $text2")

        etUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                clearColorFieldUsername()
                if (s.toString().isNotEmpty())
                    tvUsername.visibility = View.VISIBLE
                else
                    tvUsername.visibility = View.GONE
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                clearColorFieldUsername()
                if (s.toString().isNotEmpty())
                    tvUsername.visibility = View.VISIBLE
                else
                    tvUsername.visibility = View.GONE
            }
            override fun afterTextChanged(s: Editable?) {
                clearColorFieldUsername()
                if (s.toString().isNotEmpty()) tvUsername.visibility = View.VISIBLE
                else tvUsername.visibility = View.GONE
            }
        })
        etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                clearColorFieldEmail()
                if (s.toString().isNotEmpty())
                    tvEmail.visibility = View.VISIBLE
                else
                    tvEmail.visibility = View.GONE
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                clearColorFieldEmail()
                if (s.toString().isNotEmpty())
                    tvEmail.visibility = View.VISIBLE
                else
                    tvEmail.visibility = View.GONE
            }
            override fun afterTextChanged(s: Editable?) {
                clearColorFieldEmail()
                if (s.toString().isNotEmpty()) tvEmail.visibility = View.VISIBLE
                else tvEmail.visibility = View.GONE
            }
        })
        etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                clearColorFieldPassword()
                if (s.toString().isNotEmpty()) tvPassword.visibility = View.VISIBLE
                else tvPassword.visibility = View.GONE

            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
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

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnSignUp -> signUp()
            R.id.tvSignIn -> finish()
            R.id.btnBack -> finish()
        }
    }

    private fun clearAllOptionFields() {
        etUsername.clearFocus()
        etEmail.clearFocus()
        etPassword.clearFocus()

        etUsername.setText("")
        etEmail.setText("")
        etPassword.setText("")

        clearAllColorField()
    }

    private fun clearAllColorField() {
        clearColorFieldUsername()
        clearColorFieldEmail()
        clearColorFieldPassword()
    }

    private fun clearColorFieldUsername() {
        tilUsername.isHelperTextEnabled = false
        etUsername.background = resources.getDrawable(R.drawable.border_accent)
    }

    private fun clearColorFieldEmail() {
        tilEmail.isHelperTextEnabled = false
        etEmail.background = resources.getDrawable(R.drawable.border_accent)
    }

    private fun clearColorFieldPassword() {
        tilPassword.isHelperTextEnabled = false
        etPassword.background = resources.getDrawable(R.drawable.border_accent)
    }

    private fun signUp() {
        clearAllColorField()

        val username = etUsername.text.toString()
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()

        if (username.isEmpty()) {
            etUsername.requestFocus()
            etUsername.background = resources.getDrawable(R.drawable.border_danger)
            tilUsername.isHelperTextEnabled = true
            tilUsername.helperText = "Username is required"
            tilUsername.setHelperTextColor(ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_dark)))
        } else if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            clearColorFieldUsername()

            etEmail.requestFocus()
            etEmail.background = resources.getDrawable(R.drawable.border_danger)
            tilEmail.isHelperTextEnabled = true
            tilEmail.helperText = "Invalid E-mail format"
            tilEmail.setHelperTextColor(ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_dark)))
        } else if (password.length < 6) {
            clearColorFieldUsername()
            clearColorFieldEmail()

            etPassword.requestFocus()
            etPassword.background = resources.getDrawable(R.drawable.border_danger)
            tilPassword.isHelperTextEnabled = true
            tilPassword.helperText = "Password too short (Minimal 6 digit)"
            tilPassword.setHelperTextColor(ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_dark)))
        } else {
            clearAllColorField()

            progressBar.visibility = View.VISIBLE
            btnSignUp.isEnabled = false
            cvSignUp.setCardBackgroundColor(ColorStateList.valueOf(resources.getColor(R.color.colorPrimaryDark)))

            mService!!.signUp(email, username, password).enqueue(object : Callback<ResponseModel> {
                override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                    val code = response.body()!!.code
                    val message = response.body()!!.message
                    progressBar.visibility = View.INVISIBLE
                    btnSignUp.isEnabled = true
                    cvSignUp.setCardBackgroundColor(ColorStateList.valueOf(resources.getColor(R.color.colorAccent)))

                    if (code == 1) {
                        etUsername.requestFocus()
                        etUsername.background = resources.getDrawable(R.drawable.border_danger)
                        tilUsername.isHelperTextEnabled = true
                        tilUsername.helperText = message
                        tilUsername.setHelperTextColor(ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_dark)))
                    } else if (code == 2) {
                        clearColorFieldUsername()

                        etEmail.requestFocus()
                        etEmail.background = resources.getDrawable(R.drawable.border_danger)
                        tilEmail.isHelperTextEnabled = true
                        tilEmail.helperText = message
                        tilEmail.setHelperTextColor(ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_dark)))
                    } else if (code == 3 || code == 4) {
                        clearAllColorField()

                        val sweet = SweetAlertDialog(this@SignUp, SweetAlertDialog.WARNING_TYPE)
                        sweet.titleText = "Oops!"
                        sweet.contentText = message
                        sweet.show()
                    } else {
                        clearAllOptionFields()
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
                    clearAllColorField()
                    progressBar.visibility = View.INVISIBLE
                    btnSignUp.isEnabled = true
                    cvSignUp.setCardBackgroundColor(ColorStateList.valueOf(resources.getColor(R.color.colorAccent)))
                    val sweet = SweetAlertDialog(this@SignUp, SweetAlertDialog.WARNING_TYPE)
                    sweet.titleText = "Oops!"
                    sweet.contentText = t.message
                    sweet.show()
                }
            })
        }
    }

    override fun finish() {
        super.finish()
        CustomIntent.customType(this, Common.RTL)
    }
}