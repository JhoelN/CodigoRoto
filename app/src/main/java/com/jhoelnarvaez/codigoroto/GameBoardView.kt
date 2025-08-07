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

    private val GRID_WIDTH = 16
    private val GRID_HEIGHT = 12
    private var cellSize = 50f

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

    private var playerX = 1
    private var playerY = 1

    private val enemies = mutableListOf<Pair<Int, Int>>().apply {
        add(Pair(7, 3))
        add(Pair(9, 8))
        add(Pair(3, 6))
        add(Pair(12, 2))
    }

    private val items = mutableListOf<Pair<Int, Int>>().apply {
        add(Pair(2, 4))
        add(Pair(11, 7))
        add(Pair(6, 9))
        add(Pair(14, 5))
    }

    private val walls = mutableListOf<Rect>().apply {
        add(Rect(5, 2, 7, 3))
        add(Rect(10, 4, 11, 8))
        add(Rect(12, 6, 13, 10))
        add(Rect(2, 8, 6, 9))
    }

    private val goldItem = Pair(8, 9)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val availableWidth = (w - paddingLeft - paddingRight).toFloat()
        val availableHeight = (h - paddingTop - paddingBottom).toFloat()
        cellSize = minOf(availableWidth / GRID_WIDTH, availableHeight / GRID_HEIGHT)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val startX = (width - (GRID_WIDTH * cellSize)) / 2
        val startY = (height - (GRID_HEIGHT * cellSize)) / 2

        val boardRect = RectF(
            startX, startY,
            startX + (GRID_WIDTH * cellSize),
            startY + (GRID_HEIGHT * cellSize)
        )

        canvas.drawRect(boardRect, backgroundPaint)
        canvas.drawRect(boardRect, borderPaint)
        drawGrid(canvas, startX, startY)
        drawWalls(canvas, startX, startY)
        drawItems(canvas, startX, startY)
        drawEnemies(canvas, startX, startY)
        drawGoldItem(canvas, startX, startY)
        drawPlayer(canvas, startX, startY)
    }

    private fun drawGrid(canvas: Canvas, startX: Float, startY: Float) {
        for (i in 0..GRID_WIDTH) {
            val x = startX + (i * cellSize)
            canvas.drawLine(x, startY, x, startY + (GRID_HEIGHT * cellSize), gridPaint)
        }
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
            val cx = startX + (x * cellSize) + cellSize / 2
            val cy = startY + (y * cellSize) + cellSize / 2
            canvas.drawCircle(cx, cy, cellSize * 0.25f, itemPaint)
            textPaint.textSize = cellSize * 0.3f
            canvas.drawText("+", cx, cy + (textPaint.textSize / 3), textPaint)
        }
    }

    private fun drawEnemies(canvas: Canvas, startX: Float, startY: Float) {
        for ((x, y) in enemies) {
            val cx = startX + (x * cellSize) + cellSize / 2
            val cy = startY + (y * cellSize) + cellSize / 2
            canvas.drawCircle(cx, cy, cellSize * 0.3f, enemyPaint)
            textPaint.textSize = cellSize * 0.4f
            canvas.drawText("⚠", cx, cy + (textPaint.textSize / 3), textPaint)
        }
    }

    private fun drawGoldItem(canvas: Canvas, startX: Float, startY: Float) {
        val (x, y) = goldItem
        val cx = startX + (x * cellSize) + cellSize / 2
        val cy = startY + (y * cellSize) + cellSize / 2
        val paint = Paint().apply {
            color = Color.parseColor("#FFD700")
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawCircle(cx, cy, cellSize * 0.25f, paint)
        textPaint.textSize = cellSize * 0.3f
        textPaint.color = Color.BLACK
        canvas.drawText("⚿", cx, cy + (textPaint.textSize / 3), textPaint)
        textPaint.color = Color.WHITE
    }

    private fun drawPlayer(canvas: Canvas, startX: Float, startY: Float) {
        val cx = startX + (playerX * cellSize) + cellSize / 2
        val cy = startY + (playerY * cellSize) + cellSize / 2
        canvas.drawCircle(cx, cy, cellSize * 0.35f, playerPaint)
        textPaint.textSize = cellSize * 0.4f
        canvas.drawText("♦", cx, cy + (textPaint.textSize / 3), textPaint)
    }

    // NUEVA VERSIÓN
    fun movePlayer(deltaX: Int, deltaY: Int): Boolean {
        val newX = playerX + deltaX
        val newY = playerY + deltaY

        if (newX < 0 || newX >= GRID_WIDTH || newY < 0 || newY >= GRID_HEIGHT) {
            return false
        }

        if (isWallAt(newX, newY)) {
            return false
        }

        playerX = newX
        playerY = newY
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

    fun getPlayerPosition(): Pair<Int, Int> = Pair(playerX, playerY)
    fun isEnemyAt(x: Int, y: Int): Boolean = enemies.contains(Pair(x, y))
    fun isItemAt(x: Int, y: Int): Boolean = items.contains(Pair(x, y))
    fun isGoldItemAt(x: Int, y: Int): Boolean = goldItem == Pair(x, y)

    fun removeEnemyAt(x: Int, y: Int) {
        enemies.remove(Pair(x, y))
        invalidate()
    }

    fun removeItemAt(x: Int, y: Int) {
        items.remove(Pair(x, y))
        invalidate()
    }

    fun removeGoldItem() {
        // Aquí podrías cambiar su posición a -1, -1 si quieres "eliminarlo"
        invalidate()
    }
}
