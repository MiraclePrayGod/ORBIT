package com.orbit.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbit.data.entity.ProductCategory
import com.orbit.data.entity.InventoryStatus
import com.orbit.data.entity.MovementType
import com.orbit.data.repository.OrbitRepository
import com.orbit.domain.usecase.inventory.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val repository: OrbitRepository,
    private val addProductUseCase: AddProductUseCase,
    private val updateStockUseCase: UpdateStockUseCase,
    private val checkLowStockUseCase: CheckLowStockUseCase,
    private val deleteProductUseCase: DeleteProductUseCase
) : ViewModel() {
    
    private val _selectedCategory = MutableStateFlow(ProductCategory.ALL)
    private val _searchQuery = MutableStateFlow("")
    
    // UI State con sealed class
    private val _uiState = MutableStateFlow<InventoryUiState>(InventoryUiState.Loading)
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()
    
    // Estadísticas de inventario
    val inventoryStats: StateFlow<InventoryStats> = 
        repository.getAllProducts()
            .map { products ->
                InventoryStats(
                    totalProducts = products.size,
                    lowStockCount = products.count { it.status == InventoryStatus.LOW_STOCK },
                    outOfStockCount = products.count { it.status == InventoryStatus.OUT_OF_STOCK },
                    totalValue = products.sumOf { it.unitPrice * it.totalQuantity },
                    availableValue = products.sumOf { it.unitPrice * it.availableQuantity }
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = InventoryStats()
            )
    
    // Alertas de stock bajo
    val lowStockAlerts: StateFlow<List<StockAlert>> = 
        checkLowStockUseCase()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    
    // Productos filtrados con búsqueda y categoría
    private val filteredProducts: StateFlow<List<com.orbit.data.entity.Product>> = 
        combine(
            repository.getAllProducts(),
            _selectedCategory,
            _searchQuery
        ) { products, category, query ->
            products
                .filter { 
                    category == ProductCategory.ALL || it.category == category 
                }
                .filter {
                    query.isBlank() || 
                    it.name.contains(query, ignoreCase = true) ||
                    it.description?.contains(query, ignoreCase = true) == true
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    init {
        // Observar cambios y actualizar UI State
        // Combinar en dos pasos porque combine solo soporta hasta 5 parámetros
        viewModelScope.launch {
            combine(
                repository.getAllProducts(),
                filteredProducts,
                _searchQuery,
                _selectedCategory,
                lowStockAlerts
            ) { allProducts: List<com.orbit.data.entity.Product>,
                filtered: List<com.orbit.data.entity.Product>,
                query: String,
                category: ProductCategory,
                alerts: List<StockAlert> ->
                // Combinar con inventoryStats
                inventoryStats.map { stats ->
                    InventoryUiState.Success(
                        products = allProducts,
                        filteredProducts = filtered,
                        searchQuery = query,
                        selectedCategory = category,
                        lowStockAlerts = alerts,
                        inventoryStats = stats
                    )
                }
            }
                .flatMapLatest { it }
                .catch { e ->
                    _uiState.value = InventoryUiState.Error(e.message ?: "Error desconocido")
                }
                .collect { state ->
                    _uiState.value = state
                }
        }
    }
    
    fun setCategory(category: ProductCategory) {
        _selectedCategory.value = category
    }
    
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun addProduct(product: com.orbit.data.entity.Product) {
        viewModelScope.launch {
            _uiState.value = InventoryUiState.Loading
            addProductUseCase(product)
                .onSuccess { 
                    // El estado se actualizará automáticamente por el Flow
                }
                .onFailure { e ->
                    _uiState.value = InventoryUiState.Error(e.message ?: "Error al agregar producto")
                }
        }
    }
    
    fun updateStock(
        productId: Long,
        quantity: Int,
        movementType: MovementType,
        reason: String? = null
    ) {
        viewModelScope.launch {
            _uiState.value = InventoryUiState.Loading
            updateStockUseCase(productId, quantity, movementType, reason)
                .onSuccess {
                    // El estado se actualizará automáticamente por el Flow
                }
                .onFailure { e ->
                    _uiState.value = InventoryUiState.Error(e.message ?: "Error al actualizar stock")
                }
        }
    }
    
    fun addProductQuantity(productId: Long, quantityToAdd: Int) {
        updateStock(productId, quantityToAdd, MovementType.STOCK_IN, "Aumento manual de stock")
    }
    
    fun subtractProductQuantity(productId: Long, quantityToSubtract: Int) {
        updateStock(productId, quantityToSubtract, MovementType.STOCK_OUT, "Reducción manual de stock")
    }
    
    fun adjustProductQuantity(productId: Long, newQuantity: Int) {
        viewModelScope.launch {
            val product = repository.getProductById(productId)
            product?.let {
                val adjustment = newQuantity - it.availableQuantity
                updateStock(productId, adjustment, MovementType.ADJUSTMENT, "Ajuste manual de inventario")
            }
        }
    }
    
    fun deleteProduct(productId: Long) {
        viewModelScope.launch {
            _uiState.value = InventoryUiState.Loading
            deleteProductUseCase(productId)
                .onSuccess {
                    // El estado se actualizará automáticamente por el Flow
                }
                .onFailure { e ->
                    _uiState.value = InventoryUiState.Error(e.message ?: "Error al eliminar producto")
                }
        }
    }
    
    fun refresh() {
        viewModelScope.launch {
            _uiState.value = InventoryUiState.Loading
            // El estado se actualizará automáticamente por los Flows
        }
    }
}

