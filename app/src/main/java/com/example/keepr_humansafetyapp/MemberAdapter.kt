package com.example.keepr_humansafetyapp
import android.widget.TextView


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class MemberAdapter(private val ContactListMembers: List<ContactsModel>) : RecyclerView.Adapter<MemberAdapter.Viewholder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberAdapter.Viewholder {

        val inflater = LayoutInflater.from(parent.context)
        val item = inflater.inflate(R.layout.item_mycontacts,parent,false)
        return Viewholder(item)
    }

    override fun onBindViewHolder(holder: MemberAdapter.Viewholder, position: Int) {
        val item = ContactListMembers[position]
        holder.ContactName.text = item.name
        holder.address.text = item.address
    }

    override fun getItemCount(): Int {
      return  ContactListMembers.size
    }

    class Viewholder(private val item: View) : RecyclerView.ViewHolder(item) {

        val ImgUser = item.findViewById<ImageView>(R.id.img_user)
         val ContactName = item.findViewById<TextView>(R.id.name)
        val address =item.findViewById<TextView>(R.id.address)



    }
}