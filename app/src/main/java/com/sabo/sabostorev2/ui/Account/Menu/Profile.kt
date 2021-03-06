@file:Suppress("DEPRECATION")

package com.sabo.sabostorev2.ui.Account.Menu

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import com.sabo.sabostorev2.API.APIRequestData
import com.sabo.sabostorev2.Common.Common
import com.sabo.sabostorev2.Common.FileUtils
import com.sabo.sabostorev2.Common.Preferences
import com.sabo.sabostorev2.EventBus.ActionPhotoProfileEvent
import com.sabo.sabostorev2.EventBus.UpdateProfileEvent
import com.sabo.sabostorev2.Model.ResponseModel
import com.sabo.sabostorev2.Model.UserModel
import com.sabo.sabostorev2.R
import com.sabo.sabostorev2.RoomDB.RoomDBHost
import com.sabo.sabostorev2.RoomDB.User.LocalUserDataSource
import com.sabo.sabostorev2.RoomDB.User.User
import com.sabo.sabostorev2.RoomDB.User.UserDataSource
import com.sabo.sabostorev2.ui.Account.Menu.BottomSheet.*
import com.sabo.sabostorev2.ui.Account.Menu.SubMenu.ChangePhone
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_profile.*
import maes.tech.intentanim.CustomIntent
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*


class Profile : AppCompatActivity(), View.OnClickListener {

    private var mService: APIRequestData? = null
    private var compositeDisposable: CompositeDisposable? = null
    private var userDataSource: UserDataSource? = null

    private var pickImgUri: Uri? = null
    private var resultImgUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        mService = Common.getAPI()
        compositeDisposable = CompositeDisposable()
        userDataSource = LocalUserDataSource(RoomDBHost.getInstance(this).userDAO())

