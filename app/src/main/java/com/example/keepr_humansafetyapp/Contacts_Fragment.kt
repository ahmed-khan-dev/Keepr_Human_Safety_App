package com.example.keepr_humansafetyapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class Contacts_Fragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts_, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ContactListMembers = listOf<ContactsModel>(
            ContactsModel("Ahmed Khan","karimi apt 101"),
            ContactsModel("Shaan Ali Khan","501 regency 2"),
            ContactsModel("Saif Kapadi","301 dharti complex"),
            ContactsModel("Arbaz Shaikh","201 plesent palace"),



            )

        val adapter = MemberAdapter(ContactListMembers)
        val recycler = requireView().findViewById<RecyclerView>(R.id.Contacts_recycler)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter =adapter
    }

    companion object {
        fun newInstance() =Contacts_Fragment()

    }
}