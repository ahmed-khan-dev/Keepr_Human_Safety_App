package com.example.keepr_humansafetyapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MemberAdapter(
    private val contactList: MutableList<ContactsModel>
) : RecyclerView.Adapter<MemberAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mycontacts, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = contactList[position]
        holder.name.text = item.name
        holder.address.text = item.address
        holder.phone.text = item.phone

        // Update / Delete buttons (implement later)
        holder.btnUpdate.setOnClickListener {
            // TODO: open dialog to update contact
        }
        holder.btnDelete.setOnClickListener {
            contactList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, contactList.size)
        }
    }

    override fun getItemCount(): Int = contactList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgUser: ImageView = itemView.findViewById(R.id.img_user)
        val name: TextView = itemView.findViewById(R.id.name)
        val address: TextView = itemView.findViewById(R.id.address)
        val phone: TextView = itemView.findViewById(R.id.phone_number)
        val btnUpdate: Button = itemView.findViewById(R.id.btn_update)
        val btnDelete: Button = itemView.findViewById(R.id.btn_delete)
    }
}
