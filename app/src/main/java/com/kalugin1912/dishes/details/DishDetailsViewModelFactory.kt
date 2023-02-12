package com.kalugin1912.dishes.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kalugin1912.dishes.data.repositories.DishesRepository

class DishDetailsViewModelFactory(
    private val id: String,
    private val repository: DishesRepository,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DishDetailsViewModel(id, repository) as T
    }
}