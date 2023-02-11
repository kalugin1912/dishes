package com.kalugin1912.dishes.data.source

import com.kalugin1912.dishes.Stubs
import com.kalugin1912.dishes.data.Dish
import com.kalugin1912.dishes.data.NetworkResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update

object FakeRemoteDishesSource : DishesSource {

    private val delays = listOf(50L, 100L, 200L, 500L, 800L, 1000L, 2000L)

    private val randomDelay: Long get() = delays.shuffled().first()

    private val networkStatus = MutableStateFlow(NetworkResponse(isLoading = false, data = Stubs.dishes))

    override suspend fun getDish(id: String): Dish {
        delay(randomDelay)
        return networkStatus.value.data
            .find { dish: Dish -> dish.id == id }
            ?: error("Dish with id = $id not found")
    }

    override suspend fun deleteDish(id: String) {
        val removingDish = networkStatus.value.data.find { dish: Dish -> dish.id == id }
        if (removingDish != null) {
            networkStatus.update { networkStatus ->
                networkStatus.copy(
                    isLoading = false,
                    data = networkStatus.data - removingDish
                )
            }
        }
    }

    override fun getDishesStream() = flow {
        delay(randomDelay)
        emitAll(networkStatus)
    }

    override suspend fun loadAll() {
        networkStatus.emit(
            networkStatus.value.copy(isLoading = true, data = emptyList())
        )
        delay(randomDelay)

        networkStatus.emit(
            networkStatus.value.copy(isLoading = false, data = Stubs.dishes)
        )
    }

}