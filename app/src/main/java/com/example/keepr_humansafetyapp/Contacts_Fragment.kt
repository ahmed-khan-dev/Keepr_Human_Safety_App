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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Contacts_Fragment : Fragment() {

    private lateinit var adapter: MemberAdapter
    private val contactListMembers = mutableListOf<ContactsModel>()

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var userId: String? = null

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

        adapter = MemberAdapter(contactListMembers,
            onUpdateClick = { pos -> showUpdateContactDialog(pos) },
            onDeleteClick = { pos -> deleteContact(pos) }
        )
        recycler.adapter = adapter

        userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        fetchContactsFromFirestore()

        val btnAdd = view.findViewById<Button>(R.id.btn_add_contact)
        btnAdd.setOnClickListener {
            showAddContactDialog()
        }
    }

    private fun fetchContactsFromFirestore() {
        firestore.collection("users")
            .document(userId!!)
            .collection("contacts")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(requireContext(), "Failed to fetch contacts", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                contactListMembers.clear()
                snapshot?.documents?.forEach { document ->
                    val contact = document.toObject(ContactsModel::class.java)
                    contact?.id = document.id
                    contact?.let { contactListMembers.add(it) }
                }
                adapter.notifyDataSetChanged()
            }
    }

    private fun isValidPhoneNumber(phone: String): Boolean {
        val digitsOnly = phone.replace(Regex("\\D"), "")
        return digitsOnly.length == 10
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

            if (!phone.startsWith("+")) phone = "+91$phone"
            val phoneDigits = phone.removePrefix("+91")

            if (!isValidPhoneNumber(phoneDigits)) {
                Toast.makeText(requireContext(), "Enter a valid 10-digit number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (name.isNotEmpty() && address.isNotEmpty() && phone.isNotEmpty()) {
                val newContact = ContactsModel(name = name, address = address, phone = phone)
                firestore.collection("users")
                    .document(userId!!)
                    .collection("contacts")
                    .add(newContact)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Contact added", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to add contact", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        dialog.show()
    }

    private fun showUpdateContactDialog(position: Int) {
        val contact = contactListMembers[position]
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_contact, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val etName = dialogView.findViewById<EditText>(R.id.et_name)
        val etAddress = dialogView.findViewById<EditText>(R.id.et_address)
        val etPhone = dialogView.findViewById<EditText>(R.id.et_phone)
        val btnSave = dialogView.findViewById<Button>(R.id.btn_save_contact)

        etPhone.hint = "Enter number in international format, e.g. +917208394369"
        etName.setText(contact.name)
        etAddress.setText(contact.address)
        etPhone.setText(contact.phone)

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val address = etAddress.text.toString().trim()
            var phone = etPhone.text.toString().trim()

            if (!phone.startsWith("+")) phone = "+91$phone"
            val phoneDigits = phone.removePrefix("+91")

            if (!isValidPhoneNumber(phoneDigits)) {
                Toast.makeText(requireContext(), "Enter a valid 10-digit number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (name.isNotEmpty() && address.isNotEmpty() && phone.isNotEmpty() && contact.id != null) {
                firestore.collection("users")
                    .document(userId!!)
                    .collection("contacts")
                    .document(contact.id!!)
                    .set(ContactsModel(name = name, address = address, phone = phone))
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Contact updated", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to update contact", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        dialog.show()
    }

    private fun deleteContact(position: Int) {
        val contact = contactListMembers[position]
        if (contact.id != null) {
            firestore.collection("users")
                .document(userId!!)
                .collection("contacts")
                .document(contact.id!!)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Contact deleted", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to delete contact", Toast.LENGTH_SHORT).show()
                }
        }
    }

    companion object {
        fun newInstance() = Contacts_Fragment()
    }
}
