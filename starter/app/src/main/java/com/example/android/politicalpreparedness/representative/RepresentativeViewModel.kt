package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.models.Address
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch

class RepresentativeViewModel : ViewModel() {

    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives

    private val _address = MutableLiveData<Address>()
    val address: LiveData<Address>
        get() = _address

    var _line1 = MutableLiveData<String>()
    val line1: LiveData<String>
        get() = _line1
    var _line2 = MutableLiveData<String>()
    val line2: LiveData<String>
        get() = _line2
    var _city = MutableLiveData<String>()
    val city: LiveData<String>
        get() = _city
    var _state = MutableLiveData<String>()
    val state: LiveData<String>
        get() = _state
    var _zip = MutableLiveData<String>()
    val zip: LiveData<String>
        get() = _zip

    fun getRepresentatives(address: String) {
        try {
            viewModelScope.launch {
                val (offices, officials) = CivicsApi.retrofitService.getRepresentatives(address)
                _representatives.value = offices.flatMap { office ->
                    office.getRepresentatives(officials)
                }
            }
        } catch (e: Exception) {

        }
    }

    fun getAddressFromLocation(location: Address) {
        _line1.value = location.line1
        _line2.value = location.line2
        _city.value = location.city
        _state.value = location.state
        _zip.value = location.zip
        _address.value = location
    }

    fun getAddressFromLines(state: String) {
        _address.value = Address(
            line1.value ?: EMPTY_STRING, line2.value ?: EMPTY_STRING,
            city.value ?: EMPTY_STRING, state, zip.value ?: EMPTY_STRING
        )
    }

    companion object {
        const val EMPTY_STRING = ""
    }
}
