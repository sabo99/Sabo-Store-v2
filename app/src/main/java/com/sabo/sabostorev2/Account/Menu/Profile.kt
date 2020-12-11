package com.sabo.sabostorev2.Account.Menu

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.sabo.sabostorev2.API.APIRequestData
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

class Profile : AppCompatActivity(), View.OnClickListener {

    private var civProfile: CircleImageView? = null
    private var tvUsername: TextView? = null
    private var tvEmail: TextView? = null
    private var tvPhone: TextView? = null

    private var mService: APIRequestData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        mService = Common.getAPI()
        initViews()
    }

    private fun initViews() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Profile"

        civProfile = findViewById(R.id.civProfile)
        tvUsername = findViewById(R.id.tvUsername)
        tvEmail = findViewById(R.id.tvEmail)
        tvPhone = findViewById(R.id.tvPhone)

        findViewById<ImageButton>(R.id.ibChangeImage).setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        val uid: String = Preferences.getUID(this)
        mService!!.getUser(uid).enqueue(object : Callback<ResponseModel>{
            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                var code : Int = response.body()!!.code
                if (code == 2){
                    val user : UserModel = response.body()!!.user
                    val img : String = Common.USER_IMAGE_URL + user.image
                    Picasso.get().load(img).placeholder(R.drawable.no_profile).into(civProfile)
                    tvUsername!!.text = user.username
                    tvEmail!!.text = user.email
                    if (user.phone != "")
                        tvPhone!!.text = user.phone
                    else
                        tvPhone!!.text = "None"
                }
            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.ibChangeImage -> {

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