package com.rebottle.app.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
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
internal val DarkGreen = Color(0xFF465F32)
internal val LightGreen = Color(0xFFF0F5E8)
val MainText = Color(0xFF45463F)
val SecondaryText = Color(0xFF77786F)
private val BorderColor = Color(0xFFD9DDD2)


// ============================================================
// ONBOARDING GENERAL (usado por las 3 pantallas)
// ============================================================

@Composable
internal fun OnboardingScreen(
    @DrawableRes imageRes: Int,
    title: String,
    description: String,
    selectedPage: Int,
    buttonText: String,
    onButtonClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OnboardingImage(imageRes = imageRes)

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = title,
            color = DarkGreen,
            fontSize = 25.sp,
            lineHeight = 26.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = description,
            modifier = Modifier.padding(horizontal = 34.dp),
            color = SecondaryText,
            fontSize = 15.sp,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(26.dp))

        Button(
            onClick = onButtonClick,
            modifier = Modifier
                .padding(horizontal = 55.dp)
                .fillMaxWidth()
                .height(47.dp),
            shape = RoundedCornerShape(9.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MainGreen)
        ) {
            Text(
                text = buttonText,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        PageIndicator(selectedPage = selectedPage)
    }
}


// ============================================================
// IMAGEN DEL ONBOARDING
// ============================================================

@Composable
internal fun OnboardingImage(@DrawableRes imageRes: Int) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(440.dp)
            .clipToBounds(),
        contentAlignment = Alignment.TopCenter
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Ilustración de ReBottle",
            modifier = Modifier
                .requiredSize(565.dp)
                .offset(y = (25).dp),
            contentScale = ContentScale.Fit
        )
    }
}


// ============================================================
// INDICADOR DE PÁGINA
// ============================================================

@Composable
internal fun PageIndicator(selectedPage: Int) {

    Row(
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .size(if (selectedPage == index) 8.dp else 6.dp)
                    .background(
                        color = if (selectedPage == index) Color(0xFF8FAE57) else Color(0xFFC7DBA7),
                        shape = CircleShape
                    )
            )
        }
    }
}


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
            Text(text = placeholder, color = Color(0xFF999B94), fontSize = 14.sp)
        },
        supportingText = {
            if (isError) {
                Text(text = errorMessage, color = Color.Red)
            }
        },
        visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None,
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
        modifier = modifier.height(43.dp),
        shape = RoundedCornerShape(4.dp),
        contentPadding = PaddingValues(horizontal = 2.dp, vertical = 1.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) MainGreen else Color(0xFFBBC5AE)
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selected) LightGreen else Color.White
        )
    ) {
        Text(
            text = text,
            color = MainGreen,
            fontSize = 14.sp,
            lineHeight = 10.sp,
            textAlign = TextAlign.Center,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}


// ============================================================
// LOGO
// ============================================================

@Composable
fun ReBottleLogo() {

    Image(
        painter = painterResource(id = R.drawable.rebottle),
        contentDescription = "Logo ReBottle",
        modifier = Modifier
            .requiredWidth(480.dp)
            .height(180.dp),
        contentScale = ContentScale.Fit
    )
}


// ============================================================
// DECORACIÓN REGISTRO TEMPORAL
// ============================================================

@Composable
fun RegisterDecoration() {

    Image(
        painter = painterResource(id = R.drawable.register_bottom),
        contentDescription = null,
        modifier = Modifier
            .requiredWidth(450.dp)
            .height(230.dp),
        contentScale = ContentScale.FillWidth
    )
}


// ============================================================
// IMAGEN LOGIN
// ============================================================

@Composable
internal fun LoginDecoration() {

    Image(
        painter = painterResource(id = R.drawable.login_bottom),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp),
        contentScale = ContentScale.FillWidth
    )
}