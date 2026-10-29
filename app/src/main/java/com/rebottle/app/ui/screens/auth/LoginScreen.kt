package com.rebottle.app.ui.screens.auth

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessaging
import com.rebottle.app.R
import com.rebottle.app.model.AuthViewModel
import com.rebottle.app.navigation.AppScreens

val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
@Composable
public fun LoginScreen(controller: NavController, model: AuthViewModel = viewModel()) {

    val context = LocalContext.current
    val authState by model.state.collectAsState()
    val user = authState.login

    LaunchedEffect(Unit) {
        firebaseAuth.currentUser?.let {
            navigateByRole(it.uid, controller, context)
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {

        // ====================================================
        // IMAGEN DEL PLANETA - SIEMPRE ABAJO
        // ====================================================

        Image(
            painter = painterResource(
                id = R.drawable.login_bottom
            ),
            contentDescription = null,

            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(280.dp),

            contentScale = ContentScale.FillWidth
        )


        // ====================================================
        // FORMULARIO
        // ====================================================

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 25.dp,
                    end = 25.dp,
                    top = 25.dp
                ),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(
                modifier = Modifier.height(45.dp)
            )

            ReBottleLogo()

            Spacer(
                modifier = Modifier.height(30.dp)
            )

            Text(
                text = "Iniciar sesión",
                modifier = Modifier.fillMaxWidth(),
                color = MainText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            ReBottleTextField(
                value = user.email,
                onValueChange = {
                    model.updateLoginEmail(it)
                },
                placeholder = "Email",
                isError = user.emailError.isNotEmpty(),
                errorMessage = user.emailError
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            ReBottleTextField(
                value = user.password,
                onValueChange = {
                    model.updateLoginPassword(it)
                },
                placeholder = "Contraseña",
                password = true,
                isError = user.passError.isNotEmpty(),
                errorMessage = user.passError
            )

            Spacer(
                modifier = Modifier.height(25.dp)
            )

            if (user.isLoading) {
                CircularProgressIndicator(color = MainGreen)
            } else {
                Button(
                    onClick = {
                        login(model, user.email, user.password, controller, context)
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),

                    shape = RoundedCornerShape(6.dp),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainGreen
                    )
                ) {

                    Text(
                        text = "Iniciar sesión",

                        color = Color.White,

                        fontSize = 24.sp
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "¿No tiene una cuenta? ",

                    color = SecondaryText,

                    fontSize = 15.sp
                )


                Text(
                    text = "Registrarse",

                    modifier = Modifier.clickable {
                        controller.navigate(AppScreens.register.name)
                    },

                    color = MainGreen,

                    fontSize = 15.sp,

                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

fun validateForm(model: AuthViewModel,email:String, password:String):Boolean{
    if (email.isEmpty()){ model.updateLoginEmailError("Email is empty")
        return false
    }else{model.updateLoginEmailError("")}
    if(!validEmailAddress(email)){model.updateLoginEmailError("Not a valid address")
        return false
    }else{model.updateLoginEmailError("")}
    if(password.isEmpty()) {model.updateLoginPassError("Password is empty")
        return false
    }else{model.updateLoginPassError("")}
    if(password.length < 6) {model.updateLoginPassError("Password is too short")
        return false
    }else{model.updateLoginPassError("")}
    return true
}
fun validEmailAddress(email:String):Boolean{
    val regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
    return email.matches(regex.toRegex())
}



fun login(
    model: AuthViewModel,
    email: String,
    password: String,
    controller: NavController,
    context: Context
) {

    if (validateForm(model, email, password)) {

        model.updateLoginLoading(true)

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { tarea ->

                model.updateLoginLoading(false)

                if (tarea.isSuccessful) {

                    val uid = firebaseAuth.currentUser?.uid

                    if (uid != null) {

                        // REGISTRAR DISPOSITIVO EN FCM
                        FirebaseMessaging.getInstance()
                            .register()
                            .addOnCompleteListener { registro ->

                                if (registro.isSuccessful) {

                                    Log.d(
                                        "ReBottleFCM",
                                        "Registro FCM solicitado correctamente"
                                    )

                                } else {

                                    Log.e(
                                        "ReBottleFCM",
                                        "Error solicitando registro FCM",
                                        registro.exception
                                    )
                                }
                            }

                        // CONTINUAR HACIA LA PANTALLA DEL ROL
                        navigateByRole(
                            uid,
                            controller,
                            context
                        )
                    }

                } else {

                    Toast.makeText(
                        context,
                        "Login error ${tarea.exception}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}


private fun navigateByRole(uid: String, controller: NavController, context: Context) {
    Firebase.firestore.collection("users").document(uid).get()
        .addOnSuccessListener { doc ->
            val roleValue = doc.getString("role")
            val destination = when (roleValue) {
                UserRole.CIUDADANO.firestoreValue -> AppScreens.homeCiudadano.name
                UserRole.RECOLECTOR.firestoreValue -> AppScreens.homeRecolector.name
                else -> AppScreens.homeFundacion.name
            }
            controller.navigate(destination) {
                popUpTo(AppScreens.login.name) { inclusive = true }
            }
        }
        .addOnFailureListener {
            Toast.makeText(context, "Error obteniendo el perfil: ${it.message}", Toast.LENGTH_LONG).show()
        }
}