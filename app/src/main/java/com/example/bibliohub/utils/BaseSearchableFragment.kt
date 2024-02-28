package com.example.bibliohub.utils

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.example.bibliohub.R
import com.google.android.material.textfield.TextInputEditText

/**
 * This class provides some common search functionality,
 * and can be used for any fragment with search feature.
 */
abstract class BaseSearchableFragment<T> : Fragment() {

    // Provide the viewModel's filter text
    abstract var viewModelFilterText: String?

    // Provide method search method to run after text change
    abstract val searchCallback: ((String) -> Unit)?

    // Provide binding's search button/image
    abstract val searchButton: ImageButton?

    // Provide binding's search edit text
    abstract val searchText: TextInputEditText?

    open var shouldClickToSearch = false

    /**
     * Any subclass of this class that wishes to override this method
     * for extra configuration must call super()
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        initBinding(inflater, container)

        initCompulsoryVariables()
        initRecycler()

        return returnBindingRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setBinding()
        if (shouldClickToSearch) {
            setupClickToSearch()
            return
        }
        searchText?.addTextChangedListener(SearchTextWatcher())
        setupStopSearchImageButton()
        super.onViewCreated(view, savedInstanceState)
    }

    /**
     * Override method and inflate binding object
     */
    abstract fun initBinding(inflater: LayoutInflater, container: ViewGroup?)

    /**
     * Override and initialise all compulsory variables
     */
    abstract fun initCompulsoryVariables()

    /**
     * Override and return binding.root
     */
    abstract fun returnBindingRoot(): View

    /**
     * Override and set any onClickListeners
     */
    abstract fun setBinding()

    /**
     * Override and provide implementation for clearing card selection
     */
    open fun clearCardSelection() {}

    /**
     * Override and provide implementation for initialising the recyclerView
     */
    abstract fun initRecycler()

    private fun setupClickToSearch() {
        searchText?.doAfterTextChanged {
            if (it.isNullOrBlank()) {
                searchCallback?.invoke("")
            }
        }
        searchButton?.setOnClickListener {
            searchCallback?.invoke(searchText?.text.toString())
        }
    }

    private fun setupStopSearchImageButton() {
        searchButton?.setOnClickListener {
            searchText?.setText("")
            searchText?.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                ContextCompat.getDrawable(requireContext(), R.drawable.search_icon),
                null
            )
            it.visibility = View.GONE
        }
    }

    private fun setSearchIconAndStopSearchImageButtonVisibility() {
        if (viewModelFilterText == "%%") {
            searchButton?.visibility = View.GONE
            searchText?.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.search_icon
                ),
                null
            )
        } else {
            searchButton?.visibility = View.VISIBLE
            searchText?.setCompoundDrawables(
                null, null,
                null, null
            )
        }
    }

    /**
     * Text watcher class for handling recycler item filter
     */
    private inner class SearchTextWatcher : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(s: Editable?) {
            search(s.toString())
        }
    }

    private fun search(enteredText: String) {
        try {
            val filterString = enteredText.lowercase()
            if (viewModelFilterText != null) {
                if (filterString != viewModelFilterText) {
                    clearCardSelection()
                }
            } else {
                clearCardSelection()
            }
            viewModelFilterText = filterString
            searchCallback?.invoke(filterString)
            if (!shouldClickToSearch) {
                setSearchIconAndStopSearchImageButtonVisibility()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}