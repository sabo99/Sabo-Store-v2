package com.sabo.sabostorev2.ui.Account

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.sabo.sabostorev2.ui.Account.Menu.AccountSettings
import com.sabo.sabostorev2.ui.Account.Menu.Favorite
import com.sabo.sabostorev2.ui.Account.Menu.Profile
import com.sabo.sabostorev2.Common.Common
import com.sabo.sabostorev2.Common.Preferences
import com.sabo.sabostorev2.R
import com.sabo.sabostorev2.RoomDB.RoomDBHost
import com.sabo.sabostorev2.RoomDB.User.LocalUserDataSource
import com.sabo.sabostorev2.RoomDB.User.User
import com.sabo.sabostorev2.RoomDB.User.UserDataSource
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_account.*
import maes.tech.intentanim.CustomIntent

class AccountActivity : AppCompatActivity(), View.OnClickListener {

    private var compositeDisposable: CompositeDisposable? = null
    private var userDataSource: UserDataSource? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        compositeDisposable = CompositeDisposable()
        userDataSource = LocalUserDataSource(RoomDBHost.getInstance(this).userDAO())

        initViews()
    }

    private fun initViews() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Account"

        findViewById<LinearLayout>(R.id.profile).setOnClickListener(this)
        findViewById<LinearLayout>(R.id.accountSettings).setOnClickListener(this)
        findViewById<LinearLayout>(R.id.favorite).setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        val uid = Preferences.getUID(this)

        compositeDisposable!!.add(userDataSource!!.getUser(uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { user: User ->
                    val img = Common.USER_IMAGE_URL + user.image
                    Picasso.get().load(img).placeholder(R.drawable.no_profile).into(civProfile)
                    tvUsername.text = user.username
                    tvEmail.text = user.email
                })
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.profile -> {
                if (Preferences.getIsPIN(this)) {
                    Common.twoStepVerification(this, Profile::class.java)
                    CustomIntent.customType(this, Common.FINFOUT)
                } else {
                    startActivity(Intent(this, Profile::class.java))
                    CustomIntent.customType(this, Common.FINFOUT)
                }
            }
            R.id.accountSettings -> {
                startActivity(Intent(this, AccountSettings::class.java))
                CustomIntent.customType(this, Common.LTR)
            }
            R.id.favorite -> {
                startActivity(Intent(this, Favorite::class.java))
                CustomIntent.customType(this, Common.LTR)
            }
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

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable!!.clear()
    }
}