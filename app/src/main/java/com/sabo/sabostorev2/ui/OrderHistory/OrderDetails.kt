package com.sabo.sabostorev2.ui.OrderHistory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import com.sabo.sabostorev2.API.APIRequestData
import com.sabo.sabostorev2.Adapter.OrderDetailsAdapter
import com.sabo.sabostorev2.Common.Common
import com.sabo.sabostorev2.Model.ResponseModel
import com.sabo.sabostorev2.R
import kotlinx.android.synthetic.main.activity_order_details.*
import maes.tech.intentanim.CustomIntent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderDetails : AppCompatActivity() {

    private var mService: APIRequestData?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)

        mService = Common.getAPI()

        initViews()
    }

    private fun initViews() {
        rvOrderDetails.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }

        Handler().postDelayed({
            loadOrderDetails()
        }, 500)

    }

    private fun loadOrderDetails() {
        val sweetLoading = SweetAlertDialog(this@OrderDetails, SweetAlertDialog.PROGRESS_TYPE)
        sweetLoading.titleText = "Please wait..."
        sweetLoading.progressHelper.barColor = resources.getColor(R.color.colorAccent)
        sweetLoading.show()

        mService!!.getOrderDetails(intent.getStringExtra("orderID")).enqueue(object : Callback<ResponseModel>{
            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                sweetLoading.dismissWithAnimation()
                rvOrderDetails.adapter = OrderDetailsAdapter(this@OrderDetails, response.body()!!.orderDetails)
                var totalPrice = intent.getStringExtra("orderID")
                tvTotalPrice.text = "Total Price : $ $totalPrice"
            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                sweetLoading.titleText = "Oops!"
                sweetLoading.contentText = t.message
                sweetLoading.setConfirmClickListener { sweetLoading.dismissWithAnimation() }
                sweetLoading.changeAlertType(SweetAlertDialog.WARNING_TYPE)
            }
        })
    }

    override fun finish() {
        super.finish()
        CustomIntent.customType(this, Common.FINFOUT)
    }
}