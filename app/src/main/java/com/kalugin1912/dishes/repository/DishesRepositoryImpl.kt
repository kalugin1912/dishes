package com.kalugin1912.dishes.repository

import com.kalugin1912.dishes.data.Dish
import com.kalugin1912.dishes.data.source.DishesSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DishesRepositoryImpl(
    private val dishesSource: DishesSource,
    private val ioDispatcher: CoroutineDispatcher,
) : DishesRepository {

    override suspend fun getDish(id: String): Dish = withContext(ioDispatcher) {
        dishesSource.getDish(id)
    }

    override fun getDishesStream() = dishesSource.getDishesStream().map { response ->
        when (response.isLoading) {
            true -> DishesState.Loading
            false -> DishesState.Result(response.data)
        }
    }

    override suspend fun deleteDishes(ids: Set<String>): Unit = withContext(ioDispatcher) {
        coroutineScope {
            ids
                .map { id -> launch { dishesSource.deleteDish(id) } }
                .joinAll()
        }
    }

    override suspend fun loadAll() = withContext(ioDispatcher) {
        dishesSource.loadAll()
    }

}

sealed interface DishesState {
    object Loading : DishesState
    data class Result(val dishes: List<Dish>) : DishesState
}