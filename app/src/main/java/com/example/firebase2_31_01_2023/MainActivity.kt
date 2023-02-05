package com.example.firebase2_31_01_2023

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.example.firebase2_31_01_2023.databinding.ActivityMainBinding
import com.example.firebase2_31_01_2023.prefs.Prefs
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var prefs: Prefs

    private var email = ""
    private var password = ""

    private val responseLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val tarea = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val cuenta = tarea.getResult(ApiException::class.java)
                if (cuenta != null) {
                    val credencial = GoogleAuthProvider.getCredential(cuenta.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credencial)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                prefs.guardarEmail(cuenta.email!!)
                                irHome()
                            } else {
                                mostrarError("Error al autenticar con Google")
                            }
                        }
                }
            } catch (e: ApiException) {
                mostrarError("Error al autenticar con Google")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        prefs = Prefs(this)
        setContentView(binding.root)
        comprobarSesion()
        setListeners()
    }

    private fun setListeners() {
        binding.btnLogin.setOnClickListener { login() }
        binding.btnRegistrar.setOnClickListener { register() }
        binding.btnGoogle.setOnClickListener { loginGoogle() }
    }

    private fun loginGoogle() {
        val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("717241826111-92225cis6bd4h3ibp52qfpmme16v3isu.apps.googleusercontent.com")
            .requestEmail()
            .build()
        val googleClient = GoogleSignIn.getClient(this, googleConf)
        googleClient.signOut()
        responseLauncher.launch(googleClient.signInIntent)
    }

    private fun register() {
        if (!recogerDatos()) return
        //Podemos suponer que los datos estan correctos aqui, y procedemos a registrar en firebase
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    prefs.guardarEmail(email)
                    irHome()
                } else {
                   mostrarError("Error al registrar")
                }
            }

    }

    private fun mostrarError(s: String) {
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage(s)
            .setPositiveButton("Aceptar", null)
            .show()
    }

    private fun recogerDatos(): Boolean {
        email = binding.etMail.text.toString().trim()
        if (email.isEmpty()) {
            binding.etMail.error = "El email es obligatorio"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etMail.error = "El email no es válido"
            return false
        }
        password = binding.etPass.text.toString()
        if (password.isEmpty()) {
            binding.etPass.error = "La contraseña es obligatoria"
            return false
        }
        return true

    }

    private fun login() {
        if (!recogerDatos()) return
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    prefs.guardarEmail(email)
                    irHome()
                } else {
                    mostrarError("Error al iniciar sesión")
                }
            }
    }

    private fun comprobarSesion() {
        var e = prefs.obtenerEmail()
        if (!e.isNullOrEmpty()) {
            irHome()
        }
    }

    private fun irHome() {
        startActivity(Intent(this, HomeActivity::class.java))
    }
}