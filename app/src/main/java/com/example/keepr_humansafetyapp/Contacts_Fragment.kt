package com.example.keepr_humansafetyapp

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Contacts_Fragment : Fragment() {

    private lateinit var adapter: MemberAdapter
    private val ContactListMembers = mutableListOf<ContactsModel>()

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

        // Sample initial contacts
        ContactListMembers.add(ContactsModel("Ahmed Khan","Karimi Apt 101","+919876543210"))
        ContactListMembers.add(ContactsModel("Shaan Ali Khan","501 Regency 2","+918828130894"))

        adapter = MemberAdapter(ContactListMembers,
            onUpdateClick = { pos -> showUpdateContactDialog(pos) },
            onDeleteClick = { pos ->
                ContactListMembers.removeAt(pos)
                adapter.notifyItemRemoved(pos)
            }
        )
        recycler.adapter = adapter


        // Add Contact Button
        val btnAdd = view.findViewById<Button>(R.id.btn_add_contact)
        btnAdd.setOnClickListener {
            showAddContactDialog()
        }
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
            val phone = etPhone.text.toString().trim()

            if (name.isNotEmpty() && address.isNotEmpty() && phone.isNotEmpty()) {
                ContactListMembers.add(ContactsModel(name, address, phone))
                adapter.notifyItemInserted(ContactListMembers.size - 1)
                dialog.dismiss()
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

        // Pre-fill existing values
        etName.setText(contact.name)
        etAddress.setText(contact.address)
        etPhone.setText(contact.phone)

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val address = etAddress.text.toString().trim()
            val phone = etPhone.text.toString().trim()

            if (name.isNotEmpty() && address.isNotEmpty() && phone.isNotEmpty()) {
                ContactListMembers[position] = ContactsModel(name, address, phone)
                adapter.notifyItemChanged(position)
                dialog.dismiss()
            }
        }

        dialog.show()
    }


    companion object {
        fun newInstance() = Contacts_Fragment()
    }
}
//git remote add origin https://github.com/ahmed-khan-dev/Keepr_Human_Safety_App.git
// git branch -M main
// git push -u origin main