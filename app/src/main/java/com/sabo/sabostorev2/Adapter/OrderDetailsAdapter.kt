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
import com.squareup.picasso.Picasso

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
        val itemUrl = Common.ITEMS_URL
        var resultUrl = ""
        when (list.itemId) {
            "item-01" -> resultUrl = itemUrl + "item-01/" + list.itemImage
            "item-02" -> resultUrl = itemUrl + "item-02/" + list.itemImage
            "item-03" -> resultUrl = itemUrl + "item-03/" + list.itemImage
            "item-04" -> resultUrl = itemUrl + "item-04/" + list.itemImage
            "item-05" -> resultUrl = itemUrl + "item-05/" + list.itemImage
            "item-06" -> resultUrl = itemUrl + "item-06/" + list.itemImage
            "item-07" -> resultUrl = itemUrl + "item-07/" + list.itemImage
            "item-08" -> resultUrl = itemUrl + "item-08/" + list.itemImage
        }
        Picasso.get().load(resultUrl).placeholder(R.drawable.ic_github).into(holder.ivItemImg)
        holder.tvItemName.text = list.itemName
        holder.tvItemQuantity.text = "${list.itemQuantity} pcs"
        holder.tvItemPrice.text = "$ ${Common.formatPriceUSDToDouble(list.itemPrice)} /pcs"
    }

    override fun getItemCount(): Int {
        return orderDetailsModelList.size
    }
}