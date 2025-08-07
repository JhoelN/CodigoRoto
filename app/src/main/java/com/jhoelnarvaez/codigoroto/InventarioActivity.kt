package com.jhoelnarvaez.codigoroto

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView

class InventarioActivity : AppCompatActivity() {

    private lateinit var btnVolver: Button
    private lateinit var btnCompilar: Button
    private lateinit var btnLimpiar: Button
    private lateinit var btnVolverJuego: Button
    private lateinit var txtContador: TextView

    private val selectedItems = mutableListOf<Int>()

    private lateinit var inventoryItems: List<MaterialCardView>
    private lateinit var cantidadTextViews: List<TextView>

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

        btnVolver = findViewById(R.id.btnVolver)
        btnCompilar = findViewById(R.id.btnCompilar)
        btnLimpiar = findViewById(R.id.btnLimpiarSeleccion)
        btnVolverJuego = findViewById(R.id.btnVolverAlJuego)
        txtContador = findViewById(R.id.txtContador)

        val grid = findViewById<GridLayout>(R.id.inventory_grid)
        inventoryItems = List(7) { index ->
            val view = grid.getChildAt(index)
            if (view !is MaterialCardView) {
                throw IllegalStateException("Elemento en posición $index no es un MaterialCardView")
            }
            view
        }

        cantidadTextViews = inventoryItems.map { card ->
            val layout = card.getChildAt(0) as? LinearLayout
                ?: throw IllegalStateException("MaterialCardView no tiene LinearLayout")
            val cantidadView = layout.getChildAt(2) as? TextView
                ?: throw IllegalStateException("No se encontró el TextView de cantidad")
            cantidadView
        }

        setupClickListeners()
        updateUI()
    }

    private fun setupClickListeners() {
        btnVolver.setOnClickListener { finish() }
        btnVolverJuego.setOnClickListener { finish() }
        btnLimpiar.setOnClickListener { limpiarSeleccion() }
        btnCompilar.setOnClickListener { compilarFragmentos() }

        inventoryItems.forEachIndexed { index, card ->
            card.setOnClickListener { mostrarDetallesItem(index) }
            card.setOnLongClickListener {
                seleccionarYMostrarItem(index)
                true
            }
        }
    }

    private fun mostrarDetallesItem(index: Int) {
        val mensaje = """
            ${itemNames[index]}
            
            ${itemDescriptions[index]}
            
            Cantidad disponible: ${itemCounts[index]}
        """.trimIndent()

        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    private fun seleccionarYMostrarItem(index: Int) {
        if (itemCounts[index] <= 0) {
            Toast.makeText(this, "No tienes este ítem disponible", Toast.LENGTH_SHORT).show()
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
        mostrarDetallesItem(index) // Mostrar descripción al seleccionar
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

        // Descontar uno de cada ítem seleccionado
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

        val totalItems = itemCounts.sum()
        txtContador.text = "Items: $totalItems\nSeleccionados: ${selectedItems.size}"

        cantidadTextViews.forEachIndexed { i, txt ->
            val originalText = txt.text.toString()
            val icon = originalText.split(" ")[0]
            txt.text = "$icon x${itemCounts[i]}"
        }
    }
}
