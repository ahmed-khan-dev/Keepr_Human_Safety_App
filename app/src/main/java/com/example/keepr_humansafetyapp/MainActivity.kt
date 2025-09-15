package com.example.keepr_humansafetyapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var mapsFragment: MapsFragment
    private lateinit var aboutFragment: About_Fragment
    private lateinit var fakeCallFragment: Fakecall_Fragment
    private lateinit var contactsFragment: Contacts_Fragment
    private lateinit var profileFragment: Profile_Fragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize fragments
        mapsFragment = MapsFragment.newInstance()
        aboutFragment = About_Fragment.newInstance()
        fakeCallFragment = Fakecall_Fragment.newInstance()
        contactsFragment = Contacts_Fragment.newInstance()
        profileFragment = Profile_Fragment.newInstance()

        // Add fragments initially, only show MapsFragment
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.container, mapsFragment, "MAPS")
        transaction.add(R.id.container, aboutFragment, "ABOUT").hide(aboutFragment)
        transaction.add(R.id.container, fakeCallFragment, "FAKECALL").hide(fakeCallFragment)
        transaction.add(R.id.container, contactsFragment, "CONTACTS").hide(contactsFragment)
        transaction.add(R.id.container, profileFragment, "PROFILE").hide(profileFragment)
        transaction.commit()

        // Bottom Navigation setup
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home_nav -> showFragment(mapsFragment)
                R.id.about_nav -> showFragment(aboutFragment)
                R.id.call_nav -> showFragment(fakeCallFragment)
                R.id.contacts_nav -> showFragment(contactsFragment)
                R.id.profile_nav -> showFragment(profileFragment)
            }
            true
        }

        // Default selection on login
        bottomNav.selectedItemId = R.id.home_nav
    }

    private fun showFragment(fragmentToShow: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        for (fragment in supportFragmentManager.fragments) {
            if (fragment == fragmentToShow) {
                transaction.show(fragment)
            } else {
                transaction.hide(fragment)
            }
        }
        transaction.commit()
    }
}
//git remote add origin https://github.com/ahmed-khan-dev/Keepr_Human_Safety_App.git
//git branch -M main
//git push -u origin main