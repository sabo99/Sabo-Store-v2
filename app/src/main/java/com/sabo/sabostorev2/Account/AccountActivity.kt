package com.sabo.sabostorev2.Account

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.sabo.sabostorev2.Account.Menu.AccountSettings
import com.sabo.sabostorev2.Account.Menu.Favorite
import com.sabo.sabostorev2.Account.Menu.OrderHistory
import com.sabo.sabostorev2.Account.Menu.Profile
import com.sabo.sabostorev2.Common.Common
import com.sabo.sabostorev2.Common.Preferences
import com.sabo.sabostorev2.R
import com.sabo.sabostorev2.RoomDB.RoomDBHost
import com.sabo.sabostorev2.RoomDB.User.LocalUserDataSource
import com.sabo.sabostorev2.RoomDB.User.User
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import maes.tech.intentanim.CustomIntent

class AccountActivity : AppCompatActivity(), View.OnClickListener {

    private var civProfile: CircleImageView? = null
    private var tvUsername: TextView? = null
    private var tvEmail: TextView? = null

    private var compositeDisposable: CompositeDisposable? = null
    private var localUserDataSource: LocalUserDataSource? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        compositeDisposable = CompositeDisposable()
        localUserDataSource = LocalUserDataSource(RoomDBHost.getInstance(this).userDAO())

        initViews()
    }

    private fun initViews() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Account"

        civProfile = findViewById(R.id.civPhotoProfile)
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

        compositeDisposable!!.add(localUserDataSource!!.getUser(uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { user: User ->
                    val img: String = Common.USER_IMAGE_URL + user.image
                    Picasso.get().load(img).placeholder(R.drawable.no_profile).into(civProfile)
                    tvUsername!!.text = user.username
                    tvEmail!!.text = user.email
                })
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.profile -> {
                startActivity(Intent(this, Profile::class.java))
                CustomIntent.customType(this, Common.FINFOUT)
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

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable!!.clear()
    }
}