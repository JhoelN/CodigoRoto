package com.jhoelnarvaez.codigoroto

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HabilidadesActivity : AppCompatActivity() {

    private lateinit var btnVolver: TextView
    private lateinit var tvXpDisponible: TextView

    private lateinit var skillPing: LinearLayout
    private lateinit var skillDdos: LinearLayout
    private lateinit var skillSobrecarga: LinearLayout
    private lateinit var skillDebug: LinearLayout
    private lateinit var skillOptimizar: LinearLayout
    private lateinit var skillFirewall: LinearLayout
    private lateinit var skillEncriptacion: LinearLayout

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var xpDisponible: Int = 0
    private val habilidadesDesbloqueadas = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habilidades)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        initUI()
        cargarDatosJugador()
    }

    private fun initUI() {
        btnVolver = findViewById(R.id.btn_volver)
        tvXpDisponible = findViewById(R.id.tv_xp_disponible)

        skillPing = findViewById(R.id.skill_ping)
        skillDdos = findViewById(R.id.skill_ddos)
        skillSobrecarga = findViewById(R.id.skill_sobrecarga)
        skillDebug = findViewById(R.id.skill_debug)
        skillOptimizar = findViewById(R.id.skill_optimizar)
        skillFirewall = findViewById(R.id.skill_firewall)
        skillEncriptacion = findViewById(R.id.skill_encriptacion)

        btnVolver.setOnClickListener { finish() }

        skillPing.setOnClickListener { desbloquearHabilidad("PING") }
        skillDdos.setOnClickListener { desbloquearHabilidad("DDOS") }
        skillSobrecarga.setOnClickListener { desbloquearHabilidad("SOBRECARGA") }
        skillDebug.setOnClickListener { desbloquearHabilidad("DEBUG") }
        skillOptimizar.setOnClickListener { desbloquearHabilidad("OPTIMIZAR") }
        skillFirewall.setOnClickListener { desbloquearHabilidad("FIREWALL") }
        skillEncriptacion.setOnClickListener { desbloquearHabilidad("ENCRIPTACION") }
    }

    private fun cargarDatosJugador() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("usuarios").document(userId)
            .get()
            .addOnSuccessListener { doc ->
                xpDisponible = doc.getLong("xp")?.toInt() ?: 0
                val habilidades = doc.get("habilidades") as? List<String> ?: emptyList()
                habilidadesDesbloqueadas.clear()
                habilidadesDesbloqueadas.addAll(habilidades)
                actualizarUI()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun actualizarUI() {
        tvXpDisponible.text = "XP Disponible: $xpDisponible"

        // Habilidades con su tipo
        val habilidades = listOf(
            Triple("PING", skillPing, "ROJO"),
            Triple("DDOS", skillDdos, "ROJO"),
            Triple("SOBRECARGA", skillSobrecarga, "ROJO"),
            Triple("DEBUG", skillDebug, "VERDE"),
            Triple("OPTIMIZAR", skillOptimizar, "VERDE"),
            Triple("FIREWALL", skillFirewall, "AZUL"),
            Triple("ENCRIPTACION", skillEncriptacion, "AZUL")
        )

        for ((nombre, view, tipo) in habilidades) {
            val dependencias = when (nombre) {
                "DDOS" -> listOf("PING")
                "SOBRECARGA" -> listOf("DDOS")
                "OPTIMIZAR" -> listOf("DEBUG")
                "ENCRIPTACION" -> listOf("FIREWALL")
                else -> emptyList()
            }

            when {
                habilidadesDesbloqueadas.contains(nombre) -> {
                    view.setBackgroundResource(R.drawable.skill_unlocked)
                }
                dependencias.all { habilidadesDesbloqueadas.contains(it) } -> {
                    val fondo = when (tipo) {
                        "ROJO" -> R.drawable.skill_available_red
                        "VERDE" -> R.drawable.skill_available_green
                        "AZUL" -> R.drawable.skill_available_blue
                        else -> R.drawable.skill_locked
                    }
                    view.setBackgroundResource(fondo)
                }
                else -> {
                    view.setBackgroundResource(R.drawable.skill_locked)
                }
            }
        }
    }


    private fun desbloquearHabilidad(nombre: String) {
        if (habilidadesDesbloqueadas.contains(nombre)) {
            Toast.makeText(this, "$nombre ya desbloqueada", Toast.LENGTH_SHORT).show()
            return
        }

        val costoXP = 1 // Puedes asignar diferente costo por habilidad
        if (xpDisponible < costoXP) {
            Toast.makeText(this, "No tienes suficiente XP", Toast.LENGTH_SHORT).show()
            return
        }

        val dependencias = when (nombre) {
            "DDOS" -> listOf("PING")
            "SOBRECARGA" -> listOf("DDOS")
            "OPTIMIZAR" -> listOf("DEBUG")
            "ENCRIPTACION" -> listOf("FIREWALL")
            else -> emptyList()
        }

        if (!dependencias.all { habilidadesDesbloqueadas.contains(it) }) {
            Toast.makeText(this, "Desbloquea habilidades previas", Toast.LENGTH_SHORT).show()
            return
        }

        // Desbloquear
        xpDisponible -= costoXP
        habilidadesDesbloqueadas.add(nombre)
        guardarCambiosFirestore()
    }

    private fun guardarCambiosFirestore() {
        val userId = auth.currentUser?.uid ?: return
        val updates = mapOf(
            "xp" to xpDisponible,
            "habilidades" to habilidadesDesbloqueadas.toList()
        )

        firestore.collection("usuarios").document(userId)
            .update(updates)
            .addOnSuccessListener {
                Toast.makeText(this, "Habilidad desbloqueada", Toast.LENGTH_SHORT).show()
                actualizarUI()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
            }
    }
}
