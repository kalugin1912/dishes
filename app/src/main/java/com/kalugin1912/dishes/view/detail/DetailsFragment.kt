package com.kalugin1912.dishes.view.detail

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.kalugin1912.dishes.R
import com.kalugin1912.dishes.ServiceLocator
import com.kalugin1912.dishes.databinding.FragmentDishDetailsBinding
import com.kalugin1912.dishes.databinding.LayoutDetailsErrorBinding
import com.kalugin1912.dishes.load
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class DetailsFragment : Fragment(R.layout.fragment_dish_details) {

    companion object {

        private const val DISH_ID_KEY = "dish_id_key"

        private val Bundle.getDishId: String?
            get() = getString(DISH_ID_KEY)

        fun newInstance(dishId: String) = DetailsFragment().apply {
            arguments = Bundle().apply {
                putString(DISH_ID_KEY, dishId)
            }
        }
    }

    private var fragmentDishDetailsBinding: FragmentDishDetailsBinding? = null
    private var errorStateBinding: LayoutDetailsErrorBinding? = null

    private val dishesViewModel by viewModels<DishDetailsViewModel>(
        factoryProducer = {
            val id = requireArguments().getDishId ?: error("Dish id is required")
            ServiceLocator.createDishDetailsViewModelFactory(id)
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentDishDetailsBinding = FragmentDishDetailsBinding.bind(view)

        fragmentDishDetailsBinding?.errorContainerStub?.setOnInflateListener { _, inflatedView ->
            errorStateBinding = LayoutDetailsErrorBinding.bind(inflatedView)
            errorStateBinding?.reload?.setOnClickListener {
                dishesViewModel.reloadDish()
            }
        }
        dishesViewModel.uiState.onEach(::handleUiState).launchIn(lifecycleScope)
    }

    private fun handleUiState(uiState: DetailsUIState) {
        fragmentDishDetailsBinding?.apply {
            dishDetailsProgress.isVisible = uiState is DetailsUIState.Loading
            visibleGroups.isVisible = uiState is DetailsUIState.Loaded
            errorContainerStub.isVisible = uiState is DetailsUIState.Error

            when (uiState) {
                is DetailsUIState.Loaded -> {
                    val dish = uiState.dish
                    dishPhoto.load(dish.image)
                    title.text = dish.name
                    description.text = dish.description
                    price.text = requireContext().getString(R.string.dollar, dish.price)
                }
                is DetailsUIState.Error -> errorStateBinding?.apply {
                    errorMessage.text = uiState.message
                }
                is DetailsUIState.Loading -> Unit
            }
        }
    }
}