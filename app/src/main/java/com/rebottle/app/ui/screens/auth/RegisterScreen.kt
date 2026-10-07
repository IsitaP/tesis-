package com.rebottle.app.ui.screens.auth

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.rebottle.app.model.AuthViewModel
import com.rebottle.app.model.RegisterState
import com.rebottle.app.navigation.AppScreens
import com.rebottle.app.ui.theme.RebottleTheme

@Composable
public fun RegisterScreen(controller: NavController, model: AuthViewModel = viewModel()) {

    val context = LocalContext.current
    val authState by model.state.collectAsState()
    val user = authState.register

    RegisterContent(
        user = user,
        onNameChange = { model.updateRegisterName(it) },
        onEmailChange = { model.updateRegisterEmail(it) },
        onPasswordChange = { model.updateRegisterPassword(it) },
        onRoleChange = { model.updateRegisterRole(it) },
        onRegisterClick = {
            register(model, user.name, user.email, user.password, user.role, controller, context)
        },
        onLoginClick = {
            controller.navigate(AppScreens.login.name)
        }
    )
}

@Composable
fun RegisterContent(
    user: RegisterState,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRoleChange: (UserRole) -> Unit,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .verticalScroll(
                rememberScrollState()
            )
            .padding(
                horizontal = 25.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(
            modifier = Modifier.height(15.dp)
        )


        ReBottleLogo()


        Spacer(
            modifier = Modifier.height(5.dp)
        )


        Text(
            text = "Crear cuenta",
            modifier = Modifier.fillMaxWidth(),
            color = MainText,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )


        Spacer(
            modifier = Modifier.height(12.dp)
        )


        ReBottleTextField(
            value = user.name,
            onValueChange = onNameChange,
            placeholder = "Usuario",
            isError = user.nameError.isNotEmpty(),
            errorMessage = user.nameError
        )


        Spacer(
            modifier = Modifier.height(5.dp)
        )


        ReBottleTextField(
            value = user.email,
            onValueChange = onEmailChange,
            placeholder = "Email",
            isError = user.emailError.isNotEmpty(),
            errorMessage = user.emailError
        )


        Spacer(
            modifier = Modifier.height(5.dp)
        )


        ReBottleTextField(
            value = user.password,
            onValueChange = onPasswordChange,
            placeholder = "Contraseña",
            password = true,
            isError = user.passError.isNotEmpty(),
            errorMessage = user.passError
        )


        Spacer(
            modifier = Modifier.height(10.dp)
        )


        Text(
            text = "Tipo de usuario",
            modifier = Modifier.fillMaxWidth(),
            color = MainText,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )


        Spacer(
            modifier = Modifier.height(8.dp)
        )


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {

            UserTypeButton(
                text = "Ciudadano",
                selected = user.role == UserRole.CIUDADANO,
                modifier = Modifier.weight(1f)
            ) {

                onRoleChange(UserRole.CIUDADANO)
            }


            UserTypeButton(
                text = "EcoAlianza",
                selected = user.role == UserRole.FUNDACION,
                modifier = Modifier.weight(1f)
            ) {

                onRoleChange(UserRole.FUNDACION)
            }


            UserTypeButton(
                text = "Recolector",
                selected = user.role == UserRole.RECOLECTOR,
                modifier = Modifier.weight(1f)
            ) {

                onRoleChange(UserRole.RECOLECTOR)
            }
        }


        if (user.roleError.isNotEmpty()) {

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = user.roleError,
                modifier = Modifier.fillMaxWidth(),
                color = Color.Red,
                fontSize = 12.sp
            )
        }


        Spacer(
            modifier = Modifier.height(18.dp)
        )


        Button(
            onClick = onRegisterClick,

            enabled = !user.isLoading,

            modifier = Modifier
                .fillMaxWidth()
                .height(47.dp),

            shape = RoundedCornerShape(9.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = MainGreen
            )
        ) {

            if (user.isLoading) {

                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {

                Text(
                    text = "Registrarse",
                    color = Color.White,
                    fontSize = 24.sp
                )
            }
        }


        Spacer(
            modifier = Modifier.height(15.dp)
        )


        Row {

            Text(
                text = "¿Ya tienes una cuenta? ",
                color = SecondaryText,
                fontSize = 15.sp
            )


            Text(
                text = "Iniciar Sesión",

                modifier = Modifier.clickable {
                    onLoginClick()
                },

                color = MainGreen,

                fontSize = 15.sp,

                fontWeight = FontWeight.SemiBold
            )
        }


        Spacer(
            modifier = Modifier.height(68.dp)
        )


        RegisterDecoration()
    }
}

fun validateRegisterForm(model: AuthViewModel, name: String, email: String, password: String, role: UserRole?): Boolean {
    if (name.isEmpty()) { model.updateRegisterNameError("El nombre es obligatorio"); return false }
    else { model.updateRegisterNameError("") }

    if (email.isEmpty()) { model.updateRegisterEmailError("El email es obligatorio"); return false }
    else { model.updateRegisterEmailError("") }

    if (!validEmailAddress(email)) { model.updateRegisterEmailError("Email no válido"); return false }
    else { model.updateRegisterEmailError("") }

    if (password.isEmpty()) { model.updateRegisterPassError("La contraseña es obligatoria"); return false }
    else { model.updateRegisterPassError("") }

    if (password.length < 6) { model.updateRegisterPassError("Mínimo 6 caracteres"); return false }
    else { model.updateRegisterPassError("") }

    if (role == null) { model.updateRegisterRoleError("Selecciona un tipo de usuario"); return false }
    else { model.updateRegisterRoleError("") }

    return true
}

fun register(
    model: AuthViewModel,
    name: String,
    email: String,
    password: String,
    role: UserRole?,
    controller: NavController,
    context: Context
) {

    if (
        !validateRegisterForm(
            model,
            name,
            email,
            password,
            role
        )
    ) {
        return
    }


    model.updateRegisterLoading(true)


    firebaseAuth
        .createUserWithEmailAndPassword(
            email,
            password
        )
        .addOnCompleteListener { task ->


            if (task.isSuccessful) {

                val uid =
                    firebaseAuth
                        .currentUser
                        ?.uid


                if (uid != null) {


                    // =========================================
                    // DATOS GENERALES DEL USUARIO
                    // =========================================

                    val userData =
                        hashMapOf<String, Any>(

                            "name" to
                                    name,

                            "email" to
                                    email,

                            "role" to
                                    role!!.firestoreValue,

                            "telefono" to
                                    "",

                            "direccion" to
                                    ""
                        )


                    // =========================================
                    // ESTADO DEL RECOLECTOR
                    // =========================================

                    if (
                        role ==
                        UserRole.RECOLECTOR
                    ) {

                        userData["estado"] =
                            "DISPONIBLE"
                    }


                    // =========================================
                    // GUARDAR EN FIRESTORE
                    // =========================================

                    Firebase
                        .firestore
                        .collection(
                            "users"
                        )
                        .document(
                            uid
                        )
                        .set(
                            userData
                        )
                        .addOnSuccessListener {


                            model
                                .updateRegisterLoading(
                                    false
                                )


                            val destination =
                                when (role) {

                                    UserRole.CIUDADANO ->
                                        AppScreens
                                            .homeCiudadano
                                            .name

                                    UserRole.FUNDACION ->
                                        AppScreens
                                            .homeFundacion
                                            .name

                                    UserRole.RECOLECTOR ->
                                        AppScreens
                                            .homeRecolector
                                            .name
                                }


                            controller.navigate(
                                destination
                            ) {

                                popUpTo(
                                    AppScreens
                                        .onboardingOne
                                        .name
                                ) {
                                    inclusive =
                                        true
                                }
                            }
                        }
                        .addOnFailureListener {

                            model
                                .updateRegisterLoading(
                                    false
                                )


                            Toast
                                .makeText(
                                    context,
                                    "Error guardando el perfil: ${it.message}",
                                    Toast.LENGTH_LONG
                                )
                                .show()
                        }
                }

            } else {

                model
                    .updateRegisterLoading(
                        false
                    )


                Toast
                    .makeText(
                        context,
                        "Error en el registro: ${task.exception}",
                        Toast.LENGTH_LONG
                    )
                    .show()
            }
        }
}

@Preview(showBackground = true)
@Composable
private fun RegisterScreenPreview() {
    RebottleTheme {
        RegisterContent(
            user = RegisterState(
                name = "Juan Pérez",
                email = "juan@example.com",
                password = "password123",
                role = UserRole.CIUDADANO
            ),
            onNameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onRoleChange = {},
            onRegisterClick = {},
            onLoginClick = {}
        )
    }
}