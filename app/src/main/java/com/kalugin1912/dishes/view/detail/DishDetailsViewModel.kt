package com.kalugin1912.dishes.view.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kalugin1912.dishes.data.Dish
import com.kalugin1912.dishes.repository.DishesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DishDetailsViewModel(
    private val id: String,
    private val dishesRepository: DishesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailsUIState>(DetailsUIState.Loading)

    val uiState = _uiState.asStateFlow()

    init {
        reloadDish()
    }

    fun reloadDish() {
        viewModelScope.launch {
            _uiState.emit(DetailsUIState.Loading)
            val uiState = runCatching {
                val dish = dishesRepository.getDish(id)
                DetailsUIState.Loaded(dish)
            }
                .recover { t -> DetailsUIState.Error(t.localizedMessage) }
                .getOrDefault(DetailsUIState.Error(null))

            _uiState.emit(uiState)
        }
    }

}

sealed interface DetailsUIState {
    object Loading : DetailsUIState
    data class Loaded(val dish: Dish) : DetailsUIState
    data class Error(val message: String?) : DetailsUIState
}