package com.example.keepr_humansafetyapp

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class Contacts_Fragment : Fragment() {

    private lateinit var adapter: MemberAdapter
    private val ContactListMembers = mutableListOf<ContactsModel>()

    private lateinit var database: FirebaseDatabase
    private lateinit var contactsRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_contacts_, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.Contacts_recycler)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        adapter = MemberAdapter(ContactListMembers,
            onUpdateClick = { pos -> showUpdateContactDialog(pos) },
            onDeleteClick = { pos -> deleteContact(pos) }
        )
        recycler.adapter = adapter

        database = FirebaseDatabase.getInstance()
        contactsRef = database.getReference("contacts")

        // Fetch contacts from Firebase
        fetchContactsFromFirebase()

        // Add Contact Button
        val btnAdd = view.findViewById<Button>(R.id.btn_add_contact)
        btnAdd.setOnClickListener {
            showAddContactDialog()
        }
    }

    private fun fetchContactsFromFirebase() {
        contactsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ContactListMembers.clear()
                for (child in snapshot.children) {
                    val contact = child.getValue(ContactsModel::class.java)
                    contact?.let { ContactListMembers.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to fetch contacts", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showAddContactDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_contact, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val etName = dialogView.findViewById<EditText>(R.id.et_name)
        val etAddress = dialogView.findViewById<EditText>(R.id.et_address)
        val etPhone = dialogView.findViewById<EditText>(R.id.et_phone)
        val btnSave = dialogView.findViewById<Button>(R.id.btn_save_contact)




        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val address = etAddress.text.toString().trim()
            var phone = etPhone.text.toString().trim()

            // Automatically add +91 if not present
            if (!phone.startsWith("+")) {
                phone = "+91$phone"
            }

            if (name.isNotEmpty() && address.isNotEmpty() && phone.isNotEmpty()) {
                val newContactId = contactsRef.push().key
                val newContact = ContactsModel(name, address, phone)
                newContactId?.let {
                    contactsRef.child(it).setValue(newContact).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(requireContext(), "Contact added", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        } else {
                            Toast.makeText(requireContext(), "Failed to add contact", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }


        dialog.show()
    }

    private fun showUpdateContactDialog(position: Int) {
        val contact = ContactListMembers[position]
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_contact, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val etName = dialogView.findViewById<EditText>(R.id.et_name)
        val etAddress = dialogView.findViewById<EditText>(R.id.et_address)
        val etPhone = dialogView.findViewById<EditText>(R.id.et_phone)
        val btnSave = dialogView.findViewById<Button>(R.id.btn_save_contact)

        etPhone.hint = "Enter number in international format, e.g. +917208394369"

        // Pre-fill existing values
        etName.setText(contact.name)
        etAddress.setText(contact.address)
        etPhone.setText(contact.phone)

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val address = etAddress.text.toString().trim()
            val phone = etPhone.text.toString().trim()

            if (name.isNotEmpty() && address.isNotEmpty() && phone.isNotEmpty()) {
                // Update Firebase
                contactsRef.orderByChild("phone").equalTo(contact.phone)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (child in snapshot.children) {
                                child.ref.setValue(ContactsModel(name, address, phone))
                            }
                            Toast.makeText(requireContext(), "Contact updated", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
            }
        }

        dialog.show()
    }

    private fun deleteContact(position: Int) {
        val contact = ContactListMembers[position]
        contactsRef.orderByChild("phone").equalTo(contact.phone)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children) {
                        child.ref.removeValue()
                    }
                    Toast.makeText(requireContext(), "Contact deleted", Toast.LENGTH_SHORT).show()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    companion object {
        fun newInstance() = Contacts_Fragment()
    }
}


//git remote add origin https://github.com/ahmed-khan-dev/Keepr_Human_Safety_App.git
// git branch -M main
// git push -u origin main