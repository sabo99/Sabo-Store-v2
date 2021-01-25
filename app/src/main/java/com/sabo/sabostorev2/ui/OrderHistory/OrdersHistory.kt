package com.sabo.sabostorev2.ui.OrderHistory

import android.os.Bundle
import android.os.Handler
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
    private var orderModelList: List<OrdersModel> = ArrayList()
    private var uid = ""
    private var isShowFilter = false

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
        loadOrdersHistory()
    }

    private fun loadOrdersHistory() {
        progressBar.visibility = View.VISIBLE
        rvOrdersHistory.visibility = View.INVISIBLE

        mService!!.getOrders(uid).enqueue(object : Callback<ResponseModel> {
            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                val code = response.body()!!.code
                if (code == 1) {
                    orderModelList = response.body()!!.orders
                    rvOrdersHistory.adapter = OrdersHistoryAdapter(this@OrdersHistory, orderModelList)
                }
                if (code == 0) {
                    progressBar.visibility = View.INVISIBLE
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

    private fun showFilter() {
        val linear = LinearLayout(this)
        val spFilter = Spinner(this)
        val adapter= ArrayAdapter.createFromResource(this, R.array.filter_order_history, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        linear.addView(spFilter)
        setContentView(linear)
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