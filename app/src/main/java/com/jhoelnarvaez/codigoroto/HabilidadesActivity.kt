package com.jhoelnarvaez.codigoroto

import android.os.Bundle
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

        // Asignar listeners a cada habilidad
        skillPing.setOnClickListener { intentarDesbloquear("PING") }
        skillDdos.setOnClickListener { intentarDesbloquear("DDOS") }
        skillSobrecarga.setOnClickListener { intentarDesbloquear("SOBRECARGA") }
        skillDebug.setOnClickListener { intentarDesbloquear("DEBUG") }
        skillOptimizar.setOnClickListener { intentarDesbloquear("OPTIMIZAR") }
        skillFirewall.setOnClickListener { intentarDesbloquear("FIREWALL") }
        skillEncriptacion.setOnClickListener { intentarDesbloquear("ENCRIPTACION") }
    }

    private fun cargarDatosJugador() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("usuarios").document(userId)
            .get()
            .addOnSuccessListener { doc ->
                xpDisponible = doc.getLong("xp")?.toInt() ?: 0
                val habilidades = doc.get("habilidades") as? List<*> ?: emptyList<Any>()
                habilidadesDesbloqueadas.clear()
                habilidadesDesbloqueadas.addAll(habilidades.filterIsInstance<String>())
                actualizarUI()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar datos del jugador", Toast.LENGTH_SHORT).show()
            }
    }

    private fun actualizarUI() {
        tvXpDisponible.text = "XP Disponible: $xpDisponible"

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
            val dependencias = obtenerDependencias(nombre)

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

    private fun intentarDesbloquear(nombre: String) {
        val descripcion = obtenerDescripcion(nombre)

        // Mostrar siempre descripción
        Toast.makeText(this, "$nombre: $descripcion", Toast.LENGTH_SHORT).show()

        // Ya está desbloqueada
        if (habilidadesDesbloqueadas.contains(nombre)) return

        // Verifica requisitos previos
        val dependencias = obtenerDependencias(nombre)
        if (!dependencias.all { habilidadesDesbloqueadas.contains(it) }) {
            Toast.makeText(this, "Debes desbloquear primero: ${dependencias.joinToString(", ")}", Toast.LENGTH_SHORT).show()
            return
        }

        // Verifica XP suficiente
        val costo = 1
        if (xpDisponible < costo) {
            Toast.makeText(this, "XP insuficiente para desbloquear $nombre", Toast.LENGTH_SHORT).show()
            return
        }

        // Desbloquea
        xpDisponible -= costo
        habilidadesDesbloqueadas.add(nombre)

        guardarCambiosFirestore()
        actualizarUI()
    }

    private fun obtenerDependencias(nombre: String): List<String> {
        return when (nombre) {
            "DDOS" -> listOf("PING")
            "SOBRECARGA" -> listOf("DDOS")
            "OPTIMIZAR" -> listOf("DEBUG")
            "ENCRIPTACION" -> listOf("FIREWALL")
            else -> emptyList()
        }
    }

    private fun obtenerDescripcion(nombre: String): String {
        return when (nombre) {
            "PING" -> "Ataque básico que consume poca energía."
            "DDOS" -> "Ataque masivo que abruma al sistema enemigo. Requiere: PING."
            "SOBRECARGA" -> "Provoca una sobrecarga del sistema. Requiere: DDOS."
            "DEBUG" -> "Repara errores internos del sistema para mejorar eficiencia."
            "OPTIMIZAR" -> "Mejora el rendimiento general del sistema. Requiere: DEBUG."
            "FIREWALL" -> "Genera una defensa contra ataques enemigos."
            "ENCRIPTACION" -> "Mejora la seguridad mediante cifrado. Requiere: FIREWALL."
            else -> "Habilidad sin descripción."
        }
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
                Toast.makeText(this, "Habilidad desbloqueada correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al actualizar Firestore", Toast.LENGTH_SHORT).show()
            }
    }
}
