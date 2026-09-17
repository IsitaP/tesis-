package com.rebottle.app.ui.screens.auth

import android.content.Context
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
import com.google.firebase.auth.FirebaseAuth
import com.rebottle.app.R
import com.rebottle.app.model.UserAuthViewModel
import com.rebottle.app.navigation.AppScreens

val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
@Composable
public fun LoginScreen(controller: NavController, model: UserAuthViewModel = viewModel()) {

    val context = LocalContext.current
    val user by model.user.collectAsState()

    LaunchedEffect(Unit) {
        firebaseAuth.currentUser?.let{
            controller.navigate(AppScreens.homeFundacion.name)
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

            // LOGO
            ReBottleLogo()


            Spacer(
                modifier = Modifier.height(30.dp)
            )


            // TÍTULO
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


            // EMAIL
            ReBottleTextField(
                value = user.email,

                onValueChange = {
                    model.updateEmailClass(it)
                },
                placeholder = "Email",
                isError = user.emailError.isNotEmpty(),
                errorMessage = user.emailError
            )


            Spacer(
                modifier = Modifier.height(12.dp)
            )


            // CONTRASEÑA
            ReBottleTextField(
                value = user.password,

                onValueChange = {
                    model.updatePassClass(it)
                },

                placeholder = "Contraseña",
                password = true,
                isError = user.password.isNotEmpty(),
                errorMessage = user.passError
            )


            Spacer(
                modifier = Modifier.height(25.dp)
            )


            // BOTÓN
            Button(
                onClick = {
                    login(model,user.email, user.password, controller, context)
                },
                enabled = !user.isLoading,

                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),

                shape = RoundedCornerShape(6.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = MainGreen
                )
            ) {
                if(user.isLoading){
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                }
                else {
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


            // REGISTRARSE
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

fun validateForm(model: UserAuthViewModel,email:String, password:String):Boolean{
    if (email.isEmpty()){ model.updateEmailError("Email is empty")
        return false
    }else{model.updateEmailError("")}
    if(!validEmailAddress(email)){model.updateEmailError("Not a valid address")
        return false
    }else{model.updateEmailError("")}
    if(password.isEmpty()) {model.updatePassError("Password is empty")
        return false
    }else{model.updatePassError("")}
    if(password.length < 6) {model.updatePassError("Password is too short")
        return false
    }else{model.updatePassError("")}
    return true
}
fun validEmailAddress(email:String):Boolean{
    val regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
    return email.matches(regex.toRegex())
}


fun login(model: UserAuthViewModel, email: String, password: String, controller: NavController, context: Context) {
    if (validateForm(model, email, password)) {
        model.updateLoading(true)
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            model.updateLoading(false)
            if (it.isSuccessful) {
                controller.navigate(AppScreens.homeFundacion.name)
            } else {
                Toast.makeText(context, "Login error ${it.exception.toString()}", Toast.LENGTH_LONG).show()
            }
        }
    }
}


