package com.sabo.sabostorev2.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sabo.sabostorev2.Common.Common
import com.sabo.sabostorev2.Model.Order.OrderDetailsModel
import com.sabo.sabostorev2.R

class OrderDetailsAdapter(private val context: Context, private val orderDetailsModelList: List<OrderDetailsModel>): RecyclerView.Adapter<OrderDetailsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val ivItemImg: ImageView = itemView.findViewById(R.id.ivItemImg)
        val tvItemName: TextView = itemView.findViewById(R.id.tvItemName)
        val tvItemQuantity: TextView = itemView.findViewById(R.id.tvItemQuantity)
        val tvItemPrice: TextView = itemView.findViewById(R.id.tvItemPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_order_details, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = orderDetailsModelList[position]

        holder.tvItemName.text = list.itemName
        holder.tvItemQuantity.text = "${list.itemQuantity} pcs"
        holder.tvItemPrice.text = "$ ${Common.formatPriceUSDToDouble(list.itemPrice)} /pcs"
    }

    override fun getItemCount(): Int {
        return orderDetailsModelList.size
    }
}