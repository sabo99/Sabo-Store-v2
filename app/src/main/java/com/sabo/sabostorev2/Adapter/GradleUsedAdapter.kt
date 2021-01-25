package com.sabo.sabostorev2.Adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sabo.sabostorev2.Model.GradleUsed.GradleUsedModel
import com.sabo.sabostorev2.R

class GradleUsedAdapter(private val context: Context, private val gradleUsedModelList: List<GradleUsedModel>) : RecyclerView.Adapter<GradleUsedAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvGradle: TextView = itemView.findViewById(R.id.tvGradle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_gradle, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = gradleUsedModelList[position]
        if (list.gradleName!!.isNotEmpty()) {
            val text = SpannableString(list.gradleName)
            text.setSpan(UnderlineSpan(), 0, text.length, 0)
            holder.tvGradle.text = text
        }

        holder.tvGradle.setOnClickListener {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(list.gradleLink)))
        }

    }

    override fun getItemCount(): Int {
        return gradleUsedModelList.size
    }
}