package com.example.studybuddy.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.studybuddy.R
import com.example.studybuddy.fragments.PlanListFragment


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, PlanListFragment())
                .commit()
        }
    }
}
