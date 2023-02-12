package com.kalugin1912.dishes.dishes

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kalugin1912.dishes.R
import com.kalugin1912.dishes.ServiceLocator
import com.kalugin1912.dishes.collectWhenUIVisible
import com.kalugin1912.dishes.databinding.FragmentDishesBinding
import com.kalugin1912.dishes.databinding.LayoutEmptyStateDishesBinding
import com.kalugin1912.dishes.view.VerticalMarginItemDecoration
import com.kalugin1912.dishes.details.DetailsFragment
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class DishesFragment : Fragment(R.layout.fragment_dishes) {

    companion object {
        fun newInstance() = DishesFragment()
    }

    private lateinit var dishesAdapter: DishesAdapter

    private lateinit var binding: FragmentDishesBinding
    private var emptyStateBinding: LayoutEmptyStateDishesBinding? = null

    private val dishesViewModel by viewModels<DishesViewModel>(
        factoryProducer = ServiceLocator::dishesViewModelFactory
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDishesBinding.bind(view)

        binding.delete.setOnClickListener {
            dishesViewModel.deleteDishes()
        }

        binding.include.emptyStateContainerStub.setOnInflateListener { _, inflated ->
            emptyStateBinding = LayoutEmptyStateDishesBinding.bind(inflated)
            emptyStateBinding?.restoreAllDishes?.setOnClickListener {
                dishesViewModel.reloadAll()
            }
        }

        dishesAdapter = DishesAdapter(
            onCheckChanged = { id, isChecked -> dishesViewModel.checkDish(id, isChecked) },
            onClicked = { id -> openDishDetails(id) }
        )

        prepareRecyclerView(dishesAdapter)

        dishesViewModel.isButtonDeleteEnabled.collectWhenUIVisible(viewLifecycleOwner) { isEnabled ->
            binding.delete.isEnabled = isEnabled
        }

        dishesViewModel.dishes.collectWhenUIVisible(viewLifecycleOwner, block = ::handleUiState)
    }

    private fun prepareRecyclerView(adapter: RecyclerView.Adapter<*>) {
        binding.include.dishesRecyclerView.apply {
            setHasFixedSize(true)
            this.adapter = adapter

            val orientation = resources.configuration.orientation

            layoutManager = if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                GridLayoutManager(context, 2)
            } else {
                LinearLayoutManager(context)
            }

            val margin = resources.getDimensionPixelSize(R.dimen.vertical_margin)

            addItemDecoration(VerticalMarginItemDecoration(margin))
        }
    }

    private fun handleUiState(uiState: DishesUiState) {
        binding.include.dishProgress.isVisible = uiState is DishesUiState.Loading
        binding.delete.isVisible = uiState is DishesUiState.Loaded
        binding.include.dishesRecyclerView.isVisible = uiState is DishesUiState.Loaded
        binding.include.emptyStateContainerStub.isVisible = uiState is DishesUiState.EmptyState

        when (uiState) {
            is DishesUiState.Loaded -> dishesAdapter.submitList(uiState.dishes)
            is DishesUiState.EmptyState,
            is DishesUiState.Loading,
            -> dishesAdapter.submitList(emptyList())
        }
    }

    private fun openDishDetails(id: String) {
        if (parentFragmentManager.findFragmentByTag(id) == null) {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.main_container, DetailsFragment.newInstance(id), id)
                .addToBackStack(null)
                .commit()
        }
    }
}