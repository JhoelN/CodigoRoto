package com.jhoelnarvaez.codigoroto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var gameBoard: GameBoardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        gameBoard = findViewById(R.id.gameBoard)

        findViewById<Button>(R.id.btnMenu).setOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
            finish()
        }

        findViewById<Button>(R.id.btnSkills).setOnClickListener {
            startActivity(Intent(this, HabilidadesActivity::class.java))
        }

        findViewById<Button>(R.id.btnItems).setOnClickListener {
            startActivity(Intent(this, InventarioActivity::class.java))
        }

        setupMovementButtons()
    }

    private fun setupMovementButtons() {
        findViewById<Button>(R.id.btnMoveUp).setOnClickListener { moveAndHandleEvent(0, -1) }
        findViewById<Button>(R.id.btnMoveDown).setOnClickListener { moveAndHandleEvent(0, 1) }
        findViewById<Button>(R.id.btnMoveLeft).setOnClickListener { moveAndHandleEvent(-1, 0) }
        findViewById<Button>(R.id.btnMoveRight).setOnClickListener { moveAndHandleEvent(1, 0) }
    }

    private fun moveAndHandleEvent(dx: Int, dy: Int) {
        val moved = gameBoard.movePlayer(dx, dy)
        if (!moved) {
            Toast.makeText(this, "No puedes moverte allí", Toast.LENGTH_SHORT).show()
            return
        }

        val event = checkGameEvent()
        when (event) {
            GameEvent.ENEMY -> {
                Toast.makeText(this, "¡Enemigo encontrado!", Toast.LENGTH_SHORT).show()
                gameBoard.removeEnemyAt(
                    gameBoard.getPlayerPosition().first,
                    gameBoard.getPlayerPosition().second
                )
                startActivity(Intent(this, CombateActivity::class.java))
            }

            GameEvent.ITEM -> {
                Toast.makeText(this, "¡Has recogido un ítem!", Toast.LENGTH_SHORT).show()
                gameBoard.removeItemAt(
                    gameBoard.getPlayerPosition().first,
                    gameBoard.getPlayerPosition().second
                )
                startActivity(Intent(this, InventarioActivity::class.java))
            }

            GameEvent.GOLD -> {
                Toast.makeText(this, "¡Encontraste el ítem dorado!", Toast.LENGTH_LONG).show()
                gameBoard.removeGoldItem()
                startActivity(Intent(this, HabilidadesActivity::class.java))
            }

            else -> {
            }
        }
    }

    private fun checkGameEvent(): GameEvent {
        val (x, y) = gameBoard.getPlayerPosition()

        return when {
            gameBoard.isEnemyAt(x, y) -> GameEvent.ENEMY
            gameBoard.isItemAt(x, y) -> GameEvent.ITEM
            gameBoard.isGoldItemAt(x, y) -> GameEvent.GOLD
            else -> GameEvent.NONE
        }
    }
}
