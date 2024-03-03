package com.example.bibliohub

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.example.bibliohub.fragments.login.LoginViewModel
import com.example.bibliohub.utils.Constants.Companion.IS_ADMIN
import com.example.bibliohub.utils.Constants.Companion.IS_LOGGED_IN
import kotlinx.coroutines.launch

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
            IS_ADMIN
        )
        lifecycleScope.launch {
            isLoggedIn.collect {
                if (it == true) {
                    if (isAdmin.equals(true)) {
                        navGraph.setStartDestination(startDestId = R.id.adminHomeFragment)
                    } else {
                        navGraph.setStartDestination(startDestId = R.id.homeFragment)
                    }
                } else {
                    navGraph.setStartDestination(startDestId = R.id.loginFragment)
                }
                val navController = navHostFragment.navController
                navController.setGraph(navGraph, intent.extras)
            }
        }
//        setMenu()
    }

    private fun setMenu() {
        // Add menu items without using the Fragment Menu APIs
        // Note how we can tie the MenuProvider to the viewLifecycleOwner
        // and an optional Lifecycle.State (here, RESUMED) to indicate when
        // the menu should be visible
        val navController =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
        addMenuProvider(object : MenuProvider {
            @SuppressLint("RestrictedApi", "UseCompatLoadingForDrawables")
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_menu, menu)
                val home = menu.findItem(R.id.home_item)
                home.icon?.setTint(resources.getColor(R.color.darkBlue))
            }

            override fun onPrepareMenu(menu: Menu) {
                super.onPrepareMenu(menu)
                val menuItems = arrayListOf(R.id.home_item, R.id.cart_item)
                menuItems.forEach { id ->
                    val item = menu.findItem(id)
                    item.icon?.setTint(resources.getColor(R.color.lightGrey))
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
//                resetMenuItemColors(menuItem.itemId)
                Log.d("MainActivity", menuItem.itemId.toString())
                menuItem.icon?.setColorFilter(
                    getColor(R.color.darkBlue),
                    PorterDuff.Mode.SRC_IN
                )
                return when (menuItem.itemId) {
                    R.id.home_item -> {
                        navController.popBackStack(R.id.homeFragment, true)
                        true
                    }

                    R.id.log_out -> {
                        lifecycleScope.launch {
                            viewModel.biblioHubPreferencesRepository.clearDataStore()
                            navController.popBackStack(R.id.loginFragment, true)
                        }
                        true
                    }

                    R.id.cart_item -> {
                        navController.navigate(R.id.cartFragment)
                        true
                    }

                    else -> false
                }
            }
        }, this, Lifecycle.State.RESUMED)
    }

//    @SuppressLint("RestrictedApi", "UseCompatLoadingForDrawables")
//    private fun resetMenuItemColors(selectedId: Int) {
//        val home = findViewById<ActionMenuItemView>(R.id.home_item)
//        home.setIcon(getDrawable(R.drawable.home))
//        val cart = findViewById<ActionMenuItemView>(R.id.cart_item)
//        cart.setIcon(getDrawable(R.drawable.cart))
//        if (home.id == selectedId) {
//            home.setBackgroundColor(resources.getColor(R.color.darkBlue))
//            cart.setBackgroundColor(resources.getColor(R.color.disabled))
//        } else{
//            home.setBackgroundColor(resources.getColor(R.color.disabled))
//            cart.setBackgroundColor(resources.getColor(R.color.darkBlue))
//        }
//    }
}