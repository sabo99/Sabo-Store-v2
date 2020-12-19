package com.sabo.sabostorev2.Account.Menu

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.github.chrisbanes.photoview.PhotoView
import com.sabo.sabostorev2.Common.Common
import com.sabo.sabostorev2.R
import com.squareup.picasso.Picasso
import maes.tech.intentanim.CustomIntent

class PreviewProfilePhoto : AppCompatActivity() {

    private var pvProfilePhoto: PhotoView ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_profile_photo)

        initViews()
    }

    private fun initViews() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Profile photo"

        pvProfilePhoto = findViewById(R.id.pvProfilePhoto)
    }

    override fun onResume() {
        super.onResume()
        loadProfilePhoto()
    }

    private fun loadProfilePhoto() {
        val image: String = intent.getStringExtra(Common.INTENT_IMG)
        if (image.isNotEmpty()){
            val img: String = Common.USER_IMAGE_URL + image
            Picasso.get().load(img).placeholder(R.drawable.no_profile).into(pvProfilePhoto)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                CustomIntent.customType(this, Common.FINFOUT)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun finish() {
        super.finish()
        CustomIntent.customType(this, Common.FINFOUT)
    }
}