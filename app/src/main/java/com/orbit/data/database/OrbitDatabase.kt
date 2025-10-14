package com.orbit.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.orbit.data.entity.*
import com.orbit.data.dao.*

@Database(
    entities = [
        Client::class,
        Product::class,
        Order::class,
        OrderItem::class,
        Payment::class,
        Installment::class,
        InstallmentPayment::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class OrbitDatabase : RoomDatabase() {
    
    abstract fun clientDao(): ClientDao
    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao
    abstract fun orderItemDao(): OrderItemDao
    abstract fun paymentDao(): PaymentDao
    abstract fun installmentDao(): InstallmentDao
    
    companion object {
        @Volatile
        private var INSTANCE: OrbitDatabase? = null
        
        fun getDatabase(context: Context): OrbitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OrbitDatabase::class.java,
                    "orbit_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

