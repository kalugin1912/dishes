package com.kalugin1912.dishes.dishes

import com.kalugin1912.dishes.data.repositories.DishesRepository

class DeleteDishesUseCase(private val dishesRepository: DishesRepository) {

    suspend fun execute(dishesIds: Set<String>) {
        if (dishesIds.isNotEmpty()) {
            dishesRepository.deleteDishes(dishesIds)
        }
    }

}