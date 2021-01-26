package com.sabo.sabostorev2.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sabo.sabostorev2.Common.Common
import com.sabo.sabostorev2.Model.Item.ItemsModel
import com.sabo.sabostorev2.R
import com.sabo.sabostorev2.ui.ItemDetails.ItemDetails
import com.squareup.picasso.Picasso
import maes.tech.intentanim.CustomIntent

class SearchAdapter(private val context: Context, private val itemsModelList: List<ItemsModel>) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemClick: LinearLayout = itemView.findViewById(R.id.itemClick)
        val ivItemImg: ImageView = itemView.findViewById(R.id.ivItemImg)
        val tvItemName: TextView = itemView.findViewById(R.id.tvItemName)
        val tvItemPrice: TextView = itemView.findViewById(R.id.tvItemPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_search, parent, false))
    }

    override fun onBindViewHolder(holder: SearchAdapter.ViewHolder, position: Int) {
        val list = itemsModelList[position]
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
        Picasso.get().load(resultUrl).into(holder.ivItemImg)
        holder.tvItemName.text = list.name
        holder.tvItemPrice.text = "$ ${Common.formatPriceUSDToDouble(list.price.div(Common.ratesIDR))}"

        holder.itemClick.setOnClickListener {
            Common.itemDetails = list
            context.startActivity(Intent(context, ItemDetails::class.java))
            CustomIntent.customType(context, Common.LTR)
        }
    }

    override fun getItemCount(): Int {
        return itemsModelList.size
    }
}