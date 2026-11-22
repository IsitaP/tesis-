package com.rebottle.app.ui.screens.usuario

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
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
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
private val SecondaryText = RebottleColors.SecondaryText

@Composable
fun EditarPerfilUsuarioScreen(
    controller: NavController
) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var guardando by remember { mutableStateOf(false) }
    var telefonoError by remember { mutableStateOf("") }

    LaunchedEffect(uid) {
        if (uid != null) {
            Firebase.firestore.collection("users").document(uid).get()
                .addOnSuccessListener { doc ->
                    telefono = doc.getString("telefono") ?: ""
                    direccion = doc.getString("direccion") ?: ""
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

        // ====================================================
        // NÚMERO DE CELULAR (opcional)
        // ====================================================

        Text(text = "Número de celular (opcional)", fontSize = 14.sp, color = MainText)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = telefono,
            onValueChange = { nuevo ->
                // Solo números, espacios y el + del indicativo
                telefono = nuevo.filter { it.isDigit() || it == '+' || it == ' ' }.take(16)
                telefonoError = ""
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Ej: 311 222 9987") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Phone,
                    contentDescription = null,
                    tint = SecondaryText
                )
            },
            singleLine = true,
            isError = telefonoError.isNotEmpty(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MainGreen,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        if (telefonoError.isNotEmpty()) {
            // Mismo estilo de error que en el formulario de solicitudes
            Text(
                text = telefonoError,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        } else {
            Text(
                text = "Así el recolector podrá contactarte el día de tu recolección.",
                color = SecondaryText,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(text = "Dirección", fontSize = 14.sp, color = MainText)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = direccion,
            onValueChange = { direccion = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Ej: Cl 131a # 53-31, Bogotá") },
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
                val celular = normalizarCelular(telefono)

                // Vacío se permite (es opcional); si escribió algo, debe ser válido
                if (telefono.isNotBlank() && celular == null) {
                    telefonoError = "Ingresa un celular válido de 10 dígitos (Ej: 311 222 9987)"
                    return@Button
                }

                if (uid != null) {
                    guardando = true
                    Firebase.firestore.collection("users").document(uid)
                        .update(mapOf("telefono" to (celular ?: ""), "direccion" to direccion.trim()))
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


/**
 * Celular colombiano: 10 dígitos que empiezan por 3, con o sin +57.
 * Devuelve "+57 3XXXXXXXXX" o null si no es válido.
 */
internal fun normalizarCelular(texto: String): String? {
    var digitos = texto.filter { it.isDigit() }

    if (digitos.length == 12 && digitos.startsWith("57")) {
        digitos = digitos.drop(2)
    }

    return if (digitos.length == 10 && digitos.startsWith("3")) "+57 $digitos" else null
}

