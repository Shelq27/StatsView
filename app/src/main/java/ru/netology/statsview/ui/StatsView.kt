package ru.netology.statsview.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.withStyledAttributes
import ru.netology.statsview.R
import ru.netology.statsview.utils.AndroidUtils
import kotlin.math.min
import kotlin.random.Random


class StatsView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,

    ) : View(
    context,
    attributeSet,
    defStyleAttr,
    defStyleRes
) {
    private var lineWith = AndroidUtils.dp(context, 5)
    private var colors = emptyList<Int>()
    private var radius = 0F
    private var center = PointF()
    private var oval = RectF()
    private var progress = 0F
    private var valueAnimator: ValueAnimator? = null


    init {
        context.withStyledAttributes(attributeSet, R.styleable.StatsView) {
            lineWith = getDimension(R.styleable.StatsView_lineWidth, lineWith.toFloat()).toInt()
            colors = listOf(
                getColor(R.styleable.StatsView_color1, generateRandomColor()),
                getColor(R.styleable.StatsView_color2, generateRandomColor()),
                getColor(R.styleable.StatsView_color3, generateRandomColor()),
                getColor(R.styleable.StatsView_color4, generateRandomColor())
            )
        }
    }

    private var paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = lineWith.toFloat()
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }


    var data: List<Float> = emptyList()
        set(value) {
            field = value
            update()
        }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = min(w, h) / 2F - lineWith
        center = PointF(w / 2F, h / 2F)
        oval = RectF(
            center.x - radius,
            center.y - radius,
            center.x + radius,
            center.y + radius,
        )

    }

    override fun onDraw(canvas: Canvas) {

        //Светло Серый круг
        paint.color = 0xFFD3D3D3.toInt()
        canvas.drawCircle(center.x, center.y, radius, paint)

        if (data.isEmpty()) {
            return
        }
        var startAngle = -90F
        val maxDatum = data.sum()
        val fillStatsView = (data.lastIndex + 1) * 0.25F
        data.forEachIndexed { index, datum ->

         

            val angle = 360F * (datum / maxDatum) * fillStatsView

            paint.color = colors.getOrElse(index) { generateRandomColor() }
            canvas.drawArc(oval, startAngle, angle * progress, false, paint)
            startAngle += angle

        }

        //Повтор 1 дуги
//        startAngle=-90F
//        paint.color = (colors[0])
//        canvas.drawArc(oval, startAngle, data[0] * 0.18F, false, paint)


//        startAngle=-90F
//        paint.color = (colors[0])
//        canvas.drawArc(oval, startAngle, data[0]/maxDatum, false, paint)




    }

    private fun update() {
        valueAnimator?.let {
            it.removeAllListeners()
            it.cancel()
        }
        progress = 0F
        valueAnimator = ValueAnimator.ofFloat(0F, 1F).apply {
            addUpdateListener { anim ->
                progress = anim.animatedValue as Float
                invalidate()
            }
            duration = 2000
            interpolator = LinearInterpolator()
        }.also {
            it.start()
        }
    }

    private fun generateRandomColor() = Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())
}