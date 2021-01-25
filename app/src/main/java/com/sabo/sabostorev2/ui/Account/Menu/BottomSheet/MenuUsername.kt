package com.sabo.sabostorev2.ui.Account.Menu.BottomSheet

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
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
import kotlinx.android.synthetic.main.fragment_menu_username.*
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuUsername : BottomSheetDialogFragment(), View.OnClickListener {

    companion object {
        private var instance: MenuUsername? = null
        fun getInstance(): MenuUsername? {
            if (instance == null)
                instance = MenuUsername()
            return instance
        }
    }

    private var mService: APIRequestData? = null
    private var compositeDisposable: CompositeDisposable?= null
    private var userDataSource: UserDataSource?= null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_menu_username, container, false)

        mService = Common.getAPI()
        compositeDisposable = CompositeDisposable()
        userDataSource = LocalUserDataSource(RoomDBHost.getInstance(root.context).userDAO())
        initViews(root)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (requireView().parent as View).setBackgroundColor(Color.TRANSPARENT)
    }

    override fun onResume() {
        super.onResume()
        etUsername.requestFocus()
        etUsername.setText(Common.currentUser.username)
        etPassword.setText("")
    }

    private fun initViews(root: View) {
        root.findViewById<Button>(R.id.btnCancel).setOnClickListener(this)
        root.findViewById<Button>(R.id.btnConfirm).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnCancel -> {
                etPassword!!.setText("")
                instance!!.dismiss()
            }
            R.id.btnConfirm -> {
                reauth()
            }
        }
    }

    private fun reauth() {
        val uid = Preferences.getUID(context)
        val email = Common.currentUser.email
        val username = etUsername.text.toString()
        val password = etPassword.text.toString()
        if (username.isNullOrEmpty()) {
            tvUsernameError.text = "Username - Can't be null"
            tvUsernameError.setTextColor(requireContext().resources.getColor(android.R.color.holo_red_dark))
            etUsername.background = requireContext().resources.getDrawable(R.drawable.border_danger)
            etUsername.requestFocus()
        } else if (username.contains(" ")) {
            tvUsernameError.text = "Username - Can't be whitespace"
            tvUsernameError.setTextColor(requireContext().resources.getColor(android.R.color.holo_red_dark))
            etUsername.background = requireContext().resources.getDrawable(R.drawable.border_danger)
            etUsername.requestFocus()
        }
        else {
            tvUsernameError.text = "Username"
            tvUsernameError.setTextColor(requireContext().resources.getColor(R.color.colorAccent))
            etUsername.background = requireContext().resources.getDrawable(R.drawable.border_accent)

            val sweetLoading = SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
            sweetLoading.progressHelper.barColor = requireContext().resources.getColor(R.color.colorAccent)
            sweetLoading.titleText = "Please wait..."
            sweetLoading.setCanceledOnTouchOutside(false)
            sweetLoading.show()

            mService!!.updateUserEmailUsername(uid, password, email, username).enqueue(object : Callback<ResponseModel> {
                override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                    val code: Int = response.body()!!.code
                    val message: String = response.body()!!.message
                    if (code == 1) {
                        sweetLoading.dismissWithAnimation()
                        tvPasswordError.text = "Current Password - $message"
                        tvPasswordError.setTextColor(requireContext().resources.getColor(android.R.color.holo_red_dark))
                        etPassword.background = requireContext().resources.getDrawable(R.drawable.border_danger)
                        etPassword.requestFocus()
                        etPassword.setText("")
                    }
                    if (code == 2) {
                        sweetLoading.dismissWithAnimation()
                        tvPasswordError.text = "Current Password"
                        tvPasswordError.setTextColor(requireContext().resources.getColor(R.color.colorAccent))
                        etPassword.background = requireContext().resources.getDrawable(R.drawable.border_accent)
                        SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Oops!")
                                .setContentText(message)
                                .show()
                    }
                    if (code == 3) {
                        tvPasswordError.text = "Current Password"
                        tvPasswordError.setTextColor(requireContext().resources.getColor(R.color.colorAccent))
                        etPassword.background = requireContext().resources.getDrawable(R.drawable.border_accent)

                        Handler().postDelayed({
                            sweetLoading.dismissWithAnimation()
                            instance!!.dismiss()
                            etPassword.setText("")
                            val t = Toast.makeText(context, "Username has changed", Toast.LENGTH_SHORT)
                            t.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                            t.show()
                        }, 1000)

                        val userModel = response.body()!!.user
                        Common.currentUser = userModel

                        val user = User()
                        user.uid = userModel.uid
                        user.email = userModel.email
                        user.username = userModel.username
                        user.nickname = userModel.nickname
                        user.image = userModel.image
                        user.phone = userModel.phone
                        user.countryCode = userModel.countryCode
                        user.gender = userModel.gender

                        compositeDisposable!!.add(userDataSource!!.insertOrUpdateUser(user)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    EventBus.getDefault().postSticky(UpdateProfileEvent(true))
                                })
                                { throwable: Throwable ->
                                    Log.d("user", throwable.message)
                                })
                    }

                }

                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                    sweetLoading.dismissWithAnimation()
                    SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Oops!")
                            .setContentText(t.message)
                            .show()
                }
            })
        }
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable!!.clear()
    }
}
