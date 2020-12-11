package com.sabo.sabostorev2.Account

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.sabo.sabostorev2.API.APIRequestData
import com.sabo.sabostorev2.Account.Menu.AccountSettings
import com.sabo.sabostorev2.Account.Menu.Favorite
import com.sabo.sabostorev2.Account.Menu.OrderHistory
import com.sabo.sabostorev2.Account.Menu.Profile
import com.sabo.sabostorev2.Common.Common
import com.sabo.sabostorev2.Common.Preferences
import com.sabo.sabostorev2.Model.ResponseModel
import com.sabo.sabostorev2.Model.UserModel
import com.sabo.sabostorev2.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import maes.tech.intentanim.CustomIntent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountActivity : AppCompatActivity(), View.OnClickListener {

    private var mService: APIRequestData? = null
    private var civProfile: CircleImageView? = null
    private var tvUsername: TextView? = null
    private var tvEmail: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        mService = Common.getAPI()

        initViews()
    }

    private fun initViews() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Account"

        civProfile = findViewById(R.id.civProfile)
        tvUsername = findViewById(R.id.tvUsername)
        tvEmail = findViewById(R.id.tvEmail)

        findViewById<LinearLayout>(R.id.profile).setOnClickListener(this)
        findViewById<LinearLayout>(R.id.accountSettings).setOnClickListener(this)
        findViewById<LinearLayout>(R.id.favorite).setOnClickListener(this)
        findViewById<LinearLayout>(R.id.orderHistory).setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        val uid: String = Preferences.getUID(this)

        mService!!.getUser(uid).enqueue(object : Callback<ResponseModel> {
            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                val code: Int = response.body()!!.code
                if (code == 2) {
                    val user: UserModel = response.body()!!.user
                    val img: String = Common.USER_IMAGE_URL + user.image
                    Picasso.get().load(img).placeholder(R.drawable.no_profile).into(civProfile)
                    tvUsername!!.text = user.username
                    tvEmail!!.text = user.email
                }
                if (code == 1) {
                    val message: String = response.body()!!.message
                    Log.d("User", message)
                }
            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.profile -> {
                startActivity(Intent(this, Profile::class.java))
                CustomIntent.customType(this, Common.LTR)
            }
            R.id.accountSettings -> {
                startActivity(Intent(this, AccountSettings::class.java))
                CustomIntent.customType(this, Common.LTR)
            }
            R.id.favorite -> {
                startActivity(Intent(this, Favorite::class.java))
                CustomIntent.customType(this, Common.LTR)
            }
            R.id.orderHistory -> {
                startActivity(Intent(this, OrderHistory::class.java))
                CustomIntent.customType(this, Common.LTR)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                CustomIntent.customType(this, Common.RTL)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun finish() {
        super.finish()
        CustomIntent.customType(this, Common.RTL)
    }
}