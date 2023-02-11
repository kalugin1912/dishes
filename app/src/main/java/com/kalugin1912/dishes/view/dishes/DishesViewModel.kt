package com.kalugin1912.dishes.view.dishes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kalugin1912.dishes.data.Checkable
import com.kalugin1912.dishes.data.Dish
import com.kalugin1912.dishes.repository.DishesRepository
import com.kalugin1912.dishes.repository.DishesState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DishesViewModel(private val dishesRepository: DishesRepository) : ViewModel() {

    private val checkedDishes = MutableStateFlow<Set<String>>(emptySet())

    val dishes = combine(
        flow = dishesRepository.getDishesStream(),
        flow2 = checkedDishes,
        transform = { dishState, checkedIds ->
            when (dishState) {
                is DishesState.Loading -> DishesUiState.Loading
                is DishesState.Result -> {
                    if (dishState.dishes.isEmpty()) {
                        DishesUiState.EmptyState
                    } else {
                        val data = dishState.dishes.map { dish: Dish ->
                            Checkable(value = dish, dish.id in checkedIds)
                        }
                        DishesUiState.Loaded(dishes = data)
                    }
                }
            }
        }
    )
        .stateIn(viewModelScope, SharingStarted.Lazily, DishesUiState.Loading)

    val isButtonDeleteEnabled = checkedDishes
        .map { dishes -> dishes.isNotEmpty() }
        .distinctUntilChanged()

    fun checkDish(dishId: String, isChecked: Boolean) {
        checkedDishes.update { dishes ->
            if (isChecked) {
                dishes + dishId
            } else {
                dishes - dishId
            }
        }
    }

    fun deleteDishes() {
        viewModelScope.launch {
            val checkedDishesIds = checkedDishes.value
            if (checkedDishesIds.isNotEmpty()) {
                checkedDishes.emit(emptySet())
                dishesRepository.deleteDishes(checkedDishesIds)
            }
        }
    }

    fun reloadAll() {
        viewModelScope.launch {
            dishesRepository.loadAll()
        }
    }
}

sealed interface DishesUiState {
    object EmptyState : DishesUiState
    object Loading : DishesUiState

    data class Loaded(val dishes: List<Checkable<Dish>>) : DishesUiState
}