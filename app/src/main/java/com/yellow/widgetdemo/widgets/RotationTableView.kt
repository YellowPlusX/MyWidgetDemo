package com.yellow.widgetdemo.widgets

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import com.yellow.widgetdemo.utils.LogUtil
import com.yellow.widgetdemo.utils.ScreenUtils
import kotlin.math.atan
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Created by Freeman on 2020-03-25
 */
class RotationTableView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "RotationTableView"

        private const val DURATION_ROTATION = 800L
        private const val CIRCLE_DEGREE = 360f
        private const val ONE_THIRD_CIRCLE_DEGREE = 270f // 1/3 circle degree
        private const val ONE_EIGHT_CIRCLE_DEGREE: Double = 45.0 // 1/8 circle degree
        private const val AWARD_ICON_ROTATION = 135f // 90 + 45

        private const val HALF_CIRCLE = 180f
    }

    private var panelClickEnabled: Boolean = true

    private var autoRotationAnim: ObjectAnimator? = null

    var onPanelColorCreator: ((radius: Float) -> Unit)? = null

    private var selectedListener: OnSelectedListener? = null

    private var panelClickListener: OnPanelClickListener? = null

    // The view is a square, width equals height.
    private var size = 0
    private var centerX = 0f
    private var centerY = 0f
    private var radius = 0f

    private var sweepAngle = 0f // The angle of the arc panel
    private var currentDegree = 0f
    private var offsetDegree = 0f

    private var textSizeVOffset = 0f

    private var tablePanelList = mutableListOf<TablePanelEntity>()

    private val tablePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val iconPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var specialPanelColor: RadialGradient? = null
    private var tablePanelColor: Array<RadialGradient>? = null

    private var selectedPosition: Int = -1

    private var iconOffsetY = 0f

    init {
        tablePaint.style = Paint.Style.FILL
        tablePaint.strokeWidth = 20f

        textPaint.color = ContextCompat.getColor(context, android.R.color.white)
        textPaint.style = Paint.Style.FILL
        textPaint.strokeWidth = 20f
        textPaint.textSize = ScreenUtils.dip2px(context, 12f).toFloat()
        textSizeVOffset = ScreenUtils.dip2px(context, 35f).toFloat()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // medium
            textPaint.typeface = Typeface.create(Typeface.SANS_SERIF, 500, false)
        } else {
            textPaint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            setLayerType(LAYER_TYPE_SOFTWARE, null)
        }
    }

    fun <T> setTablePanelList(awardList: List<T>, panelDataAdapter: TablePanelAdapter<T>) {
        tablePanelList.clear()

        // convert data
        awardList.forEachIndexed { index, data ->
            val tablePanelEntity = TablePanelEntity()
            tablePanelEntity.text = panelDataAdapter.getText(data, index)
            tablePanelEntity.iconId = panelDataAdapter.getIconId(data, index)
            tablePanelList.add(tablePanelEntity)
        }
        invalidate()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val viewWidth = right - left
        val viewHeight = bottom - top
        centerX = viewWidth / 2f
        centerY = viewHeight / 2f
        size = min(viewWidth, viewHeight)
        radius = size / 2f
        onPanelColorCreator?.invoke(radius)
    }

    fun initPayTableColor(
        specialPanelColor: RadialGradient?, tablePanelColor: Array<RadialGradient>?
    ) {
        this.specialPanelColor = specialPanelColor
        this.tablePanelColor = tablePanelColor
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (size == 0) {
            return
        }
        if (tablePanelList.isNullOrEmpty()) {
            return
        }

        drawCanvas(canvas)
    }

    private fun drawCanvas(canvas: Canvas) {
        // draw the arc panel
        val tablePanelCount = tablePanelList.size
        sweepAngle = CIRCLE_DEGREE / tablePanelCount

        // make the pointer point to the middle of the arc panel
        canvas.translate(centerX, centerY)
        canvas.rotate(ONE_THIRD_CIRCLE_DEGREE + sweepAngle / 2, 0f, 0f)

        val drawRect = RectF(-radius, -radius, radius, radius)
        var startAngle = 0f
        for (i in 1..tablePanelCount) {
            if (i == tablePanelCount && specialPanelColor != null) {
                tablePaint.shader = specialPanelColor
            } else {
                tablePanelColor?.apply {
                    tablePaint.shader = get(i % size)
                }
            }
            canvas.drawArc(drawRect, startAngle, sweepAngle, true, tablePaint)
            startAngle += sweepAngle
        }
        canvas.save()
        canvas.rotate(-sweepAngle / 2 - ONE_EIGHT_CIRCLE_DEGREE.toFloat(), 0f, 0f)

        // draw award icon and text
        val anchorCenter = (radius * sin(ONE_EIGHT_CIRCLE_DEGREE) / 2).toFloat()
        for (i in 0 until tablePanelCount) {
            canvas.save()
            canvas.rotate(sweepAngle * i, 0f, 0f)
            val panelEntity = tablePanelList!![i]
            val arcPanelCenter = if (panelEntity.text == "") {
                anchorCenter - iconOffsetY * .25f
            } else {
                anchorCenter + iconOffsetY
            }
            val prizeDrawableId = panelEntity.iconId
            // read only bitmap
            val baseBitmap = BitmapFactory.decodeResource(resources, prizeDrawableId)
            val awardRectF = RectF(
                arcPanelCenter - baseBitmap.width.toFloat() / 2,
                arcPanelCenter - baseBitmap.height.toFloat() / 2,
                arcPanelCenter + baseBitmap.width.toFloat() / 2,
                arcPanelCenter + baseBitmap.height.toFloat() / 2
            )
            val matrix = Matrix()
            matrix.setRotate(
                AWARD_ICON_ROTATION,
                baseBitmap.width.toFloat() / 2,
                baseBitmap.height.toFloat() / 2
            )
            // target mutable bitmap to rotate
            val awardBitmap = Bitmap.createBitmap(
                baseBitmap, 0, 0, baseBitmap.width, baseBitmap.height, matrix, true
            )
            canvas.drawBitmap(awardBitmap, null, awardRectF, iconPaint)
            baseBitmap.recycle()
            awardBitmap.recycle()
            drawText(canvas, panelEntity.text)
            canvas.restore()
        }
        canvas.restore()
    }

    private fun drawText(canvas: Canvas, text: String) {
        val path = Path()
        // We had translated the center before.
        val rectF = RectF(-radius, -radius, radius, radius)
        val arcLength = (2 * Math.PI * radius * sweepAngle / CIRCLE_DEGREE).toFloat()
        val textRect = Rect()
        textPaint.getTextBounds(text, 0, text.length, textRect)
        path.addArc(rectF, (ONE_EIGHT_CIRCLE_DEGREE - sweepAngle / 2).toFloat(), sweepAngle)
        canvas.drawTextOnPath(
            text,
            path,
            (arcLength - textRect.width()) / 2,
            textSizeVOffset,
            textPaint
        )
    }

    fun startRotate(): Boolean {
        LogUtil.i(TAG, "init degree is $rotation")
        if (tablePanelList.isNullOrEmpty()) {
            LogUtil.i(TAG, "startRotate Error!!, current rotation table is empty")
            return false
        }
        autoRotationAnim = ObjectAnimator.ofFloat(
            this,
            "rotation",
            rotation,
            rotation + CIRCLE_DEGREE * 2).apply {
            duration = DURATION_ROTATION
            repeatMode = ValueAnimator.RESTART
            repeatCount = Animation.INFINITE
            interpolator = LinearInterpolator()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animator: Animator?) {
                    LogUtil.i(TAG, "startRotate onAnimationEnd = $rotation")
                    // Avoid the rotation too large to stack over flow.
                    rotation = (rotation + CIRCLE_DEGREE) % 360
                    goTargetAward()
                }
            })
            addUpdateListener {
                currentDegree = it.animatedValue as Float
            }
            start()
        }
        return true
    }

    fun restore() {
        this.offsetDegree = -1f
        selectedPosition = -1
        stopAutoRotate()
    }

    fun setDrawResultIndex(prizeInfoIndex: Int) {
        offsetDegree = sweepAngle * prizeInfoIndex
        selectedPosition = prizeInfoIndex
        stopAutoRotate()
    }

    private fun goTargetAward() {
        // start the award animation to specified position
        val degreeOffset = if (offsetDegree == -1f) { // restore
            CIRCLE_DEGREE
        } else {
            /**
             * CIRCLE_DEGREE - rotation，开始旋转的角度先转到360°，回到原位
             * CIRCLE_DEGREE - offsetDegree，旋转到指定奖品的角度
             */
            CIRCLE_DEGREE - rotation + CIRCLE_DEGREE - offsetDegree
        }
        val selectedDuration = (degreeOffset / CIRCLE_DEGREE * DURATION_ROTATION).toLong()
        ObjectAnimator.ofFloat(this, "rotation", rotation, rotation + degreeOffset).apply {
            duration = selectedDuration
            repeatCount = 0
            interpolator = LinearInterpolator()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(p0: Animator?) {
                    doShakeAnim(degreeOffset, selectedDuration)
                }
            })
            start()
        }
    }

    private fun doShakeAnim(degreeOffset: Float, selectedDuration: Long) {
        // do shake animation
        ObjectAnimator.ofFloat(
            this@RotationTableView,
            "rotation",
            rotation,
            rotation + sweepAngle * .8f).apply {
            duration = (sweepAngle * .8f / degreeOffset * selectedDuration * 3f).toLong()
            repeatCount = 1
            repeatMode = ValueAnimator.REVERSE
            interpolator = LinearInterpolator()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(p0: Animator?) {
                    clearAnimation()
                    animate().setListener(null)
                    LogUtil.i(TAG, "The final rotation degree is : $rotation")
                    selectedListener?.onSelected(selectedPosition)
                }
            })
            start()
        }
    }

    fun release() {
        animate().setListener(null)
        clearAnimation()
    }

    fun setOnSelectedListener(selectedListener: OnSelectedListener) {
        this.selectedListener = selectedListener
    }

    interface OnSelectedListener {
        fun onSelected(selectedPosition: Int)
    }

    fun setOnPanelClickListener(panelClickListener: OnPanelClickListener) {
        this.panelClickListener = panelClickListener
    }

    interface OnPanelClickListener {
        fun onPanelClicked(position: Int)
    }

    private fun stopAutoRotate() {
        LogUtil.i(TAG, "stopAutoRotate")
        autoRotationAnim?.repeatCount = Animation.ABSOLUTE
    }

    fun setPanelClickEnabled(enabled: Boolean) {
        panelClickEnabled = enabled
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) {
            return super.onTouchEvent(event)
        } else {
            LogUtil.i(TAG, "event.action = ${event.action}")
            when (event.action) {
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (panelClickEnabled && inRange(event)) {
                        onPanelClick(event)
                        return true
                    }
                    return super.onTouchEvent(event)
                }
            }
            return super.onTouchEvent(event)
        }
    }

    private fun inRange(event: MotionEvent): Boolean {
        LogUtil.i(TAG, "x = ${event.x}, y = ${event.y}")
        // not in the rotation table range circle.
        if (sqrt((event.x - centerX).pow(2) + (event.y - centerY).pow(2)) > radius) {
            return false
        }
        LogUtil.i(TAG, "in the circle !!!")
        return true
    }

    private fun onPanelClick(event: MotionEvent) {
        val translateX = event.x - centerX
        val translateY = event.y - centerY
        var position: Int
        if (tablePanelList.isNullOrEmpty() || sweepAngle == 0f) {
            return
        }
        val dataSize = tablePanelList!!.size
        if (translateX == 0f) {
            position = if (translateY > 0) { // middle bottom,the middle one
                tablePanelList!!.size / 2
            } else { // middle top,the last one
                0
            }
            panelClickListener?.onPanelClicked(position)
            return
        }
        var degree = atan(translateY / translateX) * 180 / Math.PI
        if (degree < 0f) { // second quadrant
            degree += HALF_CIRCLE
        }
        if (translateY < 0) { // third and forth quadrant
            degree += HALF_CIRCLE
        }
        if (degree <= sweepAngle / 2 || degree >= CIRCLE_DEGREE - sweepAngle / 2) {
            position = 2
        } else {
            position = ((degree - sweepAngle / 2) / sweepAngle).toInt() + 3
            if (position >= dataSize) {
                position -= dataSize
            }
        }
        panelClickListener?.onPanelClicked(position)
    }

    interface TablePanelAdapter<T> {
        fun getText(data: T, position: Int): String
        fun getIconId(data: T, position: Int): Int
    }

    private class TablePanelEntity {
        var text: String = ""
        var iconId: Int = 0
    }

    fun setTextSize(dp: Float) {
        textPaint.textSize = ScreenUtils.dip2px(context, dp).toFloat()
    }

    fun setLetterSpacing(spacing: Float) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return
        }
        textPaint.letterSpacing = spacing
    }

    fun setIconOffset(offsetYPx: Float) {
        iconOffsetY = offsetYPx
    }

    fun setTextOffset(offsetYDp: Float) {
        textSizeVOffset = ScreenUtils.dip2px(context, offsetYDp).toFloat()
    }

    fun setTextDyShadow(dy: Float) {
        // Yes,it is correct!The dy is used in dx owing to the text painting direction is vertical
        textPaint.setShadowLayer(1f, -dy, 0f, Color.parseColor("#7F000000"))
    }
}