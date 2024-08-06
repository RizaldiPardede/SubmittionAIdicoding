package com.example.submissionpertamaai

import androidx.lifecycle.*
import com.example.submissionpertamaai.DataStore.UserPreferences
import com.example.submissionpertamaai.DataStore.UserViewModel
import kotlinx.coroutines.launch

class MainViewModel(private val pref: UserPreferences) : ViewModel() {
    fun getuser(): LiveData<UserViewModel> {
        return pref.getUser().asLiveData()
    }
    fun saveuser(user: UserViewModel) {
        viewModelScope.launch {
            pref.saveUser(user)
        }
    }
    fun logout() {
        viewModelScope.launch {
            pref.ClearUser()
        }
    }

}