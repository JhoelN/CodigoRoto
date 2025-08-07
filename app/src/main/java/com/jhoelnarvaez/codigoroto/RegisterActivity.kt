package com.jhoelnarvaez.codigoroto

import android.content.Intent
import android.os.Bundle
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
        btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        btnCrearCuenta.setOnClickListener {
            val usuario = etUsuario.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()

            if (usuario.isEmpty() || email.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            // Crear usuario en Firebase Auth
            auth.createUserWithEmailAndPassword(email, contrasena)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid

                        // Guardar datos en Firestore
                        val datosUsuario = hashMapOf(
                            "usuario" to usuario,
                            "email" to email
                        )

                        firestore.collection("usuarios").document(userId!!)
                            .set(datosUsuario)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this,
                                    "Usuario registrado correctamente",
                                    Toast.LENGTH_SHORT
                                ).show()
                                // Puedes redirigir a otra pantalla aquí
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    this,
                                    "Error al guardar los datos: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                    } else {
                        Toast.makeText(
                            this,
                            "Error al registrar: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }
}
