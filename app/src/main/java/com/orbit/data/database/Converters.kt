package com.orbit.data.database

import androidx.room.TypeConverter
import com.orbit.data.entity.*

class Converters {
    
    @TypeConverter
    fun fromProductCategory(category: ProductCategory): String {
        return category.name
    }
    
    @TypeConverter
    fun toProductCategory(category: String): ProductCategory {
        return ProductCategory.valueOf(category)
    }
    
    @TypeConverter
    fun fromInventoryStatus(status: InventoryStatus): String {
        return status.name
    }
    
    @TypeConverter
    fun toInventoryStatus(status: String): InventoryStatus {
        return InventoryStatus.valueOf(status)
    }
    
    @TypeConverter
    fun fromOrderStatus(status: OrderStatus): String {
        return status.name
    }
    
    @TypeConverter
    fun toOrderStatus(status: String): OrderStatus {
        return OrderStatus.valueOf(status)
    }
    
    @TypeConverter
    fun fromPaymentMethod(method: PaymentMethod): String {
        return method.name
    }
    
    @TypeConverter
    fun toPaymentMethod(method: String): PaymentMethod {
        return PaymentMethod.valueOf(method)
    }
    
    @TypeConverter
    fun fromMovementType(type: MovementType): String {
        return type.name
    }
    
    @TypeConverter
    fun toMovementType(type: String): MovementType {
        return MovementType.valueOf(type)
    }
}

