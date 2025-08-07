package com.jhoelnarvaez.codigoroto

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random
import kotlin.math.max

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

    private var contadorActivo = false

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
            appendLog("🏃 ¡Huiste del combate!")
            finish()
        }

        actualizarUI()
    }

    private fun actualizarUI() {
        playerHpText.text = "${playerHP}/100"
        playerEnText.text = "${playerEN}/50"
        playerHpBar.progress = playerHP
        playerEnBar.progress = playerEN

        enemyHpText.text = "${enemyHP}/80"
        enemyHpBar.progress = enemyHP

        tvTurno.text = if (turnoJugador) "🧑‍💻 Tu turno" else "👾 Turno del enemigo"

        bloquearBotones(turnoJugador)
    }

    private fun appendLog(mensaje: String) {
        tvCombatLog.append("\n$mensaje")
    }

    private fun bloquearBotones(activo: Boolean) {
        btnPing.isEnabled = activo
        btnFirewall.isEnabled = activo
    }

    private fun usarComando(tipo: String) {
        if (!turnoJugador || playerHP <= 0 || enemyHP <= 0) return

        var damage = 0
        var costo = 0
        var nombreAtaque = ""

        when (tipo) {
            "PING" -> {
                costo = 10
                if (playerEN < costo) {
                    appendLog("No tienes suficiente energía para usar PING.")
                    return
                }
                damage = Random.nextInt(15, 26)
                nombreAtaque = "PING"
            }
            "FIREWALL" -> {
                costo = 20
                if (playerEN < costo) {
                    appendLog("No tienes suficiente energía para usar FIREWALL.")
                    return
                }
                damage = Random.nextInt(25, 41)
                nombreAtaque = "FIREWALL"
            }
        }

        playerEN -= costo
        enemyHP = max(0, enemyHP - damage)

        appendLog("💥 Usaste $nombreAtaque e hiciste $damage de daño.")
        turnoJugador = false
        actualizarUI()

        if (enemyHP <= 0) {
            appendLog("🏆 ¡Enemigo derrotado!")
            guardarResultadoCombate(true)
            return
        }

        handler.postDelayed({
            turnoEnemigo()
        }, 1000)
    }

    private fun turnoEnemigo() {
        if (enemyHP <= 0 || playerHP <= 0) return

        tvTurno.text = "👾 Turno del enemigo..."

        val damage = Random.nextInt(10, 20)

        handler.postDelayed({
            playerHP = max(0, playerHP - damage)
            appendLog("⚔️ El enemigo te atacó causando $damage de daño.")
            actualizarUI()

            if (playerHP <= 0) {
                appendLog("!Has sido derrotado!")
                guardarResultadoCombate(false)
            } else {
                turnoJugador = true
                handler.postDelayed({
                    iniciarTurno()
                }, 1000)
            }
        }, 1000)
    }

    private fun iniciarTurno() {
        if (playerHP <= 0 || enemyHP <= 0) return

        actualizarUI()
        tiempoRestante = 10
        contadorActivo = true
        iniciarContador()
    }

    private fun iniciarContador() {
        if (!contadorActivo) return

        tvTiempo.text = "⏱️ Tiempo: $tiempoRestante s"

        if (tiempoRestante <= 0) {
            contadorActivo = false
            if (!turnoJugador) {
                turnoEnemigo()
            }
            return
        }

        handler.postDelayed({
            tiempoRestante--
            iniciarContador()
        }, 1000)
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
