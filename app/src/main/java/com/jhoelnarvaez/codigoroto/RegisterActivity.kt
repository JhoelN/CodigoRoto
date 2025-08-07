package com.jhoelnarvaez.codigoroto

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inicializar Firebase Auth y Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Referencias a vistas
        val etUsuario = findViewById<EditText>(R.id.et_usuario)
        val etEmail = findViewById<EditText>(R.id.et_email)
        val etContrasena = findViewById<EditText>(R.id.et_contrasena)
        val btnCrearCuenta = findViewById<Button>(R.id.btn_crear_cuenta)
        val btnLogin = findViewById<Button>(R.id.btn_login)

        // Botón para ir a login
        btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Crear cuenta
        btnCrearCuenta.setOnClickListener {
            val usuario = etUsuario.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()

            // Validaciones
            if (usuario.isEmpty() || email.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (usuario.length > 8) {
                Toast.makeText(this, "El nombre de usuario no debe exceder los 8 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Correo electrónico no válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (contrasena.length < 8) {
                Toast.makeText(this, "La contraseña debe tener al menos 8 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, contrasena)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid

                        if (userId == null) {
                            Toast.makeText(this, "Error al obtener el ID del usuario", Toast.LENGTH_LONG).show()
                            return@addOnCompleteListener
                        }

                        // Datos iniciales del jugador
                        val datosJugador = hashMapOf(
                            "usuario" to usuario,
                            "email" to email,
                            "nivel" to 1,
                            "experiencia" to 0,
                            "inventario" to mapOf(
                                "fragmento_basico" to 3,
                                "codigo_debug" to 1,
                                "kit_exploits" to 1,
                                "parche_firewall" to 2,
                                "celda_energia" to 5,
                                "nano_reparador" to 3,
                                "nucleo_cuantico" to 1
                            ),
                            "habilidades" to emptyList<String>(),
                            "progreso" to mapOf(
                                "mision_actual" to "intro",
                                "mapa" to "sector_1"
                            )
                        )

                        firestore.collection("jugadores").document(userId)
                            .set(datosJugador)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, MenuActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error al guardar datos: ${e.message}", Toast.LENGTH_LONG).show()
                            }

                    } else {
                        Toast.makeText(this, "Error al registrar: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}
