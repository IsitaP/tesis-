package com.rebottle.app.integracion

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuth
import com.rebottle.app.navigation.AppScreens
import com.rebottle.app.ui.screens.auth.LoginScreen
import com.rebottle.app.ui.screens.auth.UserRole
import org.junit.After
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Pruebas de integración del inicio de sesión:
 * LoginScreen (Compose) + Firebase Auth + documento `users/{uid}`
 * en Firestore + navegación según el rol.
 *
 * Las pantallas de inicio se reemplazan por textos simples para
 * comprobar a dónde navegó la app sin cargar mapas ni listas.
 */
@RunWith(AndroidJUnit4::class)
class LoginIntegracionTest {

    @get:Rule
    val compose = createComposeRule()

    @Before
    fun prepararEmulador() {
        EmuladorFirebase.configurar()
        EmuladorFirebase.limpiarDatos()
    }

    @After
    fun cerrarSesion() {
        FirebaseAuth.getInstance().signOut()
    }

    private fun mostrarLogin() {
        compose.setContent {
            val nav = rememberNavController()
            NavHost(nav, startDestination = AppScreens.login.name) {
                composable(AppScreens.login.name) { LoginScreen(nav) }
                composable(AppScreens.homeCiudadano.name) { Text("INICIO_CIUDADANO") }
                composable(AppScreens.homeRecolector.name) { Text("INICIO_RECOLECTOR") }
                composable(AppScreens.homeFundacion.name) { Text("INICIO_FUNDACION") }
                composable(AppScreens.register.name) { Text("REGISTRO") }
            }
        }
    }

    private fun iniciarSesion(correo: String, contrasena: String) {
        val campos = compose.onAllNodes(hasSetTextAction())
        campos[0].performTextInput(correo)
        campos[1].performTextInput(contrasena)
        compose.onNode(hasText("Iniciar sesión") and hasClickAction()).performClick()
    }

    private fun esperarTexto(texto: String) {
        compose.waitUntil(timeoutMillis = 20_000) {
            compose.onAllNodesWithText(texto).fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText(texto).assertExists()
    }

    private fun crearUsuarioSinSesion(rol: UserRole): UsuarioPrueba {
        val usuario = EmuladorFirebase.crearUsuario(rol.firestoreValue)
        FirebaseAuth.getInstance().signOut()
        return usuario
    }

    @Test
    fun I07_ciudadano_inicia_sesion_y_llega_a_su_inicio() {
        val usuario = crearUsuarioSinSesion(UserRole.CIUDADANO)
        mostrarLogin()

        iniciarSesion(usuario.correo, usuario.contrasena)

        esperarTexto("INICIO_CIUDADANO")
    }

    @Test
    fun I08_recolector_inicia_sesion_y_llega_a_su_inicio() {
        val usuario = crearUsuarioSinSesion(UserRole.RECOLECTOR)
        mostrarLogin()

        iniciarSesion(usuario.correo, usuario.contrasena)

        esperarTexto("INICIO_RECOLECTOR")
    }

    @Test
    fun I09_fundacion_inicia_sesion_y_llega_a_su_inicio() {
        val usuario = crearUsuarioSinSesion(UserRole.FUNDACION)
        mostrarLogin()

        iniciarSesion(usuario.correo, usuario.contrasena)

        esperarTexto("INICIO_FUNDACION")
    }

    @Test
    fun I10_contrasena_incorrecta_no_inicia_sesion() {
        val usuario = crearUsuarioSinSesion(UserRole.CIUDADANO)
        mostrarLogin()

        iniciarSesion(usuario.correo, "ContrasenaMala1")

        // Mientras Auth responde se muestra el indicador de carga en
        // lugar del botón; cuando el botón vuelve, la petición terminó.
        compose.waitForIdle()
        compose.waitUntil(timeoutMillis = 20_000) {
            compose.onAllNodes(hasText("Iniciar sesión") and hasClickAction())
                .fetchSemanticsNodes().isNotEmpty()
        }
        compose.waitForIdle()
        assertNull(FirebaseAuth.getInstance().currentUser)
        compose.onAllNodesWithText("INICIO_CIUDADANO").assertCountEquals(0)
    }

    @Test
    fun I11_sesion_ya_iniciada_entra_directo_al_inicio() {
        // crearUsuario deja la sesión abierta, como al reabrir la app.
        EmuladorFirebase.crearUsuario(UserRole.CIUDADANO.firestoreValue)

        mostrarLogin()

        esperarTexto("INICIO_CIUDADANO")
    }
}
