package com.kalugin1912.dishes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kalugin1912.dishes.dishes.DishesFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.main_container, DishesFragment.newInstance())
                .commit()
        }
    }
}