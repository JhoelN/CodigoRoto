package com.tuapp.nombre

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jhoelnarvaez.codigoroto.R

class InventarioActivity : AppCompatActivity() {

    // UI Components
    private lateinit var btnVolver: TextView
    private lateinit var btnCompilar: TextView
    private lateinit var btnLimpiar: TextView
    private lateinit var btnVolverJuego: TextView
    private val inventoryItems = arrayOfNulls<LinearLayout>(7)

    // Data
    private val selectedItems = mutableListOf<Int>()
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

        initViews()
        setupClickListeners()
        updateUI()
    }

    private fun initViews() {
        btnVolver = findViewById(R.id.btn_volver)
        btnCompilar = findViewById(R.id.btn_compilar)
        btnLimpiar = findViewById(R.id.btn_limpiar)
        btnVolverJuego = findViewById(R.id.btn_volver_juego)

        // Inicializar items del inventario
        inventoryItems[0] = findViewById(R.id.item_fragmento_basico)
        inventoryItems[1] = findViewById(R.id.item_codigo_debug)
        inventoryItems[2] = findViewById(R.id.item_kit_exploits)
        inventoryItems[3] = findViewById(R.id.item_parche_firewall)
        inventoryItems[4] = findViewById(R.id.item_celda_energia)
        inventoryItems[5] = findViewById(R.id.item_nano_reparador)
        inventoryItems[6] = findViewById(R.id.item_nucleo_cuantico)
    }

    private fun setupClickListeners() {
        // Botón volver
        btnVolver.setOnClickListener { finish() }

        // Botón compilar
        btnCompilar.setOnClickListener { compilarFragmentos() }

        // Botón limpiar selección
        btnLimpiar.setOnClickListener { limpiarSeleccion() }

        // Botón volver al juego
        btnVolverJuego.setOnClickListener { finish() }

        // Click listeners para items del inventario
        inventoryItems.forEachIndexed { index, item ->
            item?.setOnClickListener { mostrarDetallesItem(index) }

            // Doble click para seleccionar
            item?.setOnLongClickListener {
                toggleItemSelection(index)
                true
            }
        }
    }

    private fun mostrarDetallesItem(itemIndex: Int) {
        val details = "${itemNames[itemIndex]}\n\n" +
                "${itemDescriptions[itemIndex]}\n\n" +
                "Cantidad disponible: ${itemCounts[itemIndex]}"

        Toast.makeText(this, details, Toast.LENGTH_LONG).show()
    }

    private fun toggleItemSelection(itemIndex: Int) {
        if (itemCounts[itemIndex] <= 0) {
            Toast.makeText(this, "No tienes este item disponible", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedItems.contains(itemIndex)) {
            // Deseleccionar
            selectedItems.remove(itemIndex)
            inventoryItems[itemIndex]?.alpha = 1.0f
        } else {
            // Seleccionar
            selectedItems.add(itemIndex)
            inventoryItems[itemIndex]?.alpha = 0.7f
        }

        updateUI()
        val action = if (selectedItems.contains(itemIndex)) "seleccionado" else "deseleccionado"
        Toast.makeText(this, "${itemNames[itemIndex]} $action", Toast.LENGTH_SHORT).show()
    }

    private fun limpiarSeleccion() {
        selectedItems.clear()

        // Restaurar alpha de todos los items
        inventoryItems.forEach { item ->
            item?.alpha = 1.0f
        }

        updateUI()
        Toast.makeText(this, "Selección limpiada", Toast.LENGTH_SHORT).show()
    }

    private fun compilarFragmentos() {
        if (selectedItems.size < 2) {
            Toast.makeText(this, "Necesitas seleccionar al menos 2 fragmentos", Toast.LENGTH_SHORT).show()
            return
        }

        // Simular compilación
        val compilacion = buildString {
            append("Compilando:\n")
            selectedItems.forEach { itemIndex ->
                append("- ${itemNames[itemIndex]}\n")
            }
            append("\n¡Nueva habilidad creada!")
        }

        // Consumir items seleccionados
        selectedItems.forEach { itemIndex ->
            if (itemCounts[itemIndex] > 0) {
                itemCounts[itemIndex]--
            }
        }

        limpiarSeleccion()
        updateItemCounts()

        Toast.makeText(this, compilacion, Toast.LENGTH_LONG).show()
    }

    private fun updateUI() {
        // Actualizar contador de seleccionados en el header
        // Esto requeriría modificar el header para incluir el TextView correspondiente

        // Habilitar/deshabilitar botón compilar
        val isEnabled = selectedItems.size >= 2
        btnCompilar.alpha = if (isEnabled) 1.0f else 0.5f
        btnCompilar.isEnabled = isEnabled
    }

    private fun updateItemCounts() {
        // Actualizar las cantidades mostradas en cada item
        // Esto requeriría obtener referencias a los TextViews de cantidad de cada item
        // y actualizarlos con los nuevos valores de itemCounts[]

        inventoryItems.forEachIndexed { index, item ->
            // Aquí actualizarías el TextView de cantidad dentro de cada LinearLayout
            // Por simplicidad, solo mostramos un Toast con las cantidades actualizadas
        }
    }

    // Método helper para obtener el estado del inventario
    fun getSelectedItems(): List<Int> = selectedItems.toList()

    // Método helper para establecer cantidades de items (útil para testing)
    fun setItemCount(itemIndex: Int, count: Int) {
        if (itemIndex in itemCounts.indices) {
            itemCounts[itemIndex] = count
            updateItemCounts()
        }
    }
}