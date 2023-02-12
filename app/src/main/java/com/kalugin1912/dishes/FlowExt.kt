package com.kalugin1912.dishes

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Collect data when UI on is Visible
 * https://developer.android.com/topic/libraries/architecture/coroutines#restart
 */
fun <T> Flow<T>.collectWhenUIVisible(
    owner: LifecycleOwner,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    block: (suspend (T) -> Unit),
) = owner.lifecycleScope.launch(
    block = {
        owner.repeatOnLifecycle(lifecycleState) {
            collect(block)
        }
    },
)