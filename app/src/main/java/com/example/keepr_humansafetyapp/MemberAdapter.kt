package com.example.keepr_humansafetyapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MemberAdapter(
    private val contactListMembers: MutableList<ContactsModel>,
    private val onUpdateClick: (position: Int) -> Unit,   // callback for update
    private val onDeleteClick: (position: Int) -> Unit    // callback for delete
) : RecyclerView.Adapter<MemberAdapter.Viewholder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val item = LayoutInflater.from(parent.context).inflate(R.layout.item_mycontacts, parent, false)
        return Viewholder(item)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val item = contactListMembers[position]
        holder.ContactName.text = item.name
        holder.address.text = item.address
        holder.phoneNumber.text = item.phone

        holder.btnUpdate.setOnClickListener { onUpdateClick(position) }
        holder.btnDelete.setOnClickListener { onDeleteClick(position) }
    }

    override fun getItemCount() = contactListMembers.size

    class Viewholder(item: View) : RecyclerView.ViewHolder(item) {
        val ImgUser: ImageView = item.findViewById(R.id.img_user)
        val ContactName: TextView = item.findViewById(R.id.name)
        val address: TextView = item.findViewById(R.id.address)
        val phoneNumber: TextView = item.findViewById(R.id.phone_number)
        val btnUpdate: Button = item.findViewById(R.id.btn_update)
        val btnDelete: Button = item.findViewById(R.id.btn_delete)
    }
}
