@file:Suppress("DEPRECATION")

package com.sabo.sabostorev2.ui.Account.Menu.SubMenu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button

import androidx.appcompat.app.AppCompatActivity

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import com.sabo.sabostorev2.API.APIRequestData
import com.sabo.sabostorev2.Common.Common
import com.sabo.sabostorev2.Common.Preferences
import com.sabo.sabostorev2.Model.ResponseModel
import com.sabo.sabostorev2.R
import com.sabo.sabostorev2.RoomDB.RoomDBHost
import com.sabo.sabostorev2.RoomDB.User.LocalUserDataSource
import com.sabo.sabostorev2.RoomDB.User.User
import com.sabo.sabostorev2.RoomDB.User.UserDataSource
import com.sabo.sabostorev2.ui.SignIn.SignIn

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_delete_account.*
import maes.tech.intentanim.CustomIntent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeleteAccount : AppCompatActivity(), View.OnClickListener {

    private var mService: APIRequestData? = null
    private var compositeDisposable: CompositeDisposable?= null
    private var userDataSource: UserDataSource? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_account)

        mService = Common.getAPI()
        compositeDisposable = CompositeDisposable()
        userDataSource = LocalUserDataSource(RoomDBHost.getInstance(this).userDAO())
        initViews()
    }

    private fun initViews() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Delete Account"

        findViewById<Button>(R.id.btnConfirm).setOnClickListener(this)
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
            R.id.btnConfirm -> deleteAccount()
        }
    }

    private fun deleteAccount() {
        val sweetLoading = SweetAlertDialog(this@DeleteAccount, SweetAlertDialog.PROGRESS_TYPE)
        sweetLoading.progressHelper.barColor = resources.getColor(R.color.colorAccent)
        sweetLoading.setCanceledOnTouchOutside(false)
        sweetLoading.titleText = "Please wait..."

        SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Delete this account")
                .showCancelButton(true)
                .setCancelText("No")
                .setCancelClickListener { sweetAlertDialog: SweetAlertDialog ->
                    sweetAlertDialog.dismissWithAnimation()
                }
                .setConfirmText("Yes")
                .setConfirmClickListener { sweetAlertDialog: SweetAlertDialog ->
                    sweetAlertDialog.dismissWithAnimation()
                    sweetLoading.show()

                    val uid = Preferences.getUID(this)
                    val email = etEmail.text.toString()
                    val password = etPassword.text.toString()

                    removeUserPhoto(uid)
                    /** Remove User Photo */
                    mService!!.removeAccount(uid, email, password).enqueue(object : Callback<ResponseModel> {
                        override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                            val code = response.body()!!.code
                            val message = response.body()!!.message

                            if (code == 4) {
                                sweetLoading.dismissWithAnimation()
                                userDataSource!!.removeAccount(uid)
                                Preferences.clearAllPreferences(this@DeleteAccount)
                                SweetAlertDialog(this@DeleteAccount, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Success!")
                                        .setContentText(message)
                                        .setConfirmText("Exit")
                                        .setConfirmClickListener { sweetAlertDialog: SweetAlertDialog ->
                                            sweetAlertDialog.dismissWithAnimation()
                                            startActivity(Intent(this@DeleteAccount, SignIn::class.java))
                                            finish()
                                        }
                                        .show()
                            } else
                                sweetLoading.setTitleText("Oops!")
                                        .setContentText(message)
                                        .setConfirmClickListener {
                                            sweetLoading.dismissWithAnimation()
                                        }
                                        .changeAlertType(SweetAlertDialog.WARNING_TYPE)

                        }

                        override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                            sweetLoading.setTitleText("Oops!")
                                    .setContentText(t.message)
                                    .setConfirmClickListener {
                                        sweetLoading.dismissWithAnimation()
                                    }
                                    .changeAlertType(SweetAlertDialog.WARNING_TYPE)
                        }
                    })

                }.show()

    }

    private fun removeUserPhoto(uid: String) {
        compositeDisposable!!.add(userDataSource!!.getUser(uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { user: User ->

                    mService!!.removeUserImage(user.uid, user.image).enqueue(object : Callback<ResponseModel>{
                        override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                            Log.d("removeAccount", response.body()!!.message)
                        }
                        override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                            //Log.d("removeAccount", t.message)
                        }
                    })
                })
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable!!.clear()
    }
}