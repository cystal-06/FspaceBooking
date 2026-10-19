package com.example.fspacebooking.ui.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fspacebooking.domain.model.User
import com.example.fspacebooking.domain.repository.AuthRepository
import com.example.fspacebooking.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _username = mutableStateOf("")
    val username: State<String> = _username

    private val _password = mutableStateOf("")
    val password: State<String> = _password

    private val _loginState = mutableStateOf<Resource<User>?>(null)
    val loginState: State<Resource<User>?> = _loginState

    val currentUser: StateFlow<User?> = authRepository.getSessionUser()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun onUsernameChange(value: String) {
        _username.value = value
    }

    fun onPasswordChange(value: String) {
        _password.value = value
    }

    fun login(onSuccess: (User) -> Unit) {
        viewModelScope.launch {
            _loginState.value = Resource.Loading()
            val result = authRepository.login(_username.value, _password.value)
            _loginState.value = result
            if (result is Resource.Success && result.data != null) {
                onSuccess(result.data)
            }
        }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            _username.value = ""
            _password.value = ""
            _loginState.value = null
            onComplete()
        }
    }
}
