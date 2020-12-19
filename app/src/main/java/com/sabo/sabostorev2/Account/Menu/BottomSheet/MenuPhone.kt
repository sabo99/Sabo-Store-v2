package com.sabo.sabostorev2.Account.Menu.BottomSheet

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.util.Patterns
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.rilixtech.widget.countrycodepicker.CountryCodePicker
import com.sabo.sabostorev2.API.APIRequestData
import com.sabo.sabostorev2.Common.Common
import com.sabo.sabostorev2.Common.Preferences
import com.sabo.sabostorev2.EventBus.UpdateProfileEvent
import com.sabo.sabostorev2.Model.ResponseModel
import com.sabo.sabostorev2.Model.UserModel
import com.sabo.sabostorev2.R
import com.sabo.sabostorev2.RoomDB.RoomDBHost
import com.sabo.sabostorev2.RoomDB.User.LocalUserDataSource
import com.sabo.sabostorev2.RoomDB.User.User
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_menu_phone.*
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class MenuPhone : AppCompatActivity(), View.OnClickListener {

    private var llGetOTP: LinearLayout? = null
    private var llSendOTP: LinearLayout? = null
    private var etPhone: EditText? = null
    private var etCode: EditText? = null
    private var tvResendCodeMessage: TextView? = null
    private var ccPicker: CountryCodePicker? = null

    private var mService: APIRequestData? = null
    private var compositeDisposable: CompositeDisposable?= null
    private var localUserDataSource: LocalUserDataSource?= null

    private var mCodeVerification: Int? = null
    private var timer: CountDownTimer? = null
    private var state = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_phone)

        mService = Common.getAPI()
        compositeDisposable = CompositeDisposable();
        localUserDataSource = LocalUserDataSource(RoomDBHost.getInstance(this).userDAO())
        initViews()
        initTimer()
    }

    private fun initViews() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Change Phone Number"

        llGetOTP = findViewById(R.id.llGetOTP)
        llSendOTP = findViewById(R.id.llSendOTP)
        etPhone = findViewById(R.id.etPhone)
        etCode = findViewById(R.id.etCode)
        tvResendCodeMessage = findViewById(R.id.tvResendCodeMessage)
        ccPicker = findViewById(R.id.ccPicker)


        if (state == 0){
            llGetOTP!!.visibility = View.VISIBLE
            llSendOTP!!.visibility = View.INVISIBLE
        }
        if (state == 1){
            llGetOTP!!.visibility = View.INVISIBLE
            llSendOTP!!.visibility = View.VISIBLE
        }

        findViewById<Button>(R.id.btnSend).setOnClickListener(this)
        findViewById<TextView>(R.id.tvResendCode).setOnClickListener(this)
        findViewById<Button>(R.id.btnVerify).setOnClickListener(this)
    }

    private fun initTimer() {
        timer = object : CountDownTimer(20 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tvResendCodeMessage!!.text = "Resend Code in " + millisUntilFinished / 1000 + " second"
                tvResendCode!!.isEnabled = false
            }

            override fun onFinish() {
                tvResendCodeMessage!!.text = "Resend a new code"
                tvResendCode!!.isEnabled = true
            }
        }
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnSend -> {
                sendCode()
            }
            R.id.tvResendCode -> {
                resendCode()
            }
            R.id.btnVerify -> {
                verifyPhone()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun sendCode() {
        val uid: String = Preferences.getUID(this)
        val phone: String = etPhone!!.text.toString()
        val countryCode: String = "+" + ccPicker!!.selectedCountryCode

        val rand = Random()
        val number: Int = rand.nextInt(999999)
        val code: Int = String.format("%06d", number).toInt()
        var checkZero: String? = null
        if (phone.isNotEmpty()){
            checkZero = phone.substring(0, 1)
        }

        if (phone.isNullOrEmpty() || !Patterns.PHONE.matcher(phone).matches() || phone.length < 11 || checkZero == "0") {
            Toast.makeText(this, "Invalid number", Toast.LENGTH_SHORT).show()
            etPhone!!.error = "ex: 721 xxxx xxxx"
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                etPhone!!.backgroundTintList = ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_light))
            }
        } else {
            val animation = AnimationUtils.loadAnimation(this, R.anim.slide_to_left)
            llGetOTP!!.animation = animation
            llGetOTP!!.visibility = View.INVISIBLE

            val animation1 = AnimationUtils.loadAnimation(this, R.anim.slide_from_right_to_left)
            llSendOTP!!.animation = animation1
            llSendOTP!!.visibility = View.VISIBLE
            state = 1

            btnSend.isEnabled = false
            btnVerify.isEnabled = false
//            llGetOTP!!.visibility = View.GONE
//            llSendOTP!!.visibility = View.VISIBLE

            Handler().postDelayed({
                timer!!.start()
            }, 1000)

            Preferences.setCountryCode(this, countryCode)
            mService!!.getOTP(uid, phone, code).enqueue(object : Callback<ResponseModel> {
                override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                    val code: Int = response.body()!!.code
                    btnSend.isEnabled = true
                    if (code == 1) {
                        val userCode = response.body()!!.user
                        mCodeVerification = userCode.code
                        // Delay 5 Second
                        Handler().postDelayed({
                            btnVerify.isEnabled = true
                            etCode!!.setText(mCodeVerification.toString())
                        }, 5000);

                        /** Update Phone In DB Local */
                        updateDBLocal(phone)
                    }
                    if (code == 2) {
                        Toast.makeText(this@MenuPhone, "Request code failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                    btnSend.isEnabled = true
                    Toast.makeText(this@MenuPhone, t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun updateDBLocal(phone: String) {
        val userModel = Common.currentUser
        val user = User()
        user.uid = userModel.uid
        user.email = userModel.email
        user.username = userModel.username
        user.image = userModel.image
        user.phone = phone
        user.gender = userModel.gender

        compositeDisposable!!.add(localUserDataSource!!.insertOrUpdateUser(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("user", "Phone updated successfully")
                })
                { throwable: Throwable ->
                    Log.d("user", throwable.message)
                })
    }

    private fun resendCode() {
        val phone: String = etPhone!!.text.toString()
        val uid: String = Preferences.getUID(this)

        val rand = Random()
        val number: Int = rand.nextInt(999999)
        val code: Int = String.format("%06d", number).toInt()

        btnVerify.isEnabled = false
        timer!!.start()

        mService!!.getOTP(uid, phone, code).enqueue(object : Callback<ResponseModel> {
            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                val code: Int = response.body()!!.code
                btnSend.isEnabled = true
                if (code == 1) {
                    val userCode = response.body()!!.user
                    mCodeVerification = userCode.code
                    // Delay 5 Second
                    Handler().postDelayed({
                        btnVerify.isEnabled = true
                        etCode!!.setText(mCodeVerification.toString())
                    }, 5000);
                }
                if (code == 2) {
                    Toast.makeText(this@MenuPhone, "Request code failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                btnSend.isEnabled = true
                Toast.makeText(this@MenuPhone, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun verifyPhone() {
        val code: String = etCode!!.text.toString()
        if (code != mCodeVerification.toString()) {
            Toast.makeText(this, "Invalid Code", Toast.LENGTH_SHORT).show()
            return
        } else {
            EventBus.getDefault().postSticky(UpdateProfileEvent(true))
            Toast.makeText(this, "Phone number added successfully", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable!!.clear()
    }

}