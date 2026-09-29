package com.rebottle.app.ui.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rebottle.app.R


// ============================================================
// COLORES
// ============================================================

val BackgroundColor = Color(0xFFFAF7EF)

val MainGreen = Color(0xFF5E7D3F)

private val LightGreen = Color(0xFFF0F5E8)

val MainText = Color(0xFF45463F)

val SecondaryText = Color(0xFF77786F)

private val BorderColor = Color(0xFFD9DDD2)


// ============================================================
// CAMPOS DE TEXTO
// ============================================================

@Composable
fun ReBottleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    password: Boolean = false,
    isError: Boolean = false,
    errorMessage: String = ""
) {

    OutlinedTextField(
        value = value,

        onValueChange = onValueChange,

        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),

        singleLine = true,

        isError = isError,

        placeholder = {

            Text(
                text = placeholder,
                color = Color(0xFF999B94),
                fontSize = 14.sp
            )
        },

        supportingText = {

            if (isError) {

                Text(
                    text = errorMessage,
                    color = Color.Red
                )
            }
        },

        visualTransformation = if (password) {

            PasswordVisualTransformation()

        } else {

            VisualTransformation.None
        },

        shape = RoundedCornerShape(4.dp),

        colors = OutlinedTextFieldDefaults.colors(

            focusedBorderColor = MainGreen,

            unfocusedBorderColor = BorderColor,

            focusedContainerColor = Color.White,

            unfocusedContainerColor = Color.White,

            cursorColor = MainGreen,

            focusedTextColor = MainText,

            unfocusedTextColor = MainText
        )
    )
}


// ============================================================
// BOTONES DE TIPO DE USUARIO
// ============================================================

@Composable
fun UserTypeButton(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    OutlinedButton(
        onClick = onClick,

        modifier = modifier
            .height(43.dp),

        shape = RoundedCornerShape(4.dp),

        contentPadding = PaddingValues(
            horizontal = 2.dp,
            vertical = 1.dp
        ),

        border = BorderStroke(
            width = 1.dp,

            color = if (selected) {

                MainGreen

            } else {

                Color(0xFFBBC5AE)
            }
        ),

        colors = ButtonDefaults.outlinedButtonColors(

            containerColor = if (selected) {

                LightGreen

            } else {

                Color.White
            }
        )
    ) {

        Text(
            text = text,

            color = MainGreen,

            fontSize = 14.sp,

            lineHeight = 10.sp,

            textAlign = TextAlign.Center,

            fontWeight = if (selected) {

                FontWeight.SemiBold

            } else {

                FontWeight.Normal
            }
        )
    }
}


// ============================================================
// LOGO
// ============================================================

@Composable
fun ReBottleLogo() {

    Image(
        painter = painterResource(
            id = R.drawable.rebottle
        ),

        contentDescription = "Logo ReBottle",

        modifier = Modifier
            .requiredWidth(480.dp)
            .height(180.dp),

        contentScale = ContentScale.Fit
    )
}


// ============================================================
// DECORACIÓN REGISTRO
// ============================================================

@Composable
fun RegisterDecoration() {

    Image(
        painter = painterResource(
            id = R.drawable.register_bottom
        ),

        contentDescription = null,

        modifier = Modifier
            .requiredWidth(450.dp)
            .height(230.dp),

        contentScale = ContentScale.FillWidth
    )
}