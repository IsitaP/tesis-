package com.rebottle.app.ui.screens.auth

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
public fun RegisterScreen( navController: NavController) {

    var username by remember {
        mutableStateOf("")
    }

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var selectedUserType by remember {
        mutableStateOf("Ciudadano")
    }


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
            modifier = Modifier.height(45.dp)
        )


        ReBottleLogo()


        Spacer(
            modifier = Modifier.height(30.dp)
        )


        Text(
            text = "Crear cuenta",
            modifier = Modifier.fillMaxWidth(),
            color = MainText,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )


        Spacer(
            modifier = Modifier.height(14.dp)
        )


        ReBottleTextField(
            value = username,
            onValueChange = {
                username = it
            },
            placeholder = "Usuario"
        )


        Spacer(
            modifier = Modifier.height(10.dp)
        )


        ReBottleTextField(
            value = email,
            onValueChange = {
                email = it
            },
            placeholder = "Email"
        )


        Spacer(
            modifier = Modifier.height(10.dp)
        )


        ReBottleTextField(
            value = password,
            onValueChange = {
                password = it
            },
            placeholder = "Contraseña",
            password = true
        )


        Spacer(
            modifier = Modifier.height(17.dp)
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
                selected = selectedUserType == "Ciudadano",
                modifier = Modifier.weight(1f)
            ) {

                selectedUserType = "Ciudadano"
            }


            UserTypeButton(
                text = "EcoAlianza",
                selected = selectedUserType == "Fundación",
                modifier = Modifier.weight(1f)
            ) {

                selectedUserType = "Fundación"
            }


            UserTypeButton(
                text = "Recolector",
                selected = selectedUserType == "Recolector",
                modifier = Modifier.weight(1f)
            ) {

                selectedUserType = "Recolector"
            }
        }


        Spacer(
            modifier = Modifier.height(18.dp)
        )


        Button(
            onClick = {},

            modifier = Modifier
                .fillMaxWidth()
                .height(47.dp),

            shape = RoundedCornerShape(9.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = MainGreen
            )
        ) {

            Text(
                text = "Registrarse",
                color = Color.White,
                fontSize = 24.sp
            )
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

                },

                color = MainGreen,

                fontSize = 15.sp,

                fontWeight = FontWeight.SemiBold
            )
        }


        Spacer(
            modifier = Modifier.height(50.dp)
        )


        RegisterDecoration()
    }
}