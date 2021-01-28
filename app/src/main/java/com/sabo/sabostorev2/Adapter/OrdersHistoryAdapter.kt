package com.sabo.sabostorev2.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sabo.sabostorev2.API.APIRequestData
import com.sabo.sabostorev2.Common.Common
import com.sabo.sabostorev2.EventBus.OnLoadOrderHistoryEvent
import com.sabo.sabostorev2.Model.Order.OrdersModel
import com.sabo.sabostorev2.Model.ResponseModel
import com.sabo.sabostorev2.R
import com.sabo.sabostorev2.ui.OrderHistory.OrderDetails
import maes.tech.intentanim.CustomIntent
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class OrdersHistoryAdapter(private val context: Context, private val ordersModelList: List<OrdersModel>) : RecyclerView.Adapter<OrdersHistoryAdapter.ViewHolder>() {

    private var mService: APIRequestData = Common.getAPI()
    private var calendar: Calendar = Calendar.getInstance()
    private var date: Date = Date()
    private var simpleDateFormat: SimpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMore: TextView = itemView.findViewById(R.id.tvMore)
        val tvOrderID: TextView = itemView.findViewById(R.id.tvOrderID)
        val ivLogoStatus: ImageView = itemView.findViewById(R.id.ivLogoStatus)
        val tvOrderStatus: TextView = itemView.findViewById(R.id.tvOrderStatus)
        val tvOrderDate: TextView = itemView.findViewById(R.id.tvOrderDate)
        val rvOrdersHistoryDetail: RecyclerView = itemView.findViewById(R.id.rvOrdersHistoryDetail)
        val tvTotalPrice: TextView = itemView.findViewById(R.id.tvTotalPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_orders_history, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = ordersModelList[position]

        val orderIDLong = list.orderID.toLong()
        calendar.timeInMillis = orderIDLong
        date = Date(orderIDLong)
        holder.tvOrderDate.text = "${Common.orderDateFormat(calendar.get(Calendar.DAY_OF_WEEK))}  ${simpleDateFormat.format(date)}"

        holder.tvOrderID.text = "Order #${list.orderID}"
        holder.tvTotalPrice.text = "$ ${Common.formatPriceUSDToString(list.totalPrice)}"

        /** Event Order Details */
        holder.tvMore.setOnClickListener {
            val i = Intent(context, OrderDetails::class.java)
            i.putExtra("orderID", list.orderID)
            i.putExtra("totalPrice", list.totalPrice)
            context.startActivity(i)
            CustomIntent.customType(context, Common.FINFOUT)
        }

        /** Check Order Status */
        Common.checkOrderStatus(holder.ivLogoStatus, holder.tvOrderStatus, list.orderStatus)

        /** OrderDetails */
        loadOrderDetails(list, holder)

    }

    private fun loadOrderDetails(list: OrdersModel, holder: ViewHolder) {
        holder.rvOrdersHistoryDetail.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        mService.getOrderDetails(list.orderID).enqueue(object : Callback<ResponseModel> {
            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                if (response.body()!!.orderDetails != null) {
                    holder.rvOrdersHistoryDetail.adapter = OrdersHistoryDetailAdapter(context, response.body()!!.orderDetails)
                    EventBus.getDefault().postSticky(OnLoadOrderHistoryEvent(true))
                }
            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {

            }
        })
    }

    override fun getItemCount(): Int {
        return ordersModelList.size
    }
}