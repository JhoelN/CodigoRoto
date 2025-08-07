package com.jhoelnarvaez.codigoroto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MenuActivity : AppCompatActivity() {

    private lateinit var tvBienvenida: TextView
    private lateinit var btnSalir: Button
    private lateinit var btnInventario: Button
    private lateinit var btnHabilidades: Button
    private lateinit var btnContinuar: Button
    private lateinit var btnNuevaPartida: Button

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Vistas
        tvBienvenida = findViewById(R.id.tv_bienvenido)
        btnSalir = findViewById(R.id.btn_salir)
        btnInventario = findViewById(R.id.btn_inventario)
        btnHabilidades = findViewById(R.id.btn_habilidades)
        btnContinuar = findViewById(R.id.btn_continuar)
        btnNuevaPartida = findViewById(R.id.btn_nueva_partida)

        // Mostrar nombre del usuario
        obtenerNombreUsuario()

        // Acciones de navegación
        btnNuevaPartida.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        btnContinuar.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        btnHabilidades.setOnClickListener {
            startActivity(Intent(this, HabilidadesActivity::class.java))
        }

        btnInventario.setOnClickListener {
            startActivity(Intent(this, InventarioActivity::class.java))
        }

        btnSalir.setOnClickListener {
            auth.signOut()
            finish()
        }
    }

    private fun obtenerNombreUsuario() {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            db.collection("usuarios").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val nombre = document.getString("usuario") ?: "Usuario"
                        tvBienvenida.text = "Bienvenido, $nombre"
                    } else {
                        tvBienvenida.text = "Bienvenido"
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show()
                }
        } else {
            tvBienvenida.text = "Bienvenido"
        }
    }
}
