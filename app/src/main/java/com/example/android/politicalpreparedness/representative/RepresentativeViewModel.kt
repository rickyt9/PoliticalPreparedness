package com.example.android.politicalpreparedness.representative

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch

class RepresentativeViewModel: ViewModel() {

    private val TAG = RepresentativeViewModel::class.java.simpleName

    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives : LiveData<List<Representative>>
        get() = _representatives

    private val address = MutableLiveData<Address>()

    fun getRepresentatives(currentAddress: Address) {
        address.value = currentAddress
        viewModelScope.launch {
            try {
                val addressStr = "${address.value!!.state},${address.value!!.city}"
                val (offices, officials) = CivicsApi.retrofitService.getRepresentatives(addressStr)
                _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }
            } catch (e: Exception) {
                Log.i(TAG, "Query exception: ${e.message}")
                e.printStackTrace()
            }
        }
    }

}
