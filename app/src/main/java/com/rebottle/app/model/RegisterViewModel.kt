package com.rebottle.app.model



import androidx.lifecycle.ViewModel
import com.rebottle.app.ui.screens.auth.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class RegisterState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val role: UserRole? = null,
    val nameError: String = "",
    val emailError: String = "",
    val passError: String = "",
    val roleError: String = "",
    val isLoading: Boolean = false
)

class RegisterViewModel : ViewModel() {
    private val _user = MutableStateFlow(RegisterState())
    val user: StateFlow<RegisterState> = _user.asStateFlow()

    fun updateName(newName: String) {
        _user.value = _user.value.copy(name = newName)
    }

    fun updateEmail(newEmail: String) {
        _user.value = _user.value.copy(email = newEmail)
    }

    fun updatePassword(newPass: String) {
        _user.value = _user.value.copy(password = newPass)
    }

    fun updateRole(newRole: UserRole) {
        _user.value = _user.value.copy(role = newRole)
    }

    fun updateNameError(error: String) {
        _user.value = _user.value.copy(nameError = error)
    }

    fun updateEmailError(error: String) {
        _user.value = _user.value.copy(emailError = error)
    }

    fun updatePassError(error: String) {
        _user.value = _user.value.copy(passError = error)
    }

    fun updateRoleError(error: String) {
        _user.value = _user.value.copy(roleError = error)
    }

    fun updateLoading(loading: Boolean) {
        _user.value = _user.value.copy(isLoading = loading)
    }
}