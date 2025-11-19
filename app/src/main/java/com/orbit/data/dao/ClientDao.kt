package com.orbit.data.dao

import androidx.room.*
import com.orbit.data.entity.Client
import com.orbit.data.relation.ClientWithOrders
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {
    
    @Query("SELECT * FROM clients ORDER BY name ASC")
    fun getAllClients(): Flow<List<Client>>
    
    @Query("SELECT * FROM clients WHERE id = :clientId")
    suspend fun getClientById(clientId: Long): Client?
    
    @Query("SELECT * FROM clients WHERE phone = :phone")
    suspend fun getClientByPhone(phone: String): Client?
    
    @Query("SELECT * FROM clients WHERE name LIKE '%' || :query || '%' OR phone LIKE '%' || :query || '%'")
    fun searchClients(query: String): Flow<List<Client>>
    
    @Transaction
    @Query("SELECT * FROM clients WHERE id = :clientId")
    suspend fun getClientWithOrders(clientId: Long): ClientWithOrders?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClient(client: Client): Long
    
    @Update
    suspend fun updateClient(client: Client)
    
    @Delete
    suspend fun deleteClient(client: Client)
    
    @Query("DELETE FROM clients WHERE id = :clientId")
    suspend fun deleteClientById(clientId: Long)
    
    @Query("SELECT COUNT(*) FROM clients")
    suspend fun getClientCount(): Int
}