        initViews()
    }

    private fun initViews() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Profile"

        findViewById<View>(R.id.previewProfilePhoto).setOnClickListener(this)
        findViewById<ImageButton>(R.id.menuPhotoProfile).setOnClickListener(this)
        findViewById<RelativeLayout>(R.id.menuUsername).setOnClickListener(this)
        findViewById<RelativeLayout>(R.id.menuNickname).setOnClickListener(this)
        findViewById<RelativeLayout>(R.id.menuEmail).setOnClickListener(this)
        findViewById<RelativeLayout>(R.id.menuPhone).setOnClickListener(this)
        findViewById<RelativeLayout>(R.id.menuGender).setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onResume() {
        super.onResume()
        loadData()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
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
                    if (user.phone != "") {
                        if (user.countryCode != null)
                            tvPhone.text =
                                    StringBuilder().append(user.countryCode)
                                            .append(" ")
                                            .append(Common.formatPhoneNumber(this@Profile, user.phone)).toString()
                        else
                            tvPhone.text =
                                    StringBuilder().append(" ")
                                            .append(Common.formatPhoneNumber(this@Profile, user.phone)).toString()
                    } else
                        tvPhone.text = "None"

                    tvNickname.text = user.nickname
                    tvGender.text = Common.genderToString(user.gender)

                    val userModel = UserModel()
                    userModel.uid = user.uid
                    userModel.email = user.email
                    userModel.username = user.username
                    userModel.nickname = user.nickname
                    userModel.image = user.image
                    userModel.phone = user.phone
                    userModel.gender = user.gender!!
                    Common.currentUser = userModel
                })
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.previewProfilePhoto -> {
                val i = Intent(this, PreviewProfilePhoto::class.java)
                i.putExtra(Common.INTENT_IMG, Common.currentUser.image)
                startActivity(i)
                CustomIntent.customType(this, Common.FINFOUT)
            }
            R.id.menuPhotoProfile -> {
                val menuPhotoProfile = MenuPhotoProfile.getInstance()!!
                menuPhotoProfile.show(supportFragmentManager, "MenuPhotoProfile")
            }
            R.id.menuUsername -> {
                val menuUsername = MenuUsername.getInstance()!!
                menuUsername.show(supportFragmentManager, "MenuUsername")
            }
            R.id.menuNickname -> {
                val menuNickname = MenuNickname.getInstance()!!
                menuNickname.show(supportFragmentManager, "MenuNickname")
            }
            R.id.menuEmail -> {
                val menuEmail = MenuEmail.getInstance()!!
                menuEmail.show(supportFragmentManager, "MenuEmail")
            }
            R.id.menuPhone -> {
                startActivity(Intent(this, ChangePhone::class.java))
                CustomIntent.customType(this, Common.LTR)
            }
            R.id.menuGender -> {
                val menuGender = MenuGender.getInstance()!!
                menuGender.show(supportFragmentManager, "MenuGender")
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    override fun finish() {
        super.finish()
        CustomIntent.customType(this, Common.FINFOUT)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable!!.clear()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun updateProfileEvent(event: UpdateProfileEvent) {
        if (event.isUpdated) {
            event.isUpdated = false
            loadData()
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun actionPhotoProfileEvent(event: ActionPhotoProfileEvent) {
        if (event.isChange) {
            event.isChange = false
            checkReqPermission()
        }

        if (event.isRemove) {
            event.isRemove = false
            val image = Common.currentUser.image
            if (image == "default.png") {
                Toast.makeText(this@Profile, "No profile photo", Toast.LENGTH_SHORT).show()
                return
            }

            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Remove")
                    .setContentText("Profile photo?")
                    .showCancelButton(true)
                    .setCancelText("Cancel")
                    .setCancelClickListener { sweetAlertDialog: SweetAlertDialog -> sweetAlertDialog.dismiss() }
                    .setConfirmText("Remove")
                    .setConfirmClickListener { sweetAlertDialog: SweetAlertDialog ->
                        sweetAlertDialog.dismiss()
                        removePhoto()
                    }
                    .show()
        }
    }

    private fun removePhoto() {
        val uid = Preferences.getUID(this)
        val image = Common.currentUser.image

        val pleaseWait = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pleaseWait.progressHelper.barColor = resources.getColor(R.color.colorPrimary)
        pleaseWait.titleText = "Please wait..."
        pleaseWait.show()

        mService!!.removeUserImage(uid, image).enqueue(object : Callback<ResponseModel> {
            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                val message = response.body()!!.message

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
                        .subscribe {
                            pleaseWait.dismissWithAnimation()
                            Picasso.get().load(R.drawable.no_profile).into(civProfile)
                            Toast.makeText(this@Profile, message, Toast.LENGTH_SHORT).show()
                        })
            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                pleaseWait.setTitleText("Oops!")
                        .setContentText("No Connection")
                        .setConfirmText("Close")
                        .setConfirmClickListener {
                            pleaseWait.dismissWithAnimation()
                        }
                        .changeAlertType(SweetAlertDialog.WARNING_TYPE)
            }
        })
    }

    private fun checkReqPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            requestPermissions(arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA),
                    Common.REQUEST_PERMISSION_CHANGE_PHOTO)
        else {
            if (pickImgUri == null)
                openPickImgChooser()
            else
                return
        }
    }

    private fun openPickImgChooser() {
        CropImage.startPickImageActivity(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == Common.REQUEST_PERMISSION_CHANGE_PHOTO && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (pickImgUri == null)
                openPickImgChooser()
            else
                return
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            pickImgUri = CropImage.getPickImageResultUri(this, data)
            CropImage.activity(pickImgUri)
                    .setFixAspectRatio(true)
                    .setRequestedSize(500, 500)
                    .start(this)
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && data != null) {
            val result: CropImage.ActivityResult = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                resultImgUri = result.uri

                val uploading = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                uploading.progressHelper.barColor = resources.getColor(R.color.colorPrimary)
                uploading.titleText = "Uploading..."
                uploading.show()

                Handler().postDelayed({
                    uploadImgToDB(resultImgUri, uploading)
                }, 1000)
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && data == null) {
            Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show()
            pickImgUri = null
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Toast.makeText(this, "Something Error with Crop Image", Toast.LENGTH_SHORT).show()
            pickImgUri = null
        } else
            pickImgUri = null


    }

    private fun uploadImgToDB(result: Uri?, uploading: SweetAlertDialog) {

        /** File Size */
        val fileSize = FileUtils.getFileSizeFromUri(this, result)
        if (fileSize > Common.MAX_SIZE_UPLOAD) {
            uploading.setTitleText("Oops!")
                    .setContentText("File size too large")
                    .setConfirmText("Close")
                    .setConfirmClickListener {
                        uploading.dismiss()
                    }.changeAlertType(SweetAlertDialog.WARNING_TYPE)
            pickImgUri = null
            resultImgUri = null
        } else {
            /** File Path */
            val filePath: String = FileUtils.getFilePathFromUri(this, result)

            /** File Name & Extension*/
            val fileNameFromUri: String = FileUtils.getFileNameFromUri(result)
            val parts = fileNameFromUri.split(".")
            val name = System.currentTimeMillis()
            val extension = parts[1]
            val fileName = "$name.$extension"

            if (filePath.isNotEmpty()) {
                val file = File(filePath)

                val requestBody = RequestBody.create(MediaType.parse("*/*"), file) as RequestBody
                val uid = MultipartBody.Part.createFormData("uid", Common.currentUser.uid!!)
                val fileBody = MultipartBody.Part.createFormData("file", fileName, requestBody)
                val oldFileName = MultipartBody.Part.createFormData("oldFileName", Common.currentUser.image!!)

                mService!!.updateUserImage(uid, fileBody, oldFileName).enqueue(object : Callback<ResponseModel> {
                    override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                        val code = response.body()!!.code
                        val message = response.body()!!.message
                        if (code == 1) {

                            val userModel = response.body()!!.user
                            Common.currentUser.image = fileName
                            val user = User()
                            user.uid = userModel.uid
                            user.email = userModel.email
                            user.username = userModel.username
                            user.nickname = userModel.nickname
                            user.image = fileName
                            user.phone = userModel.phone
                            user.countryCode = userModel.countryCode
                            user.gender = userModel.gender

                            compositeDisposable!!.add(userDataSource!!.insertOrUpdateUser(user)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe {
                                        uploading.dismissWithAnimation()
                                        SweetAlertDialog(this@Profile, SweetAlertDialog.SUCCESS_TYPE)
                                                .setTitleText("Success!")
                                                .setContentText(message)
                                                .show()
                                        Picasso.get().load(result).placeholder(R.drawable.no_profile).into(civProfile)
                                        pickImgUri = null
                                        resultImgUri = null
                                    })
                        }
                    }

                    override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                        pickImgUri = null
                        resultImgUri = null
                        uploading.setTitleText("Oops!")
                                .setContentText("No Connection")
                                .setConfirmText("Close")
                                .setConfirmClickListener {
                                    uploading.dismissWithAnimation()
                                }
                                .changeAlertType(SweetAlertDialog.WARNING_TYPE)
                    }
                })
            }
        }
    }
}