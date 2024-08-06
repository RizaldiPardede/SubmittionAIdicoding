package com.example.submissionpertamaai.DataStore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    companion object {
        @Volatile
        private var INSTANCE: UserPreferences? = null

        private val NAME_KEY = stringPreferencesKey("name")
        private val USERID_KEY = stringPreferencesKey("email")
        private val TOKEN_KEY = stringPreferencesKey("password")
        private val STATE_KEY = booleanPreferencesKey("state")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
    fun getUser(): Flow<UserViewModel> {
        return dataStore.data.map { preferences ->
            UserViewModel(
                preferences[NAME_KEY] ?:"",
                preferences[USERID_KEY] ?:"",
                preferences[TOKEN_KEY] ?:"",

            )
        }
    }

    suspend fun saveUser(user: UserViewModel) {
        dataStore.edit { preferences ->
            preferences[NAME_KEY] = user.name
            preferences[USERID_KEY] = user.userId
            preferences[TOKEN_KEY] = user.token

        }
    }

    suspend fun ClearUser() {
        dataStore.edit { preferences ->
            preferences[NAME_KEY] = ""
            preferences[USERID_KEY] = ""
            preferences[TOKEN_KEY] = ""

        }
    }
}