package com.sabo.sabostorev2.ui.Account.Menu

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.sabo.sabostorev2.Common.Common
import com.sabo.sabostorev2.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_preview_profile_photo.*
import maes.tech.intentanim.CustomIntent

class PreviewProfilePhoto : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_profile_photo)

        initViews()
    }

    private fun initViews() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Profile photo"
    }

    override fun onResume() {
        super.onResume()
        loadProfilePhoto()
    }

    private fun loadProfilePhoto() {
        val image = intent.getStringExtra(Common.INTENT_IMG)
        if (image.isNotEmpty()){
            val img = Common.USER_IMAGE_URL + image
            Picasso.get().load(img).placeholder(R.drawable.no_profile).into(pvProfilePhoto)
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
}