package com.sabo.sabostorev2.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.sabo.sabostorev2.Common.Common
import com.sabo.sabostorev2.EventBus.OnCategoriesSelectedEvent
import com.sabo.sabostorev2.Model.Item.ItemStoreModel
import com.sabo.sabostorev2.R
import com.sabo.sabostorev2.ui.Categories.Categories
import com.squareup.picasso.Picasso
import maes.tech.intentanim.CustomIntent
import org.greenrobot.eventbus.EventBus

class HomeCategoriesAdapter(private val context: Context, private val itemStoreModelList: List<ItemStoreModel>) : RecyclerView.Adapter<HomeCategoriesAdapter.ViewHolder>() {
    private var lastPosition = -1

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivItemImg: ImageView = itemView.findViewById(R.id.ivItemImg)
        val tvItemName: TextView = itemView.findViewById(R.id.tvItemName)
        val tvItemDescription: TextView = itemView.findViewById(R.id.tvItemDescription)
        val viewClick: View = itemView.findViewById(R.id.viewClick)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_home_categories, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = itemStoreModelList[position]
        val itemUrl = Common.ITEMS_URL
        var resultUrl = ""
        when (list.id) {
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
        holder.tvItemDescription.text = list.description
        setAnimation(holder.itemView, position)

        holder.viewClick.setOnClickListener {
            EventBus.getDefault().postSticky(OnCategoriesSelectedEvent(true, list))
            context.startActivity(Intent(context, Categories::class.java))
            CustomIntent.customType(context, Common.LTR)

        }
    }

    private fun setAnimation(view: View, position: Int) {
        if (position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(context, R.anim.slide_rv_item_right)
            view.startAnimation(animation)
            lastPosition = position
        }
    }

    override fun getItemCount(): Int {
        return itemStoreModelList.size
    }
}
