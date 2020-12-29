package com.sabo.sabostorev2.Account.Menu.BottomSheet

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import com.sabo.sabostorev2.API.API
import com.sabo.sabostorev2.Common.Common
import com.sabo.sabostorev2.Common.Preferences
import com.sabo.sabostorev2.EventBus.UpdateProfileEvent
import com.sabo.sabostorev2.Model.ResponseModel
import com.sabo.sabostorev2.R
import com.sabo.sabostorev2.RoomDB.RoomDBHost
import com.sabo.sabostorev2.RoomDB.User.LocalUserDataSource
import com.sabo.sabostorev2.RoomDB.User.User
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MenuGender : BottomSheetDialogFragment(), View.OnClickListener {

    companion object {
        private var instance: MenuGender? = null
        fun getInstance(): MenuGender? {
            if (instance == null)
                instance = MenuGender()
            return instance
        }
    }

    private var mService: API? = null
    private var compositeDisposable: CompositeDisposable? = null
    private var localUserDataSource: LocalUserDataSource? = null

    private var rgGender: RadioGroup? = null
    private var rbMale: RadioButton? = null
    private var rbFemale: RadioButton? = null
    private var resultGender: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root: View = inflater.inflate(R.layout.fragment_menu_gender, container, false)

        mService = Common.getAPI()
        compositeDisposable = CompositeDisposable();
        localUserDataSource = LocalUserDataSource(RoomDBHost.getInstance(root.context).userDAO())
        initViews(root)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (requireView().parent as View).setBackgroundColor(Color.TRANSPARENT)
    }

    override fun onResume() {
        super.onResume()
        var gender = Common.currentUser.gender
        if (gender == 1)
            rbMale!!.isChecked = true

        if (gender == 0)
            rbFemale!!.isChecked = true
    }

    private fun initViews(root: View) {
        rgGender = root.findViewById(R.id.rgGender)
        rbMale = root.findViewById(R.id.rbMale)
        rbFemale = root.findViewById(R.id.rbFemale)
        root.findViewById<Button>(R.id.btnCancel).setOnClickListener(this)
        root.findViewById<Button>(R.id.btnConfirm).setOnClickListener(this)

        rgGender!!.setOnCheckedChangeListener { group, checkedId ->
            for (i in 0 until group.childCount) {
                val rb = group.getChildAt(i) as RadioButton
                if (rb.id == checkedId) {
                    val text = rb.text.toString()
                    resultGender = text
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnCancel -> {
                instance!!.dismiss()
            }
            R.id.btnConfirm -> {
                changeGender()
            }
        }
    }

    private fun changeGender() {
        val uid = Preferences.getUID(context)
        val gender = Common.gender2Integer(resultGender)

        val sweetLoading = SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
        sweetLoading.progressHelper.barColor = requireContext().resources.getColor(R.color.colorAccent)
        sweetLoading.titleText = "Please wait..."
        sweetLoading.setCanceledOnTouchOutside(false)
        sweetLoading.show()

        mService!!.updateUserGender(uid, gender).enqueue(object : Callback<ResponseModel> {
            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                val code = response.body()!!.code
                val message = response.body()!!.message
                if (code == 1) {
                    sweetLoading.setTitleText("Oops!")
                            .setContentText(message)
                            .setConfirmClickListener {
                                sweetLoading.dismissWithAnimation()
                            }
                            .changeAlertType(SweetAlertDialog.WARNING_TYPE)
                }
                if (code == 2) {
                    Handler().postDelayed({
                        sweetLoading.dismissWithAnimation()
                        instance!!.dismiss()
                        Toast.makeText(context, "Gender has changed", Toast.LENGTH_SHORT).show()
                    }, 500)

                    val userModel = response.body()!!.user
                    Common.currentUser = userModel
                    val user = User()
                    user.uid = userModel.uid
                    user.email = userModel.email
                    user.username = userModel.username
                    user.image = userModel.image
                    user.phone = userModel.phone
                    user.countryCode = userModel.countryCode
                    user.gender = userModel.gender

                    compositeDisposable!!.add(localUserDataSource!!.insertOrUpdateUser(user)
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
                sweetLoading.setTitleText("Oops!")
                        .setContentText(t.message)
                        .setConfirmClickListener {
                            sweetLoading.dismissWithAnimation()
                        }
                        .changeAlertType(SweetAlertDialog.WARNING_TYPE)
            }
        })
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable!!.clear()
    }

}