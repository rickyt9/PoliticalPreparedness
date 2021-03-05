package com.example.android.politicalpreparedness.election

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.database.ElectionDatabase

// Create Factory to generate ElectionViewModel with provided election datasource
class ElectionsViewModelFactory(
        private val database: ElectionDatabase) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ElectionsViewModel::class.java)) {
            return ElectionsViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
