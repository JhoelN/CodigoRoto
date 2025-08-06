package com.jhoelnarvaez.codigoroto

import android.os.Bundle
import android.widget.Button
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

        // Solución: agregar android:id="@+id/main" al LinearLayout en XML
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar el tablero de juego
        gameBoard = findViewById(R.id.gameBoard)

        // Configurar botones de movimiento
        setupMovementButtons()
    }

    private fun setupMovementButtons() {
        findViewById<Button>(R.id.btnMoveUp).setOnClickListener {
            gameBoard.movePlayer(0, -1)
        }

        findViewById<Button>(R.id.btnMoveDown).setOnClickListener {
            gameBoard.movePlayer(0, 1)
        }

        findViewById<Button>(R.id.btnMoveLeft).setOnClickListener {
            gameBoard.movePlayer(-1, 0)
        }

        findViewById<Button>(R.id.btnMoveRight).setOnClickListener {
            gameBoard.movePlayer(1, 0)
        }
    }
}