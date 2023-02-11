package com.kalugin1912.dishes.view.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kalugin1912.dishes.repository.DishesRepository

class DishDetailsViewModelFactory(
    private val id: String,
    private val repository: DishesRepository,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DishDetailsViewModel(id, repository) as T
    }
}