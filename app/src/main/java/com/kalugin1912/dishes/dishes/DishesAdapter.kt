package com.kalugin1912.dishes.dishes


import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.kalugin1912.dishes.R
import com.kalugin1912.dishes.data.Checkable
import com.kalugin1912.dishes.data.Dish
import com.kalugin1912.dishes.databinding.ItemDishBinding
import com.kalugin1912.dishes.inflater
import com.kalugin1912.dishes.load


class DishesAdapter(
    private val onCheckChanged: (id: String, isChecked: Boolean) -> Unit,
    private val onClicked: (id: String) -> Unit,
) : ListAdapter<Checkable<Dish>, DishViewHolder>(DIFF_UTIL_ITEM_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishViewHolder {
        val binding = ItemDishBinding.inflate(parent.inflater(), parent, false)
        return DishViewHolder(binding = binding, onCheckChanged = onCheckChanged, onClicked = onClicked)
    }

    override fun getItemCount() = currentList.size

    override fun getItemViewType(position: Int) = VIEW_TYPE

    override fun onBindViewHolder(holder: DishViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(holder: DishViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            val isChecked = payloads[0] as Boolean
            holder.bindCheckBox(isChecked = isChecked)
        }
    }


    private companion object {

        private const val VIEW_TYPE = 431

        val DIFF_UTIL_ITEM_CALLBACK = object : DiffUtil.ItemCallback<Checkable<Dish>>() {
            override fun areItemsTheSame(oldItem: Checkable<Dish>, newItem: Checkable<Dish>): Boolean {
                return newItem.value.id == oldItem.value.id
            }

            override fun areContentsTheSame(oldItem: Checkable<Dish>, newItem: Checkable<Dish>): Boolean {
                return oldItem == newItem
            }

            override fun getChangePayload(oldItem: Checkable<Dish>, newItem: Checkable<Dish>): Any? {
                return if (oldItem.isChecked != newItem.isChecked) {
                    newItem.isChecked
                } else {
                    super.getChangePayload(oldItem, newItem)
                }
            }

        }
    }
}

class DishViewHolder(
    private val binding: ItemDishBinding,
    private val onCheckChanged: (id: String, isChecked: Boolean) -> Unit,
    private val onClicked: (id: String) -> Unit,
) : ViewHolder(binding.root) {

    private var value: Checkable<Dish>? = null

    init {
        binding.dishPhoto.setOnClickListener {
            value?.let { value ->
                onClicked(value.value.id)
            }
        }
        binding.title.setOnClickListener {
            value?.let { value ->
                binding.checkbox.isChecked = !binding.checkbox.isChecked
                onCheckChanged(value.value.id, binding.checkbox.isChecked)
            }
        }
        binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
            value?.let { value ->
                onCheckChanged(value.value.id, isChecked)
            }
        }
    }

    fun bind(data: Checkable<Dish>) {
        value = data
        val dish = data.value

        bindTitle(dish.name)
        bindPrice(dish.price)
        bindCheckBox(data.isChecked)
        bindImage(dish.image)
    }

    private fun bindTitle(title: String) {
        if (title != binding.title.text) {
            binding.title.text = title
        }
    }

    private fun bindPrice(price: Int) {
        val priceText = itemView.context.getString(R.string.dollar, price)
        if (priceText != binding.price.text) {
            binding.price.text = priceText
        }
    }

    private fun bindImage(url: String) {
        binding.dishPhoto.load(url)
    }

    fun bindCheckBox(isChecked: Boolean) {
        binding.checkbox.isChecked = isChecked
    }

}