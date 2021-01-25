package com.sabo.sabostorev2.Adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.sabo.sabostorev2.Common.Preferences
import com.sabo.sabostorev2.EventBus.OnLoadCategoriesEvent
import com.sabo.sabostorev2.Model.Item.ItemStoreModel
import com.sabo.sabostorev2.R
import org.greenrobot.eventbus.EventBus

class ItemCategoriesSelectionAdapter(private val context: Context, private val itemStoreModelList: List<ItemStoreModel>)
    : RecyclerView.Adapter<ItemCategoriesSelectionAdapter.ViewHolder>() {

    private var selectedItemPos = -1
    private var lastItemSelectedPos = -1

    private val capsuleUnselected = "#FFFFFF"
    private val textCapsuleUnselected = "#33B5E5"

    private val capsuleSelected = "#33B5E5"
    private val textCapsuleSelected = "#FFFFFF"


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val llCapsule: LinearLayout = itemView.findViewById(R.id.llCapsule)
        val tvCapsule: TextView = itemView.findViewById(R.id.tvCapsule)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_categories_selection, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = itemStoreModelList[position]

        holder.tvCapsule.text = list.name

        if (position == selectedItemPos)
            selectedItem(holder)
        else
            unSelectItem(holder)


        holder.itemView.setOnClickListener {
            selectedItemPos = holder.adapterPosition
            if (lastItemSelectedPos == -1) lastItemSelectedPos = selectedItemPos
            else {
                notifyItemChanged(lastItemSelectedPos)
                lastItemSelectedPos = selectedItemPos
            }
            notifyItemChanged(selectedItemPos)
            EventBus.getDefault().postSticky(OnLoadCategoriesEvent(true, list.id))
        }
    }

    override fun getItemCount(): Int {
        return itemStoreModelList.size
    }

    private fun unSelectItem(holder: ViewHolder) {
        holder.llCapsule.setBackgroundColor(Color.parseColor(capsuleUnselected))
        holder.tvCapsule.setTextColor(Color.parseColor(textCapsuleUnselected))
    }

    private fun selectedItem(holder: ViewHolder) {
        holder.llCapsule.setBackgroundColor(Color.parseColor(capsuleSelected))
        holder.tvCapsule.setTextColor(Color.parseColor(textCapsuleSelected))
    }
}