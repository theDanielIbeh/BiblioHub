package com.example.bibliohub

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.bibliohub.fragments.login.LoginViewModel
import com.example.bibliohub.utils.Constants
import com.example.bibliohub.utils.Constants.IS_LOGGED_IN

class MainActivity : AppCompatActivity() {
    private val viewModel: LoginViewModel by viewModels { LoginViewModel.Factory }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val navGraph = inflater.inflate(R.navigation.nav_graph)

        val isLoggedIn = viewModel.biblioHubPreferencesRepository.getPreference(
            Boolean::class.java,
            IS_LOGGED_IN
        )
        val isAdmin = viewModel.biblioHubPreferencesRepository.getPreference(
            Boolean::class.java,
            Constants.IS_ADMIN
        )
        isLoggedIn.observe(this@MainActivity) {
            if (it == true) {
                isAdmin.observe(this@MainActivity) { admin ->
                    Log.d("AppActivity", admin.toString())
                    if (admin == true) {
                        val intent = Intent(this@MainActivity, AdminActivity::class.java)
                        startActivity(intent)
                    } else {
                        val intent = Intent(this@MainActivity, CustomerActivity::class.java)
                        startActivity(intent)
                    }
                }
            } else {
                navGraph.setStartDestination(startDestId = R.id.loginFragment)
            }
            val navController = navHostFragment.navController
            navController.setGraph(navGraph, intent.extras)
        }
    }
}