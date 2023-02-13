package com.kalugin1912.dishes.dishes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kalugin1912.dishes.data.Checkable
import com.kalugin1912.dishes.data.Dish
import com.kalugin1912.dishes.data.repositories.DishesRepository
import com.kalugin1912.dishes.data.repositories.DishesState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DishesViewModel(
    private val dishesRepository: DishesRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private companion object {
        const val CHECKED_ITEMS_KEY = "checked_items_key"
    }

    private val checkedDishes = savedStateHandle.getStateFlow(CHECKED_ITEMS_KEY, emptySet<String>())

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
        savedStateHandle[CHECKED_ITEMS_KEY] = checkedDishes.value.let { dishes ->
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
                savedStateHandle[CHECKED_ITEMS_KEY] = emptySet<String>()
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