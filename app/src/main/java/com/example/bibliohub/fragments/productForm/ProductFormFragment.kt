package com.example.bibliohub.fragments.productForm

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.bibliohub.R
import com.example.bibliohub.data.entities.product.Product
import com.example.bibliohub.databinding.FragmentProductFormBinding
import com.example.bibliohub.utils.Constants
import com.example.bibliohub.utils.FormFunctions
import com.example.bibliohub.utils.HelperFunctions
import com.example.bibliohub.utils.HelperFunctions.displayDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class ProductFormFragment : Fragment() {

    private val viewModel: ProductFormViewModel by viewModels()
    private val args: ProductFormFragmentArgs by navArgs()
    private lateinit var binding: FragmentProductFormBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductFormBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        viewModel.resetProductModel()

        binding.isImageAvailable = false

        viewModel.product = args.product
        viewModel.product?.toString()?.let { Log.d("Product", it) }
        setBinding()
        viewModel.product?.let { product ->
            viewModel.initializeProductModel(product)
        }
        setOnBackPressedCallback()
        return binding.root
    }

    private fun loadImage(imgSrc: String) =
        Glide.with(requireContext()).load(imgSrc)
            .into(binding.productImageView)

    private fun setBinding() {
        with(binding) {
            titleEditText.doAfterTextChanged {
                FormFunctions.validateName(it.toString(), binding.titleLayout)
            }
            authorEditText.doAfterTextChanged {
                FormFunctions.validateName(it.toString(), binding.authorLayout)
            }
            descriptionEditText.doAfterTextChanged {
                FormFunctions.validateName(it.toString(), binding.descriptionLayout)
            }
            isbnEditText.doAfterTextChanged {
                FormFunctions.validateISBN(it.toString(), binding.isbnLayout)
            }
            quantityEditText.doAfterTextChanged {
                FormFunctions.validateNumber(it.toString(), binding.quantityLayout)
            }
            imageEditText.doAfterTextChanged {
                FormFunctions.validateGeneral(it.toString(), binding.imageLayout)
            }
            pubDateEditText.doAfterTextChanged {
                FormFunctions.validateGeneral(it.toString(), binding.pubDateLayout)
            }
            priceEditText.doAfterTextChanged {
                FormFunctions.validateGeneral(it.toString(), binding.priceLayout)
            }
            categoryEditText.doAfterTextChanged {
                FormFunctions.validateGeneral(it.toString(), binding.categoryLayout)
            }
            imageEditText.doOnTextChanged { text, _, _, _ ->
                isImageAvailable = !text.isNullOrEmpty()
                viewModel?.productModel?.value?.imgSrc = text.toString()
                viewModel?.productModel?.value?.imgSrc?.let { loadImage(it) }
            }
            pubDateEditText.setOnClickListener {
                displayDatePicker(
                    binding.pubDateEditText,
                    requireContext()
                )
            }
            categoryEditText.setAdapter(
                viewModel?.categoryList?.let {
                    ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_list_item_1,
                        it
                    )
                }
            )
            categoryEditText.setOnEditorActionListener { _, actionId, event ->
                if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                    btnSubmit.performClick()
                }
                false
            }
            btnSubmit.setOnClickListener {
                val (
                    title,
                    author,
                    description,
                    isbn,
                    quantity,
                    imgSrc,
                    pubDate,
                    price,
                    category
                ) = viewModel?.productModel?.value ?: ProductModel()
                val isFormValid = validateFields(
                    title = title,
                    author = author,
                    description = description,
                    isbn = isbn,
                    quantity = quantity,
                    imgSrc = imgSrc ?: "",
                    pubDate = pubDate,
                    price = price,
                    category = category
                )
                if (isFormValid) {
                    lifecycleScope.launch {
                        Log.d("Product", viewModel?.productModel?.value.toString())
                        Log.d("Product", viewModel?.product.toString())
                        val sdf = SimpleDateFormat(Constants.DATE_FORMAT_FULL, Locale.getDefault())
                        val date = viewModel?.productModel?.value?.pubDate?.let { it1 ->
                            sdf.parse(it1)
                        }
                        val dateStr =
                            date?.let { HelperFunctions.getDateString(Constants.DATE_FORMAT_HYPHEN_DMY, it) }
                        viewModel?.productModel?.value?.let { it1 ->
                            if (args.product == null) {
                                viewModel?.insertProduct(
                                    Product(
                                        title = it1.title,
                                        author = it1.author,
                                        description = it1.description,
                                        isbn = it1.isbn,
                                        quantity = it1.quantity.toInt(),
                                        imgSrc = it1.imgSrc,
                                        pubDate = dateStr,
                                        price = it1.price,
                                        category = it1.category
                                    )
                                )
                            } else {
                                viewModel?.product?.id?.let { it2 ->
                                    Product(
                                        id = it2,
                                        title = it1.title,
                                        author = it1.author,
                                        description = it1.description,
                                        isbn = it1.isbn,
                                        quantity = it1.quantity.toInt(),
                                        imgSrc = it1.imgSrc,
                                        pubDate = dateStr,
                                        price = it1.price,
                                        category = it1.category
                                    )
                                }?.let { it3 -> viewModel?.updateProduct(it3) }
                            }
                        }
                        requireActivity().onBackPressedDispatcher.onBackPressed()
//                        viewModel?.resetProductModel()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Some fields require your attention.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun validateFields(
        title: String,
        author: String,
        description: String,
        isbn: String,
        quantity: String,
        imgSrc: String,
        pubDate: String,
        price: String,
        category: String
    ): Boolean {
        val isNameValid = FormFunctions.validateName(title, binding.titleLayout)
        val isAuthorValid = FormFunctions.validateName(author, binding.authorLayout)
        val isDescriptionValid = FormFunctions.validateName(description, binding.descriptionLayout)
        val isISBNValid = FormFunctions.validateISBN(isbn, binding.isbnLayout)
        val isQuantityValid = FormFunctions.validateNumber(quantity, binding.quantityLayout)
        val isImgValid = FormFunctions.validateGeneral(imgSrc, binding.imageLayout)
        val isPubDateValid = FormFunctions.validateGeneral(pubDate, binding.pubDateLayout)
        val isPriceValid = FormFunctions.validateGeneral(price, binding.priceLayout)
        val isCategoryValid = FormFunctions.validateGeneral(category, binding.categoryLayout)

        return isNameValid && isAuthorValid && isDescriptionValid && isISBNValid && isQuantityValid && isImgValid && isPubDateValid && isPriceValid && isCategoryValid
    }

    private fun setOnBackPressedCallback() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack(R.id.adminHomeFragment, false)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
}