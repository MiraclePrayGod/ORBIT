package com.orbit.di

import android.content.Context
import com.orbit.data.dao.*
import com.orbit.data.database.OrbitDatabase
import com.orbit.data.database.DatabaseInitializer
import com.orbit.data.repository.OrbitRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideOrbitDatabase(@ApplicationContext context: Context): OrbitDatabase {
        return OrbitDatabase.getDatabase(context)
    }
    
    // DAO providers - necesarios para inyección de dependencias
    @Provides
    fun provideClientDao(database: OrbitDatabase): ClientDao = database.clientDao()
    
    @Provides
    fun provideProductDao(database: OrbitDatabase): ProductDao = database.productDao()
    
    @Provides
    fun provideOrderDao(database: OrbitDatabase): OrderDao = database.orderDao()
    
    @Provides
    fun provideOrderItemDao(database: OrbitDatabase): OrderItemDao = database.orderItemDao()
    
    @Provides
    fun providePaymentDao(database: OrbitDatabase): PaymentDao = database.paymentDao()
    
    @Provides
    fun provideInstallmentDao(database: OrbitDatabase): InstallmentDao = database.installmentDao()
    
    @Provides
    fun provideInventoryMovementDao(database: OrbitDatabase): InventoryMovementDao = database.inventoryMovementDao()
    
    @Provides
    @Singleton
    fun provideOrbitRepository(
        clientDao: ClientDao,
        productDao: ProductDao,
        orderDao: OrderDao,
        orderItemDao: OrderItemDao,
        paymentDao: PaymentDao,
        installmentDao: InstallmentDao,
        inventoryMovementDao: InventoryMovementDao
    ): OrbitRepository {
        return OrbitRepository(
            clientDao = clientDao,
            productDao = productDao,
            orderDao = orderDao,
            orderItemDao = orderItemDao,
            paymentDao = paymentDao,
            installmentDao = installmentDao,
            inventoryMovementDao = inventoryMovementDao
        )
    }
    
    @Provides
    @Singleton
    fun provideDatabaseInitializer(repository: OrbitRepository): DatabaseInitializer {
        return DatabaseInitializer(repository)
    }
}
