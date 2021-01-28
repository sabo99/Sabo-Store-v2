@file:Suppress("DEPRECATION")

package com.sabo.sabostorev2.ui.ItemDetails

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import com.sabo.sabostorev2.API.APIRequestData
import com.sabo.sabostorev2.Common.Common
import com.sabo.sabostorev2.Model.Item.ItemsModel
import com.sabo.sabostorev2.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_item_details.*
import kotlinx.android.synthetic.main.content_item_details.*
import maes.tech.intentanim.CustomIntent

class ItemDetails : AppCompatActivity() {

    private var mService: APIRequestData?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_details)

        mService = Common.getAPI()
        initViews()
    }

    private fun initViews() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = ""

        loadItemDetails()
    }

    @SuppressLint("SetTextI18n")
    private fun loadItemDetails() {
        val sweetLoading = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        sweetLoading.progressHelper.barColor = resources.getColor(R.color.colorAccent)
        sweetLoading.titleText = "Please wait..."
        sweetLoading.setCanceledOnTouchOutside(false)
        sweetLoading.show()

        Handler().postDelayed({
            val list = Common.itemDetails
            sweetLoading.dismissWithAnimation()
            val itemUrl = Common.ITEMS_URL
            var resultUrl = ""
            when (list.itemId) {
                "item-01" -> resultUrl = itemUrl + "item-01/" + list.image
                "item-02" -> resultUrl = itemUrl + "item-02/" + list.image
                "item-03" -> resultUrl = itemUrl + "item-03/" + list.image
                "item-04" -> resultUrl = itemUrl + "item-04/" + list.image
                "item-05" -> resultUrl = itemUrl + "item-05/" + list.image
                "item-06" -> resultUrl = itemUrl + "item-06/" + list.image
                "item-07" -> resultUrl = itemUrl + "item-07/" + list.image
                "item-08" -> resultUrl = itemUrl + "item-08/" + list.image
            }
            Picasso.get().load(resultUrl).placeholder(R.drawable.ic_github).into(ivItemImg)
            tvItemName.text = list.name
            tvItemPrice.text = "$ ${Common.formatPriceUSDToDouble(list.price.div(Common.ratesIDR))}"
            tvItemDescription.text = list.description
            tvItemSpecification.text = list.specification.replace("/n", "\n")

        }, 100)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    override fun finish() {
        super.finish()
        CustomIntent.customType(this, Common.RTL)
    }
}