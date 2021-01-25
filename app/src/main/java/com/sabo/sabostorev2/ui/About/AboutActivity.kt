package com.sabo.sabostorev2.ui.About

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.sabo.sabostorev2.Adapter.GradleUsedAdapter
import com.sabo.sabostorev2.Common.Common
import com.sabo.sabostorev2.Model.GradleUsed.GradleUsedData
import com.sabo.sabostorev2.R
import kotlinx.android.synthetic.main.activity_about.*
import maes.tech.intentanim.CustomIntent

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        initViews()
    }

    override fun finish() {
        super.finish()
        CustomIntent.customType(this, Common.RTL)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    private fun initViews() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "About"

//        rvGradle.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvGradle.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rvGradle.setHasFixedSize(true)

        val adapter = GradleUsedAdapter(this, GradleUsedData.getListData())
        rvGradle.adapter = adapter
    }

}