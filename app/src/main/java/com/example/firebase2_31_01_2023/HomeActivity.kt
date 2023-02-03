package com.example.firebase2_31_01_2023

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.firebase2_31_01_2023.databinding.ActivityHomeBinding
import com.example.firebase2_31_01_2023.prefs.Prefs
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding
    lateinit var prefs: Prefs
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        prefs = Prefs(this)
        setContentView(binding.root)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_logout -> {
                FirebaseAuth.getInstance().signOut()
                prefs.deleteAll()
                finish()
                true
            }
            R.id.item_exit -> {
                finishAffinity()
                true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}