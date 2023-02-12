package com.kalugin1912.dishes.data.repositories

import com.kalugin1912.dishes.data.Dish
import kotlinx.coroutines.flow.Flow

interface DishesRepository {

    suspend fun getDish(id: String): Dish

    fun getDishesStream(): Flow<DishesState>

    suspend fun deleteDishes(ids: Set<String>)

    suspend fun loadAll()

}