package com.rebottle.app.ui.screens.fundacion

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.rebottle.app.ui.theme.RebottleColors

private val BackgroundColor = RebottleColors.Background
private val MainGreen = RebottleColors.MainGreen
private val MainText = RebottleColors.MainText

@Composable
fun EditarPerfilFundacionScreen(
    controller: NavController
) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    var telefono by remember { mutableStateOf("") }
    var correoContacto by remember { mutableStateOf("") }
    var guardando by remember { mutableStateOf(false) }

    LaunchedEffect(uid) {
        if (uid != null) {
            Firebase.firestore.collection("users").document(uid).get()
                .addOnSuccessListener { doc ->
                    telefono = doc.getString("telefono") ?: ""
                    correoContacto = doc.getString("correoContacto") ?: (doc.getString("email") ?: "")
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(start = 18.dp, end = 18.dp, top = 48.dp, bottom = 20.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = MainText,
                modifier = Modifier.size(24.dp).clickable { controller.popBackStack() }
            )
            Text(
                text = "  Editar perfil",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MainText
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(text = "Número de contacto", fontSize = 14.sp, color = MainText)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Ej: +57 3112229987") },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MainGreen,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(text = "Correo de contacto", fontSize = 14.sp, color = MainText)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = correoContacto,
            onValueChange = { correoContacto = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Ej: contacto@ecoalianza.org") },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MainGreen,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (uid != null) {
                    guardando = true
                    Firebase.firestore.collection("users").document(uid)
                        .update(mapOf("telefono" to telefono, "correoContacto" to correoContacto))
                        .addOnCompleteListener {
                            guardando = false
                            controller.popBackStack()
                        }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MainGreen),
            enabled = !guardando
        ) {
            Text(text = if (guardando) "Guardando..." else "Guardar cambios", color = Color.White, fontSize = 16.sp)
        }
    }
}

