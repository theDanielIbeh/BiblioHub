package com.example.bibliohub

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.example.bibliohub.fragments.login.LoginViewModel
import com.example.bibliohub.utils.Constants.Companion.IS_LOGGED_IN
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val viewModel: LoginViewModel by viewModels { LoginViewModel.Factory }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val navGraph = inflater.inflate(R.navigation.nav_graph)

        val isLoggedIn = viewModel.biblioHubPreferencesRepository.getPreference(Boolean::class.java, IS_LOGGED_IN)
        lifecycleScope.launch {
            isLoggedIn.collect {
                Log.d("isLoggedIn", it.toString())
                if (it == true) {
                    navGraph.setStartDestination(startDestId = R.id.homeFragment)
                } else {
                    navGraph.setStartDestination(startDestId = R.id.loginFragment)
                }
                val navController = navHostFragment.navController
                navController.setGraph(navGraph, intent.extras)
            }
        }

    }
}