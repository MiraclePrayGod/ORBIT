package com.orbit.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbit.data.entity.ProductCategory
import com.orbit.data.entity.InventoryStatus
import com.orbit.data.repository.OrbitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val repository: OrbitRepository
) : ViewModel() {
    
    private val _selectedCategory = MutableStateFlow(ProductCategory.ALL)
    val selectedCategory: StateFlow<ProductCategory> = _selectedCategory.asStateFlow()
    
    val products: StateFlow<List<com.orbit.data.entity.Product>> = 
        repository.getAllProducts()
            .combine(_selectedCategory) { allProducts, category ->
                when (category) {
                    ProductCategory.ALL -> allProducts
                    else -> allProducts.filter { it.category == category }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    
    val lowStockProducts: StateFlow<List<com.orbit.data.entity.Product>> = 
        repository.getLowStockProducts()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    
    fun setCategory(category: ProductCategory) {
        _selectedCategory.value = category
    }
    
    fun updateProductQuantity(productId: Long, newQuantity: Int) {
        viewModelScope.launch {
            repository.updateProductQuantity(productId, newQuantity)
            
            // Update status based on new quantity
            val newStatus = when {
                newQuantity <= 0 -> InventoryStatus.OUT_OF_STOCK
                newQuantity <= 10 -> InventoryStatus.LOW_STOCK
                else -> InventoryStatus.AVAILABLE
            }
            repository.updateProductStatus(productId, newStatus)
        }
    }
    
    fun addProductQuantity(productId: Long, quantityToAdd: Int) {
        viewModelScope.launch {
            val product = repository.getProductById(productId)
            product?.let {
                val newQuantity = it.availableQuantity + quantityToAdd
                updateProductQuantity(productId, newQuantity)
            }
        }
    }
    
    fun deleteProduct(productId: Long) {
        viewModelScope.launch {
            val product = repository.getProductById(productId)
            product?.let {
                repository.deleteProduct(it)
            }
        }
    }
}

