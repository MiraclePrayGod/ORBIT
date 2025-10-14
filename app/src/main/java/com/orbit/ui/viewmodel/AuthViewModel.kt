package com.orbit.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbit.data.auth.MicrosoftAuthRepository
import com.orbit.data.auth.User
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val microsoftAuthRepository: MicrosoftAuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    init {
        checkCurrentUser()
    }
    
    private fun checkCurrentUser() {
        val user = microsoftAuthRepository.getCurrentUser()
        if (user != null) {
            _uiState.value = _uiState.value.copy(
                isAuthenticated = true,
                user = user
            )
        }
    }
    
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            microsoftAuthRepository.authenticateWithMicrosoft(email, password)
                .onSuccess { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        user = user
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error desconocido"
                    )
                }
        }
    }
    
    fun signOut() {
        microsoftAuthRepository.signOut()
        _uiState.value = AuthUiState()
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val user: User? = null,
    val error: String? = null
)
