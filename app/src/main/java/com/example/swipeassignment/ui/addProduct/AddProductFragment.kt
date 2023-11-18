package com.example.swipeassignment.ui.addProduct

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.graphics.drawable.toBitmapOrNull
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.swipeassignment.R
import com.example.swipeassignment.data.APIService
import com.example.swipeassignment.data.RetrofitClient
import com.example.swipeassignment.databinding.FragmentAddProductBinding
import com.example.swipeassignment.repository.Repository
import com.example.swipeassignment.utils.Result
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.lang.Exception
import java.lang.Integer.min

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class AddProductFragment : Fragment() {

    private var _binding: FragmentAddProductBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var addProductViewModel : AddProductViewModel

    private lateinit var addProductBtn : Button

    private lateinit var nameEditText : TextInputEditText
    private lateinit var priceEditText : TextInputEditText
    private lateinit var taxEditText : TextInputEditText

    private lateinit var nameLayout : TextInputLayout
    private lateinit var priceLayout : TextInputLayout
    private lateinit var taxLayout : TextInputLayout
    private lateinit var spinner : Spinner

    private lateinit var productImage : ImageView

    private lateinit var getContent : ActivityResultLauncher<Intent>

    private lateinit var uploadImage : LinearLayout

    private lateinit var uploadImageText : TextView
    private lateinit var removeImageText : TextView

    private lateinit var uploadImageDrawable : ImageView

    private lateinit var apiService : APIService
    private lateinit var repository: Repository

    private lateinit var progressBar : CardView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddProductBinding.inflate(inflater, container, false)

        apiService = RetrofitClient.create()
        repository = Repository(apiService)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addProductViewModel = ViewModelProvider(this, AddViewModelFactory(repository))[AddProductViewModel::class.java]

        addProductBtn = binding.addProductButton

        nameEditText = binding.editTextName
        priceEditText = binding.editTextPrice
        taxEditText = binding.editTextTax
        nameLayout = binding.productNameInputLayout
        priceLayout = binding.priceInputLayout
        taxLayout = binding.taxInputLayout
        spinner = binding.spinnerProductType

        progressBar = binding.snackbarProgress.root

        productImage = binding.productImage

        uploadImage = binding.uploadImage

        removeImageText = binding.textRemove

        uploadImageText = binding.textUpload

        uploadImageDrawable = binding.uploadImageCard


        getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode == Activity.RESULT_OK){
                val data: Intent? = result.data
                data?.data?.let { uri ->
                    handleImageSelection(uri)
                }
            }

        }

        nameEditText.doAfterTextChanged {
            nameLayout.error = null
        }

        priceEditText.doAfterTextChanged {
            priceLayout.error = null
        }

        taxEditText.doAfterTextChanged {
            taxLayout.error = null
        }

        addProductViewModel.selectedImage.observe(viewLifecycleOwner){
            productImage.visibility = View.VISIBLE
            uploadImageDrawable.visibility = View.GONE
            uploadImageText.visibility = View.GONE
            removeImageText.visibility = View.VISIBLE
            productImage.setImageBitmap(it!!)

        }

        uploadImage.setOnClickListener {
            openGallery()
        }

        productImage.setOnClickListener {
            openGallery()
        }

        removeImageText.setOnClickListener {
            productImage.setImageDrawable(null)
            productImage.visibility = View.GONE
            uploadImageDrawable.visibility = View.VISIBLE
            uploadImageText.visibility = View.VISIBLE
            removeImageText.visibility = View.GONE
        }

        addProductBtn.setOnClickListener {

            clearValidationForm()

            val name = nameEditText.text.toString()
            val price = priceEditText.text.toString().toDoubleOrNull() ?: 0.0
            val tax = taxEditText.text.toString().toDoubleOrNull() ?: 0.0
            val category = spinner.selectedItem.toString()
            val imageBitmap = productImage.drawable?.toBitmapOrNull()



            if (addProductViewModel.validateProductInput(name, price, tax)) {


                val alert = AlertDialog.Builder(requireContext())
                    .setTitle("Are you sure to add this product?")
                    .setPositiveButton("Yes"){ dialog, _ ->
                        progressBar.visibility = View.VISIBLE
                        addProductViewModel.addProduct(
                            productName = name,
                            productPrice = price.toString(),
                            productTax = tax.toString(),
                            productType = category,
                            image = imageBitmap
                        )
                        dialog.dismiss()
                    }
                    .setNegativeButton("No"){ dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                alert.show()
            }

        }

        addProductViewModel.validationResult.observe(viewLifecycleOwner){ validationResult ->
            handleValidationResult(validationResult)

        }

        addProductViewModel.productResponse.observe(viewLifecycleOwner){ productResponse ->
            progressBar.visibility = View.GONE
            when(productResponse){
                is Result.Success ->{
                    val productRes = productResponse.data
                    val message = productRes.message
                    val alert = AlertDialog.Builder(requireContext())
                        .setTitle("Product Added Successfully")
                        .setMessage(" message : $message \n" +
                        "Product ID : ${productRes.product_id} \n")
                        .setPositiveButton("OK"){ dialog, _ ->
                            if (findNavController().currentDestination!!.id == R.id.addProductFragment){
                                val action = AddProductFragmentDirections.actionAddProductFragmentToHomeFragment()
                                findNavController().navigate(action)
                            }
                            dialog.dismiss()
                        }
                        .create()
                    alert.show()
                }
                is Result.Error -> {
                    val productRes = productResponse.errorMessage
                    val alert = AlertDialog.Builder(requireContext())
                        .setTitle(productRes)
                        .setPositiveButton("Go To Home"){ dialog, _ ->
                            if (findNavController().currentDestination!!.id == R.id.addProductFragment){
                                val action = AddProductFragmentDirections.actionAddProductFragmentToHomeFragment()
                                findNavController().navigate(action)
                            }
                            dialog.dismiss()
                        }
                        .create()
                    alert.show()
                }

                else -> {}
            }

        }

    }


    private fun handleImageSelection(uri: Uri) {
       try {
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            val source = ImageDecoder.createSource(requireActivity().contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        }else {
            MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
        }
           
        val scaleBitmap = scaleToSquare(bitmap)   
        addProductViewModel.setImage(scaleBitmap)
    } catch (ex: Exception){
        ex.printStackTrace()
    }
    }

    private fun scaleToSquare(bitmap: Bitmap): Bitmap {

        val width = bitmap.width
        val height = bitmap.height
        val size = min(width, height)

        val x = (width - size) / 2
        val y = (height - size)/2

        return Bitmap.createBitmap(bitmap, x, y, size, size)
    }

    private fun openGallery() {

        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getContent.launch(galleryIntent)
    }

    private fun handleValidationResult(validationResult: Result<Unit>) {

        when(validationResult) {

            is Result.Success -> {

            }
            is Result.ValidationErrors -> {
                for (error in validationResult.errors){
                    when(error) {
                        is Result.ValidationFailure.InvalidName -> {
                            nameLayout.error = error.errorMessage
                        }
                        is Result.ValidationFailure.InvalidPrice -> {
                            priceLayout.error = error.errorMessage
                        }
                        is Result.ValidationFailure.InvalidTax -> {
                            taxLayout.error = error.errorMessage
                        }
                        is Result.ValidationFailure.InvalidSpinnerSelection -> {
                        }
                    }
                }
            }
            else -> {

            }
        }

    }

    private fun clearValidationForm(){
        priceLayout.error = null
        taxLayout.error = null
        nameLayout.error = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}