package com.jhoelnarvaez.codigoroto

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class GameBoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Configuración del tablero
    private val GRID_WIDTH = 16
    private val GRID_HEIGHT = 12
    private var cellSize = 50f

    // Paint objects para dibujar
    private val gridPaint = Paint().apply {
        color = Color.parseColor("#003366")
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }

    private val backgroundPaint = Paint().apply {
        color = Color.parseColor("#001122")
        style = Paint.Style.FILL
    }

    private val borderPaint = Paint().apply {
        color = Color.parseColor("#FF6666")
        strokeWidth = 8f
        style = Paint.Style.STROKE
        pathEffect = DashPathEffect(floatArrayOf(20f, 10f), 0f)
    }

    private val playerPaint = Paint().apply {
        color = Color.parseColor("#FF00FF")
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val enemyPaint = Paint().apply {
        color = Color.parseColor("#FF4444")
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val itemPaint = Paint().apply {
        color = Color.parseColor("#00FF88")
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val wallPaint = Paint().apply {
        color = Color.parseColor("#666666")
        style = Paint.Style.FILL
    }

    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 24f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        typeface = Typeface.DEFAULT_BOLD
    }

    // Posiciones del jugador
    private var playerX = 1
    private var playerY = 1

    // Enemigos (posiciones donde aparecen los círculos rojos)
    private val enemies = mutableListOf<Pair<Int, Int>>().apply {
        add(Pair(7, 3))   // Enemigo 1
        add(Pair(9, 8))   // Enemigo 2
        add(Pair(3, 6))   // Enemigo 3
        add(Pair(12, 2))  // Enemigo 4
    }

    // Ítems (círculos verdes)
    private val items = mutableListOf<Pair<Int, Int>>().apply {
        add(Pair(2, 4))   // Ítem 1
        add(Pair(11, 7))  // Ítem 2
        add(Pair(6, 9))   // Ítem 3
        add(Pair(14, 5))  // Ítem 4
    }

    // Muros/obstáculos (barras grises como en tu imagen)
    private val walls = mutableListOf<Rect>().apply {
        add(Rect(5, 2, 7, 3))    // Muro horizontal superior
        add(Rect(10, 4, 11, 8))  // Muro vertical derecho
        add(Rect(12, 6, 13, 10)) // Muro vertical derecho 2
        add(Rect(2, 8, 6, 9))    // Muro horizontal inferior
    }

    // Objeto dorado (como en tu imagen)
    private val goldItem = Pair(8, 9)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // Calcular el tamaño de celda basado en el tamaño de la vista
        val availableWidth = (w - paddingLeft - paddingRight).toFloat()
        val availableHeight = (h - paddingTop - paddingBottom).toFloat()

        cellSize = minOf(availableWidth / GRID_WIDTH, availableHeight / GRID_HEIGHT)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val startX = (width - (GRID_WIDTH * cellSize)) / 2
        val startY = (height - (GRID_HEIGHT * cellSize)) / 2

        // Dibujar fondo del tablero
        val boardRect = RectF(
            startX, startY,
            startX + (GRID_WIDTH * cellSize),
            startY + (GRID_HEIGHT * cellSize)
        )
        canvas.drawRect(boardRect, backgroundPaint)

        // Dibujar borde punteado rojo
        canvas.drawRect(boardRect, borderPaint)

        // Dibujar grid
        drawGrid(canvas, startX, startY)

        // Dibujar muros
        drawWalls(canvas, startX, startY)

        // Dibujar ítems (círculos verdes)
        drawItems(canvas, startX, startY)

        // Dibujar enemigos (círculos rojos con símbolo de peligro)
        drawEnemies(canvas, startX, startY)

        // Dibujar ítem dorado especial
        drawGoldItem(canvas, startX, startY)

        // Dibujar jugador
        drawPlayer(canvas, startX, startY)
    }

    private fun drawGrid(canvas: Canvas, startX: Float, startY: Float) {
        // Líneas verticales
        for (i in 0..GRID_WIDTH) {
            val x = startX + (i * cellSize)
            canvas.drawLine(x, startY, x, startY + (GRID_HEIGHT * cellSize), gridPaint)
        }

        // Líneas horizontales
        for (j in 0..GRID_HEIGHT) {
            val y = startY + (j * cellSize)
            canvas.drawLine(startX, y, startX + (GRID_WIDTH * cellSize), y, gridPaint)
        }
    }

    private fun drawWalls(canvas: Canvas, startX: Float, startY: Float) {
        for (wall in walls) {
            val left = startX + (wall.left * cellSize)
            val top = startY + (wall.top * cellSize)
            val right = startX + (wall.right * cellSize)
            val bottom = startY + (wall.bottom * cellSize)

            canvas.drawRect(left, top, right, bottom, wallPaint)
        }
    }

    private fun drawItems(canvas: Canvas, startX: Float, startY: Float) {
        for ((x, y) in items) {
            val centerX = startX + (x * cellSize) + (cellSize / 2)
            val centerY = startY + (y * cellSize) + (cellSize / 2)
            val radius = cellSize * 0.25f

            canvas.drawCircle(centerX, centerY, radius, itemPaint)

            // Dibujar símbolo + en el centro
            textPaint.textSize = cellSize * 0.3f
            canvas.drawText("+", centerX, centerY + (textPaint.textSize / 3), textPaint)
        }
    }

    private fun drawEnemies(canvas: Canvas, startX: Float, startY: Float) {
        for ((x, y) in enemies) {
            val centerX = startX + (x * cellSize) + (cellSize / 2)
            val centerY = startY + (y * cellSize) + (cellSize / 2)
            val radius = cellSize * 0.3f

            canvas.drawCircle(centerX, centerY, radius, enemyPaint)

            // Dibujar símbolo de peligro
            textPaint.textSize = cellSize * 0.4f
            canvas.drawText("⚠", centerX, centerY + (textPaint.textSize / 3), textPaint)
        }
    }

    private fun drawGoldItem(canvas: Canvas, startX: Float, startY: Float) {
        val (x, y) = goldItem
        val centerX = startX + (x * cellSize) + (cellSize / 2)
        val centerY = startY + (y * cellSize) + (cellSize / 2)
        val radius = cellSize * 0.25f

        val goldPaint = Paint().apply {
            color = Color.parseColor("#FFD700")
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        canvas.drawCircle(centerX, centerY, radius, goldPaint)

        // Dibujar símbolo de cofre o moneda
        textPaint.textSize = cellSize * 0.3f
        textPaint.color = Color.BLACK
        canvas.drawText("⚿", centerX, centerY + (textPaint.textSize / 3), textPaint)
        textPaint.color = Color.WHITE // Resetear color
    }

    private fun drawPlayer(canvas: Canvas, startX: Float, startY: Float) {
        val centerX = startX + (playerX * cellSize) + (cellSize / 2)
        val centerY = startY + (playerY * cellSize) + (cellSize / 2)
        val radius = cellSize * 0.35f

        canvas.drawCircle(centerX, centerY, radius, playerPaint)

        // Dibujar símbolo del jugador
        textPaint.textSize = cellSize * 0.4f
        canvas.drawText("♦", centerX, centerY + (textPaint.textSize / 3), textPaint)
    }

    // Funciones para mover el jugador
    fun movePlayer(deltaX: Int, deltaY: Int): Boolean {
        val newX = playerX + deltaX
        val newY = playerY + deltaY

        // Verificar límites del tablero
        if (newX < 0 || newX >= GRID_WIDTH || newY < 0 || newY >= GRID_HEIGHT) {
            return false
        }

        // Verificar colisiones con muros
        if (isWallAt(newX, newY)) {
            return false
        }

        // Actualizar posición
        playerX = newX
        playerY = newY

        // Redibujar vista
        invalidate()

        return true
    }

    private fun isWallAt(x: Int, y: Int): Boolean {
        for (wall in walls) {
            if (x >= wall.left && x < wall.right && y >= wall.top && y < wall.bottom) {
                return true
            }
        }
        return false
    }

    // Funciones de consulta
    fun getPlayerPosition(): Pair<Int, Int> = Pair(playerX, playerY)

    fun isEnemyAt(x: Int, y: Int): Boolean = enemies.contains(Pair(x, y))

    fun isItemAt(x: Int, y: Int): Boolean = items.contains(Pair(x, y))

    fun isGoldItemAt(x: Int, y: Int): Boolean = goldItem.first == x && goldItem.second == y

    // Función para remover enemigos después del combate
    fun removeEnemyAt(x: Int, y: Int) {
        enemies.remove(Pair(x, y))
        invalidate()
    }

    // Función para remover ítems después de recogerlos
    fun removeItemAt(x: Int, y: Int) {
        items.remove(Pair(x, y))
        invalidate()
    }

    // Función para remover ítem dorado
    fun removeGoldItem() {
        // En lugar de remover, podríamos moverlo a posición inválida
        invalidate()
    }
}