package com.example.routex_app.game

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.TimeUtils

class MotorJuegoRouteX(val tipo: String, val onGameOver: () -> Unit) : ApplicationAdapter() {

    private lateinit var batch: SpriteBatch
    private lateinit var font: BitmapFont

    // Texturas Reales (JPG) y de soporte
    private lateinit var texturaVehiculo: Texture
    private lateinit var texturaObstaculo: Texture

    // Física y Estado
    private var anchoPantalla = 0f
    private var altoPantalla = 0f
    private var gravedad = -2000f
    private var impulsoSalto = 600f
    private var velocidadY = 0f
    private var gameState = 0 // 0: Esperando, 1: Jugando, 2: GameOver
    private var puntuacion = 0

    // Entidades
    private lateinit var rectJugador: Rectangle
    private lateinit var obstaculos: Array<Rectangle>
    private var ultimoTiempoObstaculo: Long = 0
    private val HUECO_OBSTACULO = 400f // Un poco más de espacio para los JPG
    private val ANCHO_OBSTACULO = 100f
    private val VELOCIDAD_OBSTACULOS = 450f

    override fun create() {
        batch = SpriteBatch()
        font = BitmapFont()
        font.data.setScale(4f) // Texto un poco más grande

        anchoPantalla = Gdx.graphics.width.toFloat()
        altoPantalla = Gdx.graphics.height.toFloat()

        // 1. CARGAR LA IMAGEN SEGÚN EL TIPO (Desde assets)
        texturaVehiculo = when (tipo.uppercase()) {
            "AVION" -> Texture(Gdx.files.internal("avion.jpg"))
            "BARCO" -> Texture(Gdx.files.internal("barco.jpg"))
            else -> Texture(Gdx.files.internal("camion.jpg"))
        }

        // 2. TEXTURA PARA OBSTÁCULOS (Color rojo sólido)
        texturaObstaculo = crearTexturaSimple(1, 1, Color.SCARLET)

        // 3. AJUSTAR FÍSICA SEGÚN VEHÍCULO
        when (tipo.uppercase()) {
            "AVION" -> {
                gravedad = -1400f
                impulsoSalto = 500f
            }
            "BARCO" -> {
                gravedad = -2800f
                impulsoSalto = 850f
            }
            "CAMION" -> {
                gravedad = -2000f
                impulsoSalto = 650f
            }
        }

        inicializarMundo()
    }

    private fun inicializarMundo() {
        // Ajustamos el tamaño del rectángulo (ancho 140, alto 70) para que el JPG se vea bien
        rectJugador = Rectangle(anchoPantalla / 4f, altoPantalla / 2f, 140f, 70f)
        velocidadY = 0f
        obstaculos = Array()
        puntuacion = 0
        gameState = 0
    }

    override fun render() {
        // FONDO SOLICITADO #666666 (0.4f en RGB aprox)
        Gdx.gl.glClearColor(0.4f, 0.4f, 0.4f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        actualizarLogica(Gdx.graphics.deltaTime)
        dibujarMundo()
    }

    private fun actualizarLogica(delta: Float) {
        if (Gdx.input.justTouched()) {
            if (gameState == 0) gameState = 1
            if (gameState == 1) velocidadY = impulsoSalto
            if (gameState == 2) inicializarMundo()
        }

        if (gameState != 1) return

        // Física de salto
        velocidadY += gravedad * delta
        rectJugador.y += velocidadY * delta

        // Colisión Suelo/Techo
        if (rectJugador.y <= 0 || rectJugador.y > altoPantalla - rectJugador.height) {
            finalizarJuego()
        }

        // Generar Obstáculos cada 1.5 segundos
        if (TimeUtils.nanoTime() - ultimoTiempoObstaculo > 1500000000) generarObstaculo()

        val iter = obstaculos.iterator()
        while (iter.hasNext()) {
            val obs = iter.next()
            obs.x -= VELOCIDAD_OBSTACULOS * delta

            // Colisión con vehículo
            if (obs.overlaps(rectJugador)) {
                finalizarJuego()
            }

            // Sumar puntos al pasar el obstáculo
            if (obs.y > 0 && obs.x + obs.width < rectJugador.x && obs.x + obs.width > rectJugador.x - (VELOCIDAD_OBSTACULOS * delta)) {
                puntuacion++
            }

            if (obs.x + ANCHO_OBSTACULO < 0) iter.remove()
        }
    }

    private fun dibujarMundo() {
        batch.begin()

        // 1. Dibujar Obstáculos (Rojos)
        for (obs in obstaculos) {
            batch.draw(texturaObstaculo, obs.x, obs.y, obs.width, obs.height)
        }

        // 2. Dibujar Vehículo Actual (JPG)
        batch.draw(
            texturaVehiculo,
            rectJugador.x,
            rectJugador.y,
            rectJugador.width,
            rectJugador.height
        )

        // 3. UI
        val textoUI = when (gameState) {
            0 -> "Toca para empezar ($tipo)"
            1 -> "Score: $puntuacion"
            else -> "GAME OVER\nScore: $puntuacion\nToca para reiniciar"
        }
        font.draw(batch, textoUI, 50f, altoPantalla - 50f)

        batch.end()
    }

    private fun generarObstaculo() {
        val alturaHueco = MathUtils.random(150f, altoPantalla - HUECO_OBSTACULO - 150f)
        // Obstáculo de abajo
        obstaculos.add(Rectangle(anchoPantalla, 0f, ANCHO_OBSTACULO, alturaHueco))
        // Obstáculo de arriba
        obstaculos.add(Rectangle(anchoPantalla, alturaHueco + HUECO_OBSTACULO, ANCHO_OBSTACULO, altoPantalla - alturaHueco - HUECO_OBSTACULO))
        ultimoTiempoObstaculo = TimeUtils.nanoTime()
    }

    private fun finalizarJuego() {
        gameState = 2
        // Opcional: onGameOver() si quieres volver al menú de Android
    }

    private fun crearTexturaSimple(ancho: Int, alto: Int, color: Color): Texture {
        val pixmap = Pixmap(ancho, alto, Pixmap.Format.RGBA8888)
        pixmap.setColor(color)
        pixmap.fill()
        val texture = Texture(pixmap)
        pixmap.dispose()
        return texture
    }

    override fun dispose() {
        batch.dispose()
        font.dispose()
        texturaVehiculo.dispose()
        texturaObstaculo.dispose()
    }
}