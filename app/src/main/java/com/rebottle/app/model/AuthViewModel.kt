package com.rebottle.app.model

import androidx.lifecycle.ViewModel
import com.rebottle.app.ui.screens.auth.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// ============================================================
// MODELO DE USUARIO
// ============================================================

data class Usuario(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val estado: String = "",
    val fotoUrl: String = ""
)




// ============================================================
// ESTADO DEL REGISTRO
// ============================================================

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


// ============================================================
// ESTADO DEL LOGIN
// ============================================================

data class LoginState(

    val email: String = "",

    val password: String = "",

    val emailError: String = "",

    val passError: String = "",

    val isLoading: Boolean = false
)


// ============================================================
// ESTADO GENERAL DE AUTENTICACIÓN
// ============================================================

data class AuthState(

    val register: RegisterState =
        RegisterState(),

    val login: LoginState =
        LoginState()
)


// ============================================================
// AUTH VIEWMODEL
// ============================================================

class AuthViewModel : ViewModel() {


    // ========================================================
    // STATE
    // ========================================================

    private val _state =
        MutableStateFlow(
            AuthState()
        )

    val state: StateFlow<AuthState> =
        _state.asStateFlow()


    // ========================================================
    // REGISTRO
    // ========================================================


    // --------------------------------------------------------
    // NOMBRE
    // --------------------------------------------------------

    fun updateRegisterName(
        newName: String
    ) {

        _state.value =
            _state.value.copy(

                register =
                    _state.value.register.copy(

                        name = newName,

                        nameError = ""
                    )
            )
    }


    // --------------------------------------------------------
    // EMAIL
    // --------------------------------------------------------

    fun updateRegisterEmail(
        newEmail: String
    ) {

        _state.value =
            _state.value.copy(

                register =
                    _state.value.register.copy(

                        email = newEmail,

                        emailError = ""
                    )
            )
    }


    // --------------------------------------------------------
    // PASSWORD
    // --------------------------------------------------------

    fun updateRegisterPassword(
        newPassword: String
    ) {

        _state.value =
            _state.value.copy(

                register =
                    _state.value.register.copy(

                        password = newPassword,

                        passError = ""
                    )
            )
    }


    // --------------------------------------------------------
    // ROL
    // --------------------------------------------------------

    fun updateRegisterRole(
        newRole: UserRole
    ) {

        _state.value =
            _state.value.copy(

                register =
                    _state.value.register.copy(

                        role = newRole,

                        roleError = ""
                    )
            )
    }


    // --------------------------------------------------------
    // ERROR NOMBRE
    // --------------------------------------------------------

    fun updateRegisterNameError(
        error: String
    ) {

        _state.value =
            _state.value.copy(

                register =
                    _state.value.register.copy(

                        nameError = error
                    )
            )
    }


    // --------------------------------------------------------
    // ERROR EMAIL
    // --------------------------------------------------------

    fun updateRegisterEmailError(
        error: String
    ) {

        _state.value =
            _state.value.copy(

                register =
                    _state.value.register.copy(

                        emailError = error
                    )
            )
    }


    // --------------------------------------------------------
    // ERROR PASSWORD
    // --------------------------------------------------------

    fun updateRegisterPassError(
        error: String
    ) {

        _state.value =
            _state.value.copy(

                register =
                    _state.value.register.copy(

                        passError = error
                    )
            )
    }


    // --------------------------------------------------------
    // ERROR ROL
    // --------------------------------------------------------

    fun updateRegisterRoleError(
        error: String
    ) {

        _state.value =
            _state.value.copy(

                register =
                    _state.value.register.copy(

                        roleError = error
                    )
            )
    }


    // --------------------------------------------------------
    // LOADING REGISTRO
    // --------------------------------------------------------

    fun updateRegisterLoading(
        loading: Boolean
    ) {

        _state.value =
            _state.value.copy(

                register =
                    _state.value.register.copy(

                        isLoading = loading
                    )
            )
    }


    // ========================================================
    // LOGIN
    // ========================================================


    // --------------------------------------------------------
    // EMAIL
    // --------------------------------------------------------

    fun updateLoginEmail(
        newEmail: String
    ) {

        _state.value =
            _state.value.copy(

                login =
                    _state.value.login.copy(

                        email = newEmail,

                        emailError = ""
                    )
            )
    }


    // --------------------------------------------------------
    // PASSWORD
    // --------------------------------------------------------

    fun updateLoginPassword(
        newPassword: String
    ) {

        _state.value =
            _state.value.copy(

                login =
                    _state.value.login.copy(

                        password = newPassword,

                        passError = ""
                    )
            )
    }


    // --------------------------------------------------------
    // ERROR EMAIL
    // --------------------------------------------------------

    fun updateLoginEmailError(
        error: String
    ) {

        _state.value =
            _state.value.copy(

                login =
                    _state.value.login.copy(

                        emailError = error
                    )
            )
    }


    // --------------------------------------------------------
    // ERROR PASSWORD
    // --------------------------------------------------------

    fun updateLoginPassError(
        error: String
    ) {

        _state.value =
            _state.value.copy(

                login =
                    _state.value.login.copy(

                        passError = error
                    )
            )
    }


    // --------------------------------------------------------
    // LOADING LOGIN
    // --------------------------------------------------------

    fun updateLoginLoading(
        loading: Boolean
    ) {

        _state.value =
            _state.value.copy(

                login =
                    _state.value.login.copy(

                        isLoading = loading
                    )
            )
    }


    // ========================================================
    // LIMPIAR FORMULARIO DE REGISTRO
    // ========================================================

    fun resetRegister() {

        _state.value =
            _state.value.copy(

                register =
                    RegisterState()
            )
    }


    // ========================================================
    // LIMPIAR FORMULARIO DE LOGIN
    // ========================================================

    fun resetLogin() {

        _state.value =
            _state.value.copy(

                login =
                    LoginState()
            )
    }
}