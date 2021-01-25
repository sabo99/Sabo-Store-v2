package com.sabo.sabostorev2.ui.Account.Menu.SubMenu.TwoStepVerification

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import com.sabo.sabostorev2.API.APIRequestData
import com.sabo.sabostorev2.Common.Common
import com.sabo.sabostorev2.Common.Preferences
import com.sabo.sabostorev2.Model.ResponseModel
import com.sabo.sabostorev2.R
import kotlinx.android.synthetic.main.activity_two_step_verification.*
import maes.tech.intentanim.CustomIntent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TwoStepVerification : AppCompatActivity(), View.OnClickListener {

    private var mService: APIRequestData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_two_step_verification)

        mService = Common.getAPI()
        initViews()
    }

    private fun initViews() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Two-step verification"

        findViewById<Button>(R.id.btnEnable).setOnClickListener(this)
        findViewById<LinearLayout>(R.id.llDisable).setOnClickListener(this)
        findViewById<LinearLayout>(R.id.llChangePIN).setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        loadIsPIN()
    }

    private fun loadIsPIN() {
        val isPIN = Preferences.getIsPIN(this)
        if (isPIN) {
            llMenu.visibility = View.VISIBLE
            cvEnable.visibility = View.GONE
        } else {
            llMenu.visibility = View.GONE
            cvEnable.visibility = View.VISIBLE
        }
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
        when (v!!.id) {
            R.id.btnEnable -> {
                startActivity(Intent(this, PINCode::class.java))
                CustomIntent.customType(this, Common.LTR)
            }
            R.id.llDisable -> disableTwoStepVerification()
            R.id.llChangePIN -> {
                startActivity(Intent(this, PINCode::class.java))
                CustomIntent.customType(this, Common.LTR)
            }
        }
    }

    private fun disableTwoStepVerification() {
        val sweetLoading = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        sweetLoading.progressHelper.barColor = resources.getColor(R.color.colorAccent)
        sweetLoading.titleText = "Please wait..."

        val sweetDialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
        sweetDialog.titleText = "Disable"
        sweetDialog.contentText = "Two-step verification?"
        sweetDialog.showCancelButton(true)
        sweetDialog.cancelText = "Cancel"
        sweetDialog.setCancelClickListener {
            sweetDialog.dismissWithAnimation()
        }
        sweetDialog.confirmText = "Disable"
        sweetDialog.setConfirmClickListener {
            sweetDialog.dismissWithAnimation()
            sweetLoading.show()
            val uid = Preferences.getUID(this)
            mService!!.updateUserPIN(uid, 0, "0").enqueue(object : Callback<ResponseModel> {
                override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                    val code = response.body()!!.code
                    val message = response.body()!!.message
                    sweetLoading.dismissWithAnimation()
                    if (code == 0) {
                        Preferences.setIsPIN(this@TwoStepVerification, false)
                        Handler().postDelayed({
                            loadIsPIN()
                            val t = Toast.makeText(this@TwoStepVerification, message, Toast.LENGTH_SHORT)
                            t.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                            t.show()
                        }, 1000)

                    }
                    if (code == 2) {
                        val s = SweetAlertDialog(this@TwoStepVerification, SweetAlertDialog.WARNING_TYPE)
                        s.titleText = "Oops!"
                        s.contentText = message
                        s.show()
                    }
                }

                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                    sweetLoading.dismissWithAnimation()
                    val s = SweetAlertDialog(this@TwoStepVerification, SweetAlertDialog.WARNING_TYPE)
                    s.titleText = "Oops!"
                    s.contentText = t.message
                    s.show()
                }
            })
        }
        sweetDialog.show()
    }
}