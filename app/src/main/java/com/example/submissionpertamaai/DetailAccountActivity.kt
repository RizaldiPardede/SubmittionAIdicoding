package com.example.submissionpertamaai

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.submissionpertamaai.DataStore.UserPreferences
import com.example.submissionpertamaai.DataStore.ViewModelFactory
import com.example.submissionpertamaai.databinding.ActivityDetailAccountBinding

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
class DetailAccountActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityDetailAccountBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferences.getInstance(dataStore))
        )[MainViewModel::class.java]

        viewModel.getuser().observe(this){
            binding.DetailUsername.text = it.name.toString()
            binding.userId.text = it.userId.toString()
        }

    }
}