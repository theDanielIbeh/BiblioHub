package com.example.bibliohub.fragments.productForm

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.bibliohub.R
import com.example.bibliohub.databinding.FragmentProductFormBinding
import com.example.bibliohub.utils.Constants
import com.example.bibliohub.utils.FormFunctions
import com.example.bibliohub.utils.HelperFunctions.displayDatePicker
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.roundToInt

class ProductFormFragment : Fragment() {

    private val viewModel: ProductFormViewModel by viewModels { ProductFormViewModel.Factory }
    private val args: ProductFormFragmentArgs by navArgs()
    private lateinit var binding: FragmentProductFormBinding
    private var storageDir: File? = null
    private var currentFilePath: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductFormBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.isImageAvailable = false

        viewModel.product = args.product
        viewModel.product?.let { product ->
            viewModel.productToProductModel(product)
            product.imgSrc?.let { loadImage(it) }
        }
        setBinding()
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
                FormFunctions.validatePostcode(it.toString(), binding.authorLayout)
            }
            descriptionEditText.doAfterTextChanged {
                FormFunctions.validateEmail(it.toString(), binding.descriptionLayout)
            }
            isbnEditText.doAfterTextChanged {
                FormFunctions.validateExpiryDate(it.toString(), binding.isbnLayout)
            }
            quantityEditText.doAfterTextChanged {
                FormFunctions.validateCVV(it.toString(), binding.quantityLayout)
            }
            imageEditText.doAfterTextChanged {
                FormFunctions.validatePIN(it.toString(), binding.imageLayout)
            }
            btnDelete.setOnClickListener {
                binding.isImageAvailable = false
                currentFilePath = null
                viewModel?.productModel?.value?.imgSrc = null
            }
            pubDateEditText.doAfterTextChanged {
                FormFunctions.validatePIN(it.toString(), binding.pubDateLayout)
            }
            priceEditText.doAfterTextChanged {
                FormFunctions.validatePIN(it.toString(), binding.priceLayout)
            }
            categoryEditText.doAfterTextChanged {
                FormFunctions.validatePIN(it.toString(), binding.categoryLayout)
            }
            imageEditText.setOnClickListener {
                takePhotoOrSelectFile()
            }
            pubDateEditText.setOnClickListener {
                displayDatePicker(
                    binding.pubDateEditText,
                    requireContext()
                )
            }

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
                    viewModel?.productModel?.value?.let { product ->
                        viewModel?.productModelToProduct(
                            product, currentFilePath)
                    }

                    findNavController().popBackStack(R.id.adminHomeFragment, true)
                    viewModel?.resetProductModel()
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

    // Function for displaying an AlertDialog for choosing an image/pdf file
    @SuppressLint("QueryPermissionsNeeded")
    private fun takePhotoOrSelectFile() {
        val choice =
            arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val myAlertDialog: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        myAlertDialog.setTitle("Select Image/File")
        myAlertDialog.setItems(
            choice
        ) { dialog, item ->
            when {
                // Select "Take Photo" to take a photo
                choice[item] == "Take Photo" -> {
                    Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                        takePictureIntent.resolveActivity(requireContext().packageManager)
                            ?.also {
                                val photoFile: File? = try {
                                    createFile(requireContext())
                                } catch (ex: IOException) {
                                    null
                                }
                                photoFile?.also {
                                    val photoURI: Uri = Uri.fromFile(
                                        it
                                    )
                                    takePictureIntent.putExtra(
                                        MediaStore.EXTRA_OUTPUT,
                                        photoURI
                                    )
                                    startActivityForResult(takePictureIntent, 0)
                                }
                            }
                    }
                }
                // Select "Choose from Gallery" to pick image from gallery
                choice[item] == "Choose from Gallery" -> {
                    Intent(Intent.ACTION_GET_CONTENT).also { pickFromGalleryIntent ->
                        pickFromGalleryIntent.type = "image/*"
                        pickFromGalleryIntent.resolveActivity(requireContext().packageManager)
                            ?.also {
                                val photoFile: File? = try {
                                    createFile(requireContext())
                                } catch (ex: IOException) {
                                    null
                                }
                                photoFile?.also {
                                    val photoURI: Uri = Uri.fromFile(
                                        it
                                    )
                                    pickFromGalleryIntent.putExtra(
                                        MediaStore.EXTRA_OUTPUT,
                                        photoURI
                                    )
                                    startActivityForResult(pickFromGalleryIntent, 1)
                                }
                            }
                    }
                }
                // Select "Cancel" to cancel the task
                choice[item] == "Cancel" -> {
                    dialog.dismiss()
                }
            }
        }
        myAlertDialog.show()
    }

    private fun createFile(context: Context): File {
        storageDir =
            context.getExternalFilesDir("${Environment.DIRECTORY_DOCUMENTS}/${Constants.PRODUCT_PICTURE_DIR}")
        return File(
            storageDir,
            ".jpg",
        ).apply {
            currentFilePath = absolutePath
        }
    }

    // Override this method to allow you select an an image or a PDF
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // For loading Image
        if (resultCode != Activity.RESULT_CANCELED) {
            when (requestCode) {
                0 -> if (resultCode == Activity.RESULT_OK) {
                    if (currentFilePath != null) {
                        Log.d("currentFilePath", currentFilePath.toString())
                        compressImage(currentFilePath!!)
                    }
                    if (File("${currentFilePath?.dropLast(3)}pdf").exists()) {
                        File("${currentFilePath?.dropLast(3)}pdf").delete()
                    }
                }

                1 -> if (resultCode == Activity.RESULT_OK && data != null) {
                    val imageSelected = data.data
                    val pathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    val myFile = File(currentFilePath as String)

                    if (imageSelected != null) {
                        val myCursor = requireContext().contentResolver.query(
                            imageSelected,
                            pathColumn, null, null, null
                        )
                        // Moving image to desired directory
                        if (myCursor != null) {
                            myCursor.moveToFirst()
                            val columnIndex = myCursor.getColumnIndex(pathColumn[0])
                            val picturePath = myCursor.getString(columnIndex)
                            Log.i("picturePath", picturePath)
                            File(picturePath).let { sourceFile ->
                                val currentFile = File(currentFilePath)
                                if (currentFile.exists()) {
                                    currentFile.delete()
                                }
                                sourceFile.copyTo(File(currentFilePath))
                            }
                            compressImage(currentFilePath!!)
                            if (File("${currentFilePath?.dropLast(3)}pdf").exists()) {
                                File("${currentFilePath?.dropLast(3)}pdf").delete()
                            }
                        }
                    }
                }
            }
        }

        // For loading PDF
        when (requestCode) {
            2 -> if (resultCode == Activity.RESULT_OK && data != null) {
//                val pdfUri = data?.data!!
                val uri: Uri = data.data!!
                val pathColumn = arrayOf(MediaStore.Images.Media.DATA)
                val uriString: String = uri.toString()
                val myFile = File(currentFilePath as String)
                Log.d("currentFilePath", currentFilePath.toString())
                if (uriString.startsWith("content://")) {
                    var myCursor: Cursor? = null
                    try {
                        // Setting the PDF to the TextView
                        myCursor =
                            requireContext().contentResolver.query(uri, null, null, null, null)
                        if (myCursor != null && myCursor.moveToFirst()) {
                            val pdfFilePath =
                                myCursor.getString(myCursor.getColumnIndexOrThrow(pathColumn[0]))
                            Log.d("pdfFilePath", pdfFilePath)
                            File(pdfFilePath).let { sourceFile ->
                                val currentFile = File(currentFilePath)
                                if (currentFile.exists()) {
                                    currentFile.delete()
                                }
                                sourceFile.copyTo(File(currentFilePath))
                            }
                        }
                    } finally {
                        myCursor?.close()
                    }
                    if (File("${currentFilePath?.dropLast(3)}jpg").exists()) {
                        File("${currentFilePath?.dropLast(3)}jpg").delete()
                    }
                }
            }
        }
        if (currentFilePath != null) {
            if (File(currentFilePath).exists() && File(currentFilePath).length() <= 0) {
                File(currentFilePath).delete()
            }
        }
        if (currentFilePath != null) {
            binding.isImageAvailable = true
            viewModel.productModel.value.imgSrc = File(currentFilePath).name
            viewModel.productModel.value.imgSrc?.let { loadImage(it) }
        } else {
            binding.isImageAvailable = false
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun compressImage(imageUri: String): String? {
        var scaledBitmap: Bitmap? = null
        val options = BitmapFactory.Options()

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true
        var bmp = BitmapFactory.decodeFile(imageUri, options)
        var actualHeight = options.outHeight
        var actualWidth = options.outWidth

//      max Height and width values of the compressed image is taken as 816x612
        val maxHeight = 816.0f * 2
        val maxWidth = 612.0f * 2
        var imgRatio = (actualWidth / actualHeight).toFloat()
        val maxRatio = maxWidth / maxHeight

//      width and height values are set maintaining the aspect ratio of the image
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight
                actualWidth = (imgRatio * actualWidth).toInt()
                actualHeight = maxHeight.toInt()
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth
                actualHeight = (imgRatio * actualHeight).toInt()
                actualWidth = maxWidth.toInt()
            } else {
                actualHeight = maxHeight.toInt()
                actualWidth = maxWidth.toInt()
            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true
        options.inInputShareable = true
        options.inTempStorage = ByteArray(16 * 1024)
        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(imageUri, options)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }
        val ratioX = actualWidth / options.outWidth.toFloat()
        val ratioY = actualHeight / options.outHeight.toFloat()
        val middleX = actualWidth / 2.0f
        val middleY = actualHeight / 2.0f
        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
        val canvas = Canvas(scaledBitmap!!)
        canvas.setMatrix(scaleMatrix)
        canvas.drawBitmap(
            bmp,
            middleX - bmp.width / 2,
            middleY - bmp.height / 2,
            Paint(Paint.FILTER_BITMAP_FLAG)
        )

//      check the rotation of the image and display it properly
        val exif: ExifInterface
        try {
            exif = ExifInterface(imageUri!!)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, 0
            )
            Log.d("EXIF", "Exif: $orientation")
            val matrix = Matrix()
            if (orientation == 6) {
                matrix.postRotate(90F)
                Log.d("EXIF", "Exif: $orientation")
            } else if (orientation == 3) {
                matrix.postRotate(180F)
                Log.d("EXIF", "Exif: $orientation")
            } else if (orientation == 8) {
                matrix.postRotate(270F)
                Log.d("EXIF", "Exif: $orientation")
            }
            scaledBitmap = Bitmap.createBitmap(
                scaledBitmap, 0, 0,
                scaledBitmap.width, scaledBitmap.height, matrix,
                true
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
        var out: FileOutputStream? = null
        val filename = currentFilePath
        try {
            out = FileOutputStream(filename)

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, out)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return filename
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int,
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val heightRatio = (height.toFloat() / reqHeight.toFloat()).roundToInt()
            val widthRatio = (width.toFloat() / reqWidth.toFloat()).roundToInt()
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        val totalPixels = (width * height).toFloat()
        val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++
        }
        return inSampleSize
    }
}