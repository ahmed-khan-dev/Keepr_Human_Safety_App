package com.example.keepr_humansafetyapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.keepr_humansafetyapp.R
import com.example.keepr_humansafetyapp.models.About_Model

class AboutAdapter(
    private val context: Context,
    private val featureList: List<About_Model>
) : RecyclerView.Adapter<AboutAdapter.FeatureViewHolder>() {

    inner class FeatureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.feature_title)
        val desc: TextView = itemView.findViewById(R.id.feature_desc)
        val icon: ImageView = itemView.findViewById(R.id.feature_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeatureViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_feature, parent, false)
        return FeatureViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeatureViewHolder, position: Int) {
        val feature = featureList[position]
        holder.title.text = feature.title
        holder.desc.text = feature.description
        holder.icon.setImageResource(feature.icon)
    }

    override fun getItemCount(): Int = featureList.size
}
