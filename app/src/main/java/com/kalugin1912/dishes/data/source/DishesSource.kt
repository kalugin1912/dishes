package com.kalugin1912.dishes.data.source

import com.kalugin1912.dishes.data.Dish
import com.kalugin1912.dishes.data.NetworkResponse
import kotlinx.coroutines.flow.Flow

interface DishesSource {

    suspend fun getDish(id: String): Dish

    suspend fun deleteDish(id: String)

    fun getDishesStream(): Flow<NetworkResponse<List<Dish>>>

    suspend fun loadAll()

}