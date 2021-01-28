package com.sabo.sabostorev2.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.sabo.sabostorev2.Common.Common
import com.sabo.sabostorev2.Model.Item.ItemsModel
import com.sabo.sabostorev2.R
import com.smarteist.autoimageslider.SliderViewAdapter
import com.squareup.picasso.Picasso

class SliderAdapter(private val context: Context, private val itemsList: List<ItemsModel>) : SliderViewAdapter<SliderAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : SliderViewAdapter.ViewHolder(itemView) {
        val ivItemImg: ImageView = itemView.findViewById(R.id.ivItemImg)
        val tvItemName: TextView = itemView.findViewById(R.id.tvItemName)
    }

    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_slide, null))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = itemsList[position]
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
        Picasso.get().load(resultUrl).placeholder(R.drawable.ic_github).into(holder.ivItemImg)
        holder.tvItemName.text = list.name
    }

    override fun getCount(): Int {
        return itemsList.size
    }
}