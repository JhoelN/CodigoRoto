package com.jhoelnarvaez.codigoroto

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.jhoelnarvaez.codigoroto.R

class InventarioActivity : AppCompatActivity() {

    private lateinit var btnVolver: Button
    private lateinit var btnCompilar: Button
    private lateinit var btnLimpiar: Button
    private lateinit var btnVolverJuego: Button

    private val selectedItems = mutableListOf<Int>()

    private lateinit var inventoryItems: List<MaterialCardView>

    private val itemNames = arrayOf(
        "Fragmento Básico", "Código Debug", "Kit de Exploits",
        "Parche de Firewall", "Celda de Energía", "Nano Reparador", "Núcleo Cuántico"
    )

    private val itemCounts = intArrayOf(3, 1, 1, 2, 5, 3, 1)

    private val itemDescriptions = arrayOf(
        "Fragmento de código básico usado para construir habilidades simples",
        "Herramienta de debugging avanzada para detectar errores del sistema",
        "Kit de exploits para ataques penetrantes en sistemas enemigos",
        "Parche de seguridad que fortalece las defensas del firewall",
        "Fuente de energía para alimentar habilidades de alto consumo",
        "Nanobots reparadores que restauran sistemas dañados",
        "Componente cuántico raro usado en habilidades legendarias"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventario)

        // Inicializar botones con IDs reales del XML
        btnVolver = findViewById(R.id.btnVolver)
        btnCompilar = findViewById(R.id.btnCompilar)
        btnLimpiar = findViewById(R.id.btnLimpiarSeleccion)
        btnVolverJuego = findViewById(R.id.btnVolverAlJuego)

        // Obtener referencias a los CardView manualmente por orden de aparición en el layout
        inventoryItems = listOf(
            getCardViewAt(0), getCardViewAt(1), getCardViewAt(2),
            getCardViewAt(3), getCardViewAt(4), getCardViewAt(5), getCardViewAt(6)
        )

        setupClickListeners()
        updateUI()
    }

    // Helper para obtener CardViews sin IDs
    private fun getCardViewAt(index: Int): MaterialCardView {
        val grid = findViewById<android.widget.GridLayout>(R.id.inventory_grid)
        return grid.getChildAt(index) as MaterialCardView
    }

    private fun setupClickListeners() {
        btnVolver.setOnClickListener { finish() }
        btnVolverJuego.setOnClickListener { finish() }
        btnLimpiar.setOnClickListener { limpiarSeleccion() }

        btnCompilar.setOnClickListener { compilarFragmentos() }

        inventoryItems.forEachIndexed { index, card ->
            card.setOnClickListener {
                mostrarDetallesItem(index)
            }
            card.setOnLongClickListener {
                toggleItemSelection(index)
                true
            }
        }
    }

    private fun mostrarDetallesItem(index: Int) {
        val mensaje = "${itemNames[index]}\n\n${itemDescriptions[index]}\n\nCantidad: ${itemCounts[index]}"
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    private fun toggleItemSelection(index: Int) {
        if (itemCounts[index] <= 0) {
            Toast.makeText(this, "No tienes este item disponible", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedItems.contains(index)) {
            selectedItems.remove(index)
            inventoryItems[index].alpha = 1.0f
        } else {
            selectedItems.add(index)
            inventoryItems[index].alpha = 0.6f
        }

        updateUI()
        val estado = if (selectedItems.contains(index)) "seleccionado" else "deseleccionado"
        Toast.makeText(this, "${itemNames[index]} $estado", Toast.LENGTH_SHORT).show()
    }

    private fun limpiarSeleccion() {
        selectedItems.clear()
        inventoryItems.forEach { it.alpha = 1.0f }
        updateUI()
        Toast.makeText(this, "Selección limpiada", Toast.LENGTH_SHORT).show()
    }

    private fun compilarFragmentos() {
        if (selectedItems.size < 2) {
            Toast.makeText(this, "Selecciona al menos 2 ítems", Toast.LENGTH_SHORT).show()
            return
        }

        val resultado = buildString {
            append("Compilando:\n")
            selectedItems.forEach { append("- ${itemNames[it]}\n") }
            append("\n¡Habilidad creada!")
        }

        selectedItems.forEach {
            if (itemCounts[it] > 0) itemCounts[it]--
        }

        limpiarSeleccion()
        updateUI()

        Toast.makeText(this, resultado, Toast.LENGTH_LONG).show()
    }

    private fun updateUI() {
        val habilitado = selectedItems.size >= 2
        btnCompilar.isEnabled = habilitado
        btnCompilar.alpha = if (habilitado) 1.0f else 0.4f
    }
}
