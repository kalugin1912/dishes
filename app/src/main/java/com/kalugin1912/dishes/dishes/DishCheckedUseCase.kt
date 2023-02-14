package com.kalugin1912.dishes.dishes

object DishCheckedUseCase {

    fun execute(dishId: String, isChecked: Boolean, checkedDishes: Set<String>): Set<String> {
        return if (isChecked) {
            checkedDishes + dishId
        } else {
            checkedDishes - dishId
        }
    }

}