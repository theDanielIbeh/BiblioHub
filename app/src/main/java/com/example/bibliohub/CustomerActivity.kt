package com.example.bibliohub

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.bibliohub.fragments.login.LoginViewModel
import com.example.bibliohub.utils.HelperFunctions
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CustomerActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private val viewModel: LoginViewModel by viewModels()
    private var navHostFragment: NavHostFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer)

        setSupportActionBar(findViewById(R.id.nav_view))
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.customer_nav_host_fragment) as NavHostFragment
        val inflater = navHostFragment?.navController?.navInflater
        val navGraph = inflater?.inflate(R.navigation.customer_nav_graph)
        navGraph?.setStartDestination(startDestId = R.id.homeFragment)

        HelperFunctions.setMenu(
            this,
            viewModel.biblioHubPreferencesRepository
        )
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val navController =
            (supportFragmentManager.findFragmentById(R.id.customer_nav_host_fragment) as NavHostFragment).navController
        item.setChecked(true)
        return when (item.itemId) {

            R.id.home_item -> {
                navController.popBackStack(R.id.homeFragment, false)
                true
            }

            R.id.cart_item -> {
                navController.navigate(R.id.cartFragment)
                true
            }
            else -> false
        }
    }
}