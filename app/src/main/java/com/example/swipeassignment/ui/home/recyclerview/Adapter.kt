package com.example.swipeassignment.ui.home.recyclerview

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.swipeassignment.R
import com.example.swipeassignment.data.Product
import com.example.swipeassignment.databinding.ProductCardBinding
import java.util.Locale

class Adapter(private val productList : List<Product>) : RecyclerView.Adapter<Adapter.ViewHolder>(), Filterable {


    private var filteredProductsList : List<Product> = productList

    inner class ViewHolder(binding: ProductCardBinding) : RecyclerView.ViewHolder(binding.root){

        private val productName = binding.productName

        private val productType = binding.productType

        private val productPrice = binding.productPrice

        private val productTax = binding.productTax

        private val productIcon = binding.productIcon

        fun bind(product: Product) {
            productName.text = product.product_name
            productType.text = product.product_type
            productPrice.text = product.price.toString()
            productTax.text = product.tax.toString()

            val url = product.image

            Glide.with(productIcon)
                .load(url)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(productIcon)


        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val binding = ProductCardBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return filteredProductsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(filteredProductsList[holder.adapterPosition])
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                val query = constraint.toString().lowercase(Locale.ROOT).trim()

                filteredProductsList = if (query.isEmpty()){
                    productList
                }else {
                    productList.filter {
                        it.product_name.lowercase(Locale.ROOT).contains(query) ||
                                it.product_type.lowercase(Locale.ROOT).contains(query)
                    }
                }

                filterResults.values = filteredProductsList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                Log.d("Custom Tag", results.toString())
                filteredProductsList = results?.values as? List<Product> ?: emptyList()
                Log.d("Custom Tag", filteredProductsList.toString())
                notifyDataSetChanged()
            }
        }
    }
}