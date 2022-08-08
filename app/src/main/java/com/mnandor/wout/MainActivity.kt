package com.mnandor.wout

import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.mnandor.wout.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setClickListeners()
    }

    private fun setClickListeners(){
        binding.addButton.setOnClickListener {
            addExerciseLog()
        }

        binding.addButton.setOnLongClickListener {
            openConfigActivity()
            return@setOnLongClickListener true // yes, consume event
        }
    }

    private fun addExerciseLog(){

    }

    private fun openConfigActivity(){
        val intent = Intent(this, ConfigActivity::class.java)
        startActivity(intent)
    }
}