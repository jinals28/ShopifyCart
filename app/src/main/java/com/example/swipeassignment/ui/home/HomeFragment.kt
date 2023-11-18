package com.example.swipeassignment.ui.home

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.swipeassignment.R
import com.example.swipeassignment.data.APIService
import com.example.swipeassignment.data.RetrofitClient
import com.example.swipeassignment.databinding.FragmentHomeBinding
import com.example.swipeassignment.repository.Repository
import com.example.swipeassignment.ui.home.recyclerview.Adapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    private lateinit var apiService : APIService

    private lateinit var repository: Repository

    private lateinit var homeViewModel: HomeViewModel

    private lateinit var recyclerView: RecyclerView

    private lateinit var searchView: SearchView

    private var productAdapter : Adapter = Adapter(emptyList())

    private lateinit var progressBar: ProgressBar

    private lateinit var addProduct : FloatingActionButton

    private lateinit var noInternetLayout : LinearLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)


        apiService = RetrofitClient.create()
        repository = Repository(apiService)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchView = binding.searchView

        recyclerView = binding.recyclerView

        progressBar = binding.progressBar

        addProduct = binding.fab

        noInternetLayout = binding.noInternetLayout

        homeViewModel = ViewModelProvider(this, HomeViewModelFactory(repository))[HomeViewModel::class.java]

        if (!isConnectedToInternet(requireContext())){
            progressBar.visibility = View.GONE
            noInternetLayout.visibility = View.VISIBLE
        }else{
            noInternetLayout.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productAdapter
        }

        homeViewModel.isLoading.observe(viewLifecycleOwner){isLoading ->
            if (isLoading){
                progressBar.visibility = View.VISIBLE
                searchView.visibility = View.GONE
                recyclerView.visibility = View.GONE
                addProduct.visibility = View.GONE
            }else{
                progressBar.visibility = View.GONE
                searchView.visibility = View.VISIBLE
                recyclerView.visibility = View.VISIBLE
                addProduct.visibility = View.VISIBLE
            }
        }


        homeViewModel.productList.observe(viewLifecycleOwner){
            Log.d("Custom tag", it.toString())
            productAdapter = Adapter(it!!)
            recyclerView.adapter = productAdapter
        }

        homeViewModel.apiError.observe(viewLifecycleOwner){
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                productAdapter.filter.filter(newText)
                return true
            }

        })

        addProduct.setOnClickListener{
            if (findNavController().currentDestination!!.id == R.id.HomeFragment){
                val action = HomeFragmentDirections.actionHomeFragmentToSecondFragment()
                findNavController().navigate(action)
            }
        }


    }

    private fun isConnectedToInternet(requireContext: Context): Boolean {

        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val activeNetwork =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false

            return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }else{
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo?.isConnected == true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}