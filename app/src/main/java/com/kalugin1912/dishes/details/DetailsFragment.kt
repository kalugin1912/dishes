package com.kalugin1912.dishes.details

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kalugin1912.dishes.R
import com.kalugin1912.dishes.ServiceLocator
import com.kalugin1912.dishes.collectWhenUIVisible
import com.kalugin1912.dishes.databinding.AppBarDishDetailsBinding
import com.kalugin1912.dishes.databinding.LayoutDetailsErrorBinding
import com.kalugin1912.dishes.load

class DetailsFragment : Fragment(R.layout.app_bar_dish_details) {

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

    private var fragmentDishDetailsBinding: AppBarDishDetailsBinding? = null
    private var errorStateBinding: LayoutDetailsErrorBinding? = null

    private val dishesViewModel by viewModels<DishDetailsViewModel>(
        factoryProducer = {
            val id = requireArguments().getDishId ?: error("Dish id is required")
            ServiceLocator.createDishDetailsViewModelFactory(id)
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentDishDetailsBinding = AppBarDishDetailsBinding.bind(view)

        fragmentDishDetailsBinding?.toolbar?.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        fragmentDishDetailsBinding?.includeDishFragment?.errorContainerStub?.setOnInflateListener { _, inflatedView ->
            errorStateBinding = LayoutDetailsErrorBinding.bind(inflatedView)
            errorStateBinding?.reload?.setOnClickListener {
                dishesViewModel.reloadDish()
            }
        }

        dishesViewModel.uiState.collectWhenUIVisible(viewLifecycleOwner, block = ::handleUiState)
    }

    private fun handleUiState(uiState: DetailsUIState) {
        fragmentDishDetailsBinding?.includeDishFragment?.apply {
            dishDetailsProgress.isVisible = uiState is DetailsUIState.Loading
            includeDishContent.visibleGroups.isVisible = uiState is DetailsUIState.Loaded
            errorContainerStub.isVisible = uiState is DetailsUIState.Error

            when (uiState) {
                is DetailsUIState.Loaded -> {
                    val dish = uiState.dish
                    includeDishContent.apply {
                        dishPhoto.load(dish.image)
                        title.text = dish.name
                        description.text = dish.description
                        price.text = requireContext().getString(R.string.dollar, dish.price)
                    }
                }
                is DetailsUIState.Error -> errorStateBinding?.apply {
                    errorMessage.text = uiState.message
                }
                is DetailsUIState.Loading -> Unit
            }
        }
    }
}