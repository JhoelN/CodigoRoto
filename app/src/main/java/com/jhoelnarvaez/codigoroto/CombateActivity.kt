package com.jhoelnarvaez.codigoroto

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random

class CombateActivity : AppCompatActivity() {

    private lateinit var tvTurno: TextView
    private lateinit var tvTiempo: TextView
    private lateinit var tvCombatLog: TextView

    private lateinit var playerHpText: TextView
    private lateinit var playerEnText: TextView
    private lateinit var playerHpBar: ProgressBar
    private lateinit var playerEnBar: ProgressBar

    private lateinit var enemyHpText: TextView
    private lateinit var enemyHpBar: ProgressBar

    private lateinit var btnPing: LinearLayout
    private lateinit var btnFirewall: LinearLayout
    private lateinit var btnHuir: TextView

    private var playerHP = 100
    private var playerEN = 50
    private var enemyHP = 80

    private var turnoJugador = true
    private var tiempoRestante = 10
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_combate)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        initUI()
        iniciarTurno()
    }

    private fun initUI() {
        tvTurno = findViewById(R.id.tv_turno)
        tvTiempo = findViewById(R.id.tv_tiempo)
        tvCombatLog = findViewById(R.id.tv_combat_log)

        playerHpText = findViewById(R.id.tv_player_hp)
        playerEnText = findViewById(R.id.tv_player_en)
        playerHpBar = findViewById(R.id.progress_player_hp)
        playerEnBar = findViewById(R.id.progress_player_en)

        enemyHpText = findViewById(R.id.tv_enemy_hp)
        enemyHpBar = findViewById(R.id.progress_enemy_hp)

        btnPing = findViewById(R.id.cmd_ping)
        btnFirewall = findViewById(R.id.cmd_firewall)
        btnHuir = findViewById(R.id.btn_huir)

        btnPing.setOnClickListener { usarComando("PING") }
        btnFirewall.setOnClickListener { usarComando("FIREWALL") }
        btnHuir.setOnClickListener {
            appendLog("¡Huiste del combate!")
            finish()
        }

        actualizarUI()
    }

    private fun actualizarUI() {
        playerHpText.text = "$playerHP/100"
        playerEnText.text = "$playerEN/50"
        playerHpBar.progress = playerHP
        playerEnBar.progress = playerEN

        enemyHpText.text = "$enemyHP/80"
        enemyHpBar.progress = enemyHP
    }

    private fun appendLog(mensaje: String) {
        tvCombatLog.append("\n$mensaje")
    }

    private fun usarComando(tipo: String) {
        if (!turnoJugador) return

        when (tipo) {
            "PING" -> {
                if (playerEN >= 10) {
                    val damage = Random.nextInt(15, 26)
                    playerEN -= 10
                    enemyHP -= damage
                    appendLog("Usaste PING: hiciste $damage de daño.")
                } else {
                    appendLog("No tienes suficiente energía para usar PING.")
                    return
                }
            }
            "FIREWALL" -> {
                if (playerEN >= 20) {
                    val damage = Random.nextInt(25, 41)
                    playerEN -= 20
                    enemyHP -= damage
                    appendLog("Usaste FIREWALL: hiciste $damage de daño.")
                } else {
                    appendLog("No tienes suficiente energía para usar FIREWALL.")
                    return
                }
            }
        }

        if (enemyHP <= 0) {
            appendLog("¡Enemigo derrotado!")
            guardarResultadoCombate(true)
            return
        }

        actualizarUI()
        turnoJugador = false
        iniciarTurno()
    }

    private fun iniciarTurno() {
        actualizarUI()
        tiempoRestante = 10
        tvTurno.text = if (turnoJugador) "Tu turno" else "Turno enemigo"
        handler.postDelayed({ if (!turnoJugador) turnoEnemigo() }, 1000)
    }

    private fun turnoEnemigo() {
        val damage = Random.nextInt(10, 20)
        playerHP -= damage
        appendLog("El enemigo te atacó con $damage de daño.")
        if (playerHP <= 0) {
            appendLog("¡Has sido derrotado!")
            guardarResultadoCombate(false)
            return
        }
        turnoJugador = true
        actualizarUI()
    }

    private fun guardarResultadoCombate(victoria: Boolean) {
        val userId = auth.currentUser?.uid ?: return
        val resultado = hashMapOf(
            "victoria" to victoria,
            "timestamp" to System.currentTimeMillis(),
            "enemigo" to "Troyano Alpha",
            "hp_restante" to playerHP,
            "energia_restante" to playerEN
        )
        firestore.collection("usuarios").document(userId)
            .collection("combates")
            .add(resultado)
            .addOnSuccessListener {
                appendLog("Resultado del combate guardado.")
                handler.postDelayed({
                    startActivity(Intent(this, MenuActivity::class.java))
                    finish()
                }, 2000)
            }
            .addOnFailureListener {
                appendLog("Error al guardar el combate.")
            }
    }
}
