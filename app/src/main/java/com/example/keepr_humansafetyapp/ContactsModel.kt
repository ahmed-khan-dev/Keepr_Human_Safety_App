package com.example.keepr_humansafetyapp

data class ContactsModel(
    var id: String? = null,   // Firestore document ID
    var name: String = "",
    var address: String = "",
    var phone: String = ""
)
