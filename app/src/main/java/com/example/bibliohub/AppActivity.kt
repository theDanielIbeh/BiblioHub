package com.example.bibliohub

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.navigation.fragment.NavHostFragment
import com.example.bibliohub.fragments.login.LoginViewModel
import com.example.bibliohub.utils.Constants
import com.example.bibliohub.utils.HelperFunctions

class AppActivity : AppCompatActivity() {
    private val viewModel: LoginViewModel by viewModels { LoginViewModel.Factory }
    private var navHostFragment: NavHostFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.app_nav_host_fragment) as NavHostFragment
        val inflater = navHostFragment?.navController?.navInflater
        val navGraph = inflater?.inflate(R.navigation.app_nav_graph)

        val isAdmin = viewModel.biblioHubPreferencesRepository.getPreference(
            Boolean::class.java,
            Constants.IS_ADMIN
        )
        isAdmin.observe(this@AppActivity) { admin ->
            if (admin == true) {
                HelperFunctions.setAdminMenu(
                    this@AppActivity,
                    viewModel.biblioHubPreferencesRepository
                )
                navGraph?.setStartDestination(startDestId = R.id.adminHomeFragment)
            } else {
                HelperFunctions.setMenu(
                    this@AppActivity,
                    viewModel.biblioHubPreferencesRepository
                )
                navGraph?.setStartDestination(startDestId = R.id.homeFragment)
            }
        }
    }
}