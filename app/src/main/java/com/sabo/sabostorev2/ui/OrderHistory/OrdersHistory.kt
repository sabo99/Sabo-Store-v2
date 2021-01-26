@file:Suppress("DEPRECATION")

package com.sabo.sabostorev2.ui.OrderHistory

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import com.sabo.sabostorev2.API.APIRequestData
import com.sabo.sabostorev2.Adapter.OrdersHistoryAdapter
import com.sabo.sabostorev2.Common.Common
import com.sabo.sabostorev2.Common.Preferences
import com.sabo.sabostorev2.EventBus.OnLoadOrderHistoryEvent
import com.sabo.sabostorev2.Model.Order.OrdersModel
import com.sabo.sabostorev2.Model.ResponseModel
import com.sabo.sabostorev2.R
import kotlinx.android.synthetic.main.activity_orders_history.*
import maes.tech.intentanim.CustomIntent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrdersHistory : AppCompatActivity() {

    private var mService: APIRequestData? = null
    private var uid = ""
    private var isShowFilter = false
    private var sessionOrderStatus = -1
    private var isFilter = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders_history)

        mService = Common.getAPI()
        uid = Preferences.getUID(this)
        initViews()
    }

    private fun initViews() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Orders History"

        rvOrdersHistory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    override fun onResume() {
        super.onResume()
        if (isFilter && sessionOrderStatus != -1)
            loadOrdersHistoryByStatus(sessionOrderStatus)
        else
            loadOrdersHistory()
    }

    private fun loadOrdersHistory() {
        progressBar.visibility = View.VISIBLE
        rvOrdersHistory.visibility = View.INVISIBLE
        mService!!.getOrders(uid).enqueue(object : Callback<ResponseModel> {
            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                val code = response.body()!!.code
                tvEmptySearch.text = ""
                if (code == 1)
                    rvOrdersHistory.adapter = OrdersHistoryAdapter(this@OrdersHistory, response.body()!!.orders)
                if (code == 0)
                    progressBar.visibility = View.INVISIBLE
            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                progressBar.visibility = View.INVISIBLE
                val sweetLoading = SweetAlertDialog(this@OrdersHistory, SweetAlertDialog.WARNING_TYPE)
                sweetLoading.setCanceledOnTouchOutside(false)
                sweetLoading.titleText = "Oops!"
                sweetLoading.contentText = t.message
                sweetLoading.show()
            }
        })
    }

    private fun loadOrdersHistoryByStatus(sessionOrderStatus: Int) {
        progressBar.visibility = View.VISIBLE
        rvOrdersHistory.visibility = View.INVISIBLE
        mService!!.getOrdersByStatus(uid, sessionOrderStatus).enqueue(object : Callback<ResponseModel> {
            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                val code = response.body()!!.code
                if (code == 1) {
                    tvEmptySearch.text = ""
                    rvOrdersHistory.adapter = OrdersHistoryAdapter(this@OrdersHistory, response.body()!!.orders)
                }
                if (code == 0) {
                    progressBar.visibility = View.INVISIBLE
                    tvEmptySearch.text = "No results"
                    rvOrdersHistory.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                progressBar.visibility = View.INVISIBLE
                val sweetLoading = SweetAlertDialog(this@OrdersHistory, SweetAlertDialog.WARNING_TYPE)
                sweetLoading.setCanceledOnTouchOutside(false)
                sweetLoading.titleText = "Oops!"
                sweetLoading.contentText = t.message
                sweetLoading.show()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.order_history_filter, menu)
        menu.findItem(R.id.action_filter).isVisible = isShowFilter

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.action_filter -> showFilter()
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun showFilter() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_filter_order_history, null)
        val spFilter = view.findViewById(R.id.spFilter) as Spinner
        val sweetFilter = SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
        sweetFilter.titleText = "Filter View"
        sweetFilter.setOnShowListener {
            val adapter = ArrayAdapter.createFromResource(this, R.array.filter_order_history, R.layout.spinner_text_white)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spFilter.adapter = adapter

        }
        sweetFilter.setConfirmClickListener {
            sweetFilter.dismissWithAnimation()
            val result = spFilter.selectedItem.toString()

            if (result == "All") {
                sessionOrderStatus = Common.statusOrderToInteger(result)
                loadOrdersHistory()
                isFilter = false
            } else {
                sessionOrderStatus = Common.statusOrderToInteger(result)
                loadOrdersHistoryByStatus(sessionOrderStatus)
                isFilter = true
            }

        }
        sweetFilter.setCanceledOnTouchOutside(true)
        sweetFilter.show()

        val linearLayout = sweetFilter.findViewById(R.id.loading) as LinearLayout
        val confirm = sweetFilter.findViewById(R.id.confirm_button) as Button
        confirm.background = resources.getDrawable(R.drawable.accent_button_background)
        val index = linearLayout.indexOfChild(linearLayout.findViewById(R.id.content_text))
        linearLayout.addView(view, index + 1)
    }


    override fun finish() {
        super.finish()
        CustomIntent.customType(this, Common.RTL)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onLoadOrderHistory(event: OnLoadOrderHistoryEvent) {
        if (event.isLoad) {
            Handler().postDelayed({
                isShowFilter = true
                invalidateOptionsMenu()

                progressBar.visibility = View.INVISIBLE
                rvOrdersHistory.visibility = View.VISIBLE
            }, 500)
            event.isLoad = false
        }
    }
}