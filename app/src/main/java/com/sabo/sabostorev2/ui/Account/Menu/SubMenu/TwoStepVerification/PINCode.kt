package com.sabo.sabostorev2.ui.Account.Menu.SubMenu.TwoStepVerification

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import com.sabo.sabostorev2.API.APIRequestData
import com.sabo.sabostorev2.Common.Common
import com.sabo.sabostorev2.Common.Preferences
import com.sabo.sabostorev2.Model.ResponseModel
import com.sabo.sabostorev2.R
import kotlinx.android.synthetic.main.activity_pin_code.*
import maes.tech.intentanim.CustomIntent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PINCode : AppCompatActivity(), View.OnClickListener {

    private var state = 0
    private var mService: APIRequestData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_code)

        mService = Common.getAPI()
        initViews()
    }

    private fun initViews() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Two-step verification"

        findViewById<Button>(R.id.btnNext).setOnClickListener(this)
        findViewById<Button>(R.id.btnConfirm).setOnClickListener(this)

        /** Input PIN **/
        btnNext.isEnabled = false
        cvNext.setCardBackgroundColor(ColorStateList.valueOf(resources.getColor(R.color.colorPrimaryDark)))
        etPIN.requestFocus()
        etPIN.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkPIN(s)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        /** Confirm PIN **/
        btnConfirm.isEnabled = false
        cvConfirm.setCardBackgroundColor(ColorStateList.valueOf(resources.getColor(R.color.colorPrimaryDark)))
        etCPIN.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkCPIN(s)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun checkPIN(s: CharSequence?) {
        if (s.toString().length == 6) {
            btnNext.isEnabled = true
            cvNext.setCardBackgroundColor(ColorStateList.valueOf(resources.getColor(R.color.colorAccent)))
            goState1()
        } else {
            btnNext.isEnabled = false
            cvNext.setCardBackgroundColor(ColorStateList.valueOf(resources.getColor(R.color.colorPrimaryDark)))
        }
    }

    private fun checkCPIN(s: CharSequence?) {
        if (s.toString().length == 6) {
            btnConfirm.isEnabled = true
            cvConfirm.setCardBackgroundColor(ColorStateList.valueOf(resources.getColor(R.color.colorAccent)))
        } else {
            btnConfirm.isEnabled = false
            cvConfirm.setCardBackgroundColor(ColorStateList.valueOf(resources.getColor(R.color.colorPrimaryDark)))
        }
    }


    override fun finish() {
        super.finish()
        CustomIntent.customType(this, Common.RTL)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if (state == 0) finish()
                if (state == 1) backState0()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (state == 0) super.onBackPressed()
        if (state == 1) backState0()

    }

    private fun backState0() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.slide_to_right)
        llConfirmPIN.animation = animation
        llConfirmPIN.visibility = View.INVISIBLE

        val animation1 = AnimationUtils.loadAnimation(this, R.anim.slide_from_left_to_right)
        llInputPIN.animation = animation1
        llInputPIN.visibility = View.VISIBLE

        cvNext.visibility = View.VISIBLE
        cvConfirm.visibility = View.GONE
        ivPosition2.setImageResource(R.drawable.bg_circle_point_deactived)
        state = 0
    }

    private fun goState1() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.slide_to_left)
        llInputPIN.animation = animation
        llInputPIN.visibility = View.INVISIBLE

        val animation1 = AnimationUtils.loadAnimation(this, R.anim.slide_from_right_to_left)
        llConfirmPIN.animation = animation1
        llConfirmPIN.visibility = View.VISIBLE

        cvNext.visibility = View.GONE
        cvConfirm.visibility = View.VISIBLE
        ivPosition2.setImageResource(R.drawable.bg_circle_point_actived)
        state = 1
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnNext -> {
                goState1()
                etCPIN.requestFocus()
            }
            R.id.btnConfirm -> confirmPIN()
        }
    }

    private fun confirmPIN() {
        val uid = Preferences.getUID(this)
        val PIN = etPIN.text.toString()
        val CPIN = etCPIN.text.toString()

        val sweetLoading = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        sweetLoading.progressHelper.barColor = resources.getColor(R.color.colorAccent)
        sweetLoading.titleText = "Please wait..."
        sweetLoading.show()

        if (CPIN != PIN) {
            val t = Toast.makeText(this, "PIN does not match", Toast.LENGTH_SHORT)
            t.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
            t.show()
        } else {
            mService!!.updateUserPIN(uid, 1, PIN).enqueue(object : Callback<ResponseModel> {
                override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                    val code = response.body()!!.code
                    val message = response.body()!!.message

                    Handler().postDelayed({
                        sweetLoading.dismissWithAnimation()
                        if (code == 1) {
                            val s = SweetAlertDialog(this@PINCode, SweetAlertDialog.SUCCESS_TYPE)
                            s.titleText = "Success!"
                            s.contentText = message
                            s.setConfirmClickListener { s: SweetAlertDialog ->
                                s.dismissWithAnimation()
                                Preferences.setIsPIN(this@PINCode, true)
                                CustomIntent.customType(this@PINCode, Common.RTL)
                                finish()
                            }
                            s.show()
                        }
                        if (code == 2) {
                            val s = SweetAlertDialog(this@PINCode, SweetAlertDialog.WARNING_TYPE)
                            s.titleText = "Oops!"
                            s.contentText = message
                            s.show()
                        }
                    }, 1000)
                }

                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                    sweetLoading.dismissWithAnimation()
                    val s = SweetAlertDialog(this@PINCode, SweetAlertDialog.WARNING_TYPE)
                    s.titleText = "Oops!"
                    s.contentText = t.message
                    s.show()
                }
            })
        }
    }

}