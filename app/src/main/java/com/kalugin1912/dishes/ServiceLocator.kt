package com.kalugin1912.dishes

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kalugin1912.dishes.data.source.DishesSource
import com.kalugin1912.dishes.data.source.FakeRemoteDishesSource
import com.kalugin1912.dishes.data.repositories.DishesRepository
import com.kalugin1912.dishes.data.repositories.DishesRepositoryImpl
import com.kalugin1912.dishes.details.DishDetailsViewModelFactory
import com.kalugin1912.dishes.dishes.DishesViewModel
import kotlinx.coroutines.Dispatchers

object ServiceLocator {

    private val remoteDishesSources: DishesSource = FakeRemoteDishesSource

    private val dishesRepository: DishesRepository by lazy(LazyThreadSafetyMode.NONE) {
        DishesRepositoryImpl(dishesSource = remoteDishesSources, ioDispatcher = Dispatchers.IO)
    }

    val dishesViewModelFactory by lazy(LazyThreadSafetyMode.NONE) {
        object : AbstractSavedStateViewModelFactory() {
            override fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
                return DishesViewModel(dishesRepository, handle) as T
            }
        }
    }


    fun createDishDetailsViewModelFactory(id: String): ViewModelProvider.Factory {
        return DishDetailsViewModelFactory(id = id, repository = dishesRepository)
    }
}