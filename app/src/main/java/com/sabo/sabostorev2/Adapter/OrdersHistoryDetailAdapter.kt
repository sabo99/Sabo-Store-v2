package com.sabo.sabostorev2.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sabo.sabostorev2.Common.Common
import com.sabo.sabostorev2.Model.Order.OrderDetailsModel
import com.sabo.sabostorev2.R


class OrdersHistoryDetailAdapter(private val context: Context, private val ordersDetailsModelList: List<OrderDetailsModel>): RecyclerView.Adapter<OrdersHistoryDetailAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvItemName: TextView = itemView.findViewById(R.id.tvItemName)
        val tvItemQuantity: TextView = itemView.findViewById(R.id.tvItemQuantity)
        val tvItemPrice: TextView = itemView.findViewById(R.id.tvItemPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersHistoryDetailAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_orders_history_detail, parent, false))
    }

    override fun onBindViewHolder(holder: OrdersHistoryDetailAdapter.ViewHolder, position: Int) {
        val list = ordersDetailsModelList[position]

        holder.tvItemName.text = list.itemName
        holder.tvItemQuantity.text = "${list.itemQuantity} pcs"
        holder.tvItemPrice.text = "$ ${Common.formatPriceUSDToDouble(list.itemPrice)}"
    }

    override fun getItemCount(): Int {
        return ordersDetailsModelList.size
    }
}