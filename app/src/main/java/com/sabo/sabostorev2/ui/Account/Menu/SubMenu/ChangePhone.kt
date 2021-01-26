@file:Suppress("DEPRECATION")

package com.sabo.sabostorev2.ui.Account.Menu.SubMenu

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import com.sabo.sabostorev2.API.APIRequestData
import com.sabo.sabostorev2.Common.Common
import com.sabo.sabostorev2.Common.Preferences
import com.sabo.sabostorev2.EventBus.UpdateProfileEvent
import com.sabo.sabostorev2.Model.ResponseModel
import com.sabo.sabostorev2.R
import com.sabo.sabostorev2.RoomDB.RoomDBHost
import com.sabo.sabostorev2.RoomDB.User.LocalUserDataSource
import com.sabo.sabostorev2.RoomDB.User.User
import com.sabo.sabostorev2.RoomDB.User.UserDataSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_change_phone.*
import maes.tech.intentanim.CustomIntent
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class ChangePhone : AppCompatActivity(), View.OnClickListener {

    private var mService: APIRequestData? = null
    private var compositeDisposable: CompositeDisposable? = null
    private var userDataSource: UserDataSource? = null

    private var uid = ""
    private var mCodeVerification: Int? = null
    private var timer: CountDownTimer? = null
    private var state = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_phone)

        mService = Common.getAPI()
        compositeDisposable = CompositeDisposable();
        userDataSource = LocalUserDataSource(RoomDBHost.getInstance(this).userDAO())
        uid = Preferences.getUID(this)
        initViews()
        initTimer()
    }

    private fun initViews() {
        if (state == 0) {
            llGetOTP!!.visibility = View.VISIBLE
            llSendOTP!!.visibility = View.INVISIBLE
        }
        if (state == 1) {
            llGetOTP!!.visibility = View.INVISIBLE
            llSendOTP!!.visibility = View.VISIBLE
        }

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener(this)
        findViewById<Button>(R.id.btnSend).setOnClickListener(this)
        findViewById<TextView>(R.id.tvResendCode).setOnClickListener(this)
        findViewById<Button>(R.id.btnVerify).setOnClickListener(this)
    }

    private fun initTimer() {
        timer = object : CountDownTimer(20 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tvResendCodeMessage.text = "Resend Code in " + millisUntilFinished / 1000 + " second"
                tvResendCode.isEnabled = false
                tvResendCode.setTextColor(resources.getColor(android.R.color.tertiary_text_dark))
            }

            override fun onFinish() {
                tvResendCodeMessage.text = "Resend a new code"
                tvResendCode.isEnabled = true
                tvResendCode.setTextColor(resources.getColor(android.R.color.black))
            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnBack -> finish()
            R.id.btnSend -> sendCode()
            R.id.tvResendCode -> resendCode()
            R.id.btnVerify -> verifyPhone()
        }
    }

    override fun finish() {
        super.finish()
        CustomIntent.customType(this, Common.RTL)
    }

    private fun sendCode() {
        val phone = etPhone.text.toString()

        val rand = Random()
        val number = rand.nextInt(999999)
        val code = String.format("%06d", number).toInt()
        var checkZero = ""
        if (phone.isNotEmpty()) {
            checkZero = phone.substring(0, 1)
        }

        if (phone.isEmpty() || !Patterns.PHONE.matcher(phone).matches() || phone.length < 11 || checkZero == "0") {
            Toast.makeText(this, "Invalid number", Toast.LENGTH_SHORT).show()
            etPhone!!.error = "ex: 721 xxxx xxxx"
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                etPhone.backgroundTintList = ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_light))
            }
        } else {
            val animation = AnimationUtils.loadAnimation(this, R.anim.slide_to_left)
            llGetOTP.animation = animation
            llGetOTP.visibility = View.INVISIBLE

            val animation1 = AnimationUtils.loadAnimation(this, R.anim.slide_from_right_to_left)
            llSendOTP.animation = animation1
            llSendOTP.visibility = View.VISIBLE
            state = 1

            btnSend.isEnabled = false
            btnVerify.isEnabled = false
            cvVerify.setCardBackgroundColor(resources.getColor(R.color.colorPrimaryDark))

            Handler().postDelayed({
                timer!!.start()
            }, 1000)

            mService!!.getOTP(uid, phone, code).enqueue(object : Callback<ResponseModel> {
                override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                    val coded = response.body()!!.code
                    btnSend.isEnabled = true
                    if (coded == 1) {
                        val userCode = response.body()!!.user
                        mCodeVerification = userCode.code
                        // Delay 5 Second
                        Handler().postDelayed({
                            btnVerify.isEnabled = true
                            cvVerify.setCardBackgroundColor(resources.getColor(R.color.colorAccent))
                            etCode.setText(mCodeVerification.toString())
                        }, 5000);
                    }
                    if (coded == 2) {
                        Toast.makeText(this@ChangePhone, "Request code failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                    btnSend.isEnabled = true
                    Toast.makeText(this@ChangePhone, t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun resendCode() {
        val phone = etPhone.text.toString()
        val rand = Random()
        val number = rand.nextInt(999999)
        val code = String.format("%06d", number).toInt()

        btnVerify.isEnabled = false
        cvVerify.setCardBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
        timer!!.start()

        mService!!.getOTP(uid, phone, code).enqueue(object : Callback<ResponseModel> {
            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                val coded = response.body()!!.code
                btnSend.isEnabled = true
                if (coded == 1) {
                    val userCode = response.body()!!.user
                    mCodeVerification = userCode.code
                    // Delay 5 Second
                    Handler().postDelayed({
                        btnVerify.isEnabled = true
                        cvVerify.setCardBackgroundColor(resources.getColor(R.color.colorAccent))
                        etCode.setText(mCodeVerification.toString())
                    }, 5000);
                }
                if (coded == 2) {
                    Toast.makeText(this@ChangePhone, "Request code failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                btnSend.isEnabled = true
                Toast.makeText(this@ChangePhone, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun verifyPhone() {
        val code = etCode!!.text.toString()
        val phone = etPhone!!.text.toString()
        val countryCode = "+" + ccPicker.selectedCountryCode

        if (code != mCodeVerification.toString()) {
            Toast.makeText(this, "Invalid Code", Toast.LENGTH_SHORT).show()
            return
        } else {
            val sweetLoading = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            sweetLoading.progressHelper.barColor = resources.getColor(R.color.colorAccent)
            sweetLoading.setTitleText("Please wait...").setCanceledOnTouchOutside(false)
            sweetLoading.show()

            mService!!.updateUserPhone(uid, phone, countryCode).enqueue(object : Callback<ResponseModel> {
                override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                    val code = response.body()!!.code
                    sweetLoading.dismissWithAnimation()
                    if (code == 1) {
                        Toast.makeText(this@ChangePhone, "Verify code failed", Toast.LENGTH_SHORT).show()
                    }
                    if (code == 2) {
                        /** Update Phone In DB Local */
                        updateDBLocal(phone, countryCode)
                        EventBus.getDefault().postSticky(UpdateProfileEvent(true))
                        val t = Toast.makeText(this@ChangePhone, "Phone number added successfully", Toast.LENGTH_LONG)
                        t.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                        t.show()
                        finish()
                    }
                }

                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                    sweetLoading.setTitleText("Oops!")
                            .setContentText(t.message)
                            .changeAlertType(SweetAlertDialog.WARNING_TYPE)
                }
            })
        }
    }

    private fun updateDBLocal(phone: String, countryCode: String) {
        val userModel = Common.currentUser
        val user = User()
        user.uid = userModel.uid
        user.email = userModel.email
        user.username = userModel.username
        user.nickname = userModel.nickname
        user.image = userModel.image
        user.phone = phone
        user.countryCode = countryCode
        user.gender = userModel.gender

        compositeDisposable!!.add(userDataSource!!.insertOrUpdateUser(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Log.d("user", "Phone updated successfully")
                })
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable!!.clear()
    }
}