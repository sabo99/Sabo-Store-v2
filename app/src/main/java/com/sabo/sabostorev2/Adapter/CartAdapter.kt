package com.sabo.sabostorev2.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sabo.sabostorev2.Common.Common
import com.sabo.sabostorev2.R
import com.sabo.sabostorev2.RoomDB.Cart.Cart

class CartAdapter(private val context: Context, private val cartList: List<Cart>) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvQty: TextView = itemView.findViewById(R.id.tvQty)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = cartList[position]
        holder.tvName.text = list.itemName
        holder.tvPrice.text = "Price : $ ${Common.formatPriceUSDToDouble(list.itemPrice)}"
        holder.tvQty.text = "Quantity : ${list.itemQuantity}"
    }

    override fun getItemCount(): Int {
         return cartList.size
    }
}