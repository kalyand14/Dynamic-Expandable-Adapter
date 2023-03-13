package com.poc.stickyexpandableadapter.example.component

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.core.widget.NestedScrollView
import com.poc.stickyexpandableadapter.R


class StickyNestedScrollView(context: Context, attrs: AttributeSet?, defStyle: Int) :
    NestedScrollView(context, attrs, defStyle) {
    private lateinit var stickyViews: ArrayList<View>
    private var currentlyStickingView: View? = null
    private var stickyViewTopOffset = 0f
    private var stickyViewLeftOffset = 0f
    private var redirectTouchesToStickyView = false
    private var clippingToPadding = false
    private var clipToPaddingHasBeenSet = false
    private var mShadowHeight: Int
    private  var mShadowDrawable: Drawable? = null

    private val invalidateRunnable: Runnable = object : Runnable {
        override fun run() {
            currentlyStickingView?.let { view ->
                val l = getLeftForViewRelativeOnlyChild(view)
                val t = getBottomForViewRelativeOnlyChild(view)
                val r = getRightForViewRelativeOnlyChild(view)
                val b = (scrollY + (view.height + stickyViewTopOffset)) as Int
                invalidate(l, t, r, b)
            }
            postDelayed(this, 16)
        }
    }

    constructor(context: Context) : this(context, null) {}

    constructor(context: Context, attrs: AttributeSet?) : this(
        context,
        attrs,
        android.R.attr.scrollViewStyle
    ) {
    }

    /**
     * Sets the height of the shadow drawable in pixels.
     *
     * @param height
     */
    fun setShadowHeight(height: Int) {
        mShadowHeight = height
    }

    fun setup() {
        stickyViews = ArrayList<View>()
    }

    private fun getLeftForViewRelativeOnlyChild(v: View): Int {
        var v: View = v
        var left: Int = v.left
        while (v.parent !== getChildAt(0)) {
            v = v.parent as View
            left += v.left
        }
        return left
    }

    private fun getTopForViewRelativeOnlyChild(v: View): Int {
        var v: View = v
        var top: Int = v.top
        while (v.parent !== getChildAt(0)) {
            v = v.parent as View
            top += v.top
        }
        return top
    }

    private fun getRightForViewRelativeOnlyChild(v: View): Int {
        var v: View = v
        var right: Int = v.right
        while (v.parent !== getChildAt(0)) {
            v = v.parent as View
            right += v.right
        }
        return right
    }

    private fun getBottomForViewRelativeOnlyChild(v: View): Int {
        var v: View = v
        var bottom: Int = v.bottom
        while (v.parent !== getChildAt(0)) {
            v = v.parent as View
            bottom += v.bottom
        }
        return bottom
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (!clipToPaddingHasBeenSet) {
            clippingToPadding = true
        }
        notifyHierarchyChanged()
    }

    override fun setClipToPadding(clipToPadding: Boolean) {
        super.setClipToPadding(clipToPadding)
        clippingToPadding = clipToPadding
        clipToPaddingHasBeenSet = true
    }

    override fun addView(child: View) {
        super.addView(child)
        findStickyViews(child)
    }

    override fun addView(child: View, index: Int) {
        super.addView(child, index)
        findStickyViews(child)
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams?) {
        super.addView(child, index, params)
        findStickyViews(child)
    }

    override fun addView(child: View, width: Int, height: Int) {
        super.addView(child, width, height)
        findStickyViews(child)
    }

    override fun addView(child: View, params: ViewGroup.LayoutParams?) {
        super.addView(child, params)
        findStickyViews(child)
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)

        currentlyStickingView?.let { currentlyStickingView ->
            canvas.save()
            canvas.translate(
                paddingLeft + stickyViewLeftOffset,
                scrollY + stickyViewTopOffset + if (clippingToPadding) paddingTop else 0
            )

            canvas.clipRect(
                0f,
                if (clippingToPadding) -(stickyViewTopOffset) else 0f,
                width - stickyViewLeftOffset,
                currentlyStickingView.height + mShadowHeight + 1f
            )
            mShadowDrawable?.let { drawable ->
                val left = 0
                val right: Int = currentlyStickingView.width
                val top: Int = currentlyStickingView.height
                val bottom: Int = currentlyStickingView.height + mShadowHeight
                drawable.setBounds(left, top, right, bottom)
                drawable.draw(canvas)
            }

            canvas.clipRect(
                0f,
                if (clippingToPadding) -(stickyViewTopOffset) else 0f,
                width.toFloat(),
                currentlyStickingView.height.toFloat()
            )
            if (getStringTagForView(currentlyStickingView).contains(FLAG_HASTRANSPARANCY)) {
                showView(currentlyStickingView)
                currentlyStickingView.draw(canvas)
                hideView(currentlyStickingView)
            } else {
                currentlyStickingView.draw(canvas)
            }
            canvas.restore()
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            redirectTouchesToStickyView = true
        }

        if (redirectTouchesToStickyView) {
            redirectTouchesToStickyView = currentlyStickingView != null

            if (redirectTouchesToStickyView) {
                redirectTouchesToStickyView =
                    ev.y <= currentlyStickingView!!.height + stickyViewTopOffset && ev.x >=
                            getLeftForViewRelativeOnlyChild(
                                currentlyStickingView!!
                            ) && ev.x <= getRightForViewRelativeOnlyChild(currentlyStickingView!!)
            }
        } else if (currentlyStickingView == null) {
            redirectTouchesToStickyView = false
        }
        if (redirectTouchesToStickyView) {
            ev.offsetLocation(
                0f,
                -1 * (scrollY + stickyViewTopOffset - getTopForViewRelativeOnlyChild(
                    currentlyStickingView!!
                ))
            )
        }
        return super.dispatchTouchEvent(ev)
    }

    private var hasNotDoneActionDown = true

    init {
        setup()
        val a: TypedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.StickyNestedScrollView, defStyle, 0
        )
        val density: Float = context.getResources().getDisplayMetrics().density
        val defaultShadowHeightInPix = (DEFAULT_SHADOW_HEIGHT * density + 0.5f).toInt()
        mShadowHeight = a.getDimensionPixelSize(
            R.styleable.StickyNestedScrollView_stuckShadowHeight,
            defaultShadowHeightInPix
        )
        val shadowDrawableRes = a.getResourceId(
            R.styleable.StickyNestedScrollView_stuckShadowDrawable, -1
        )
        if (shadowDrawableRes != -1) {
            mShadowDrawable = context.getResources().getDrawable(
                shadowDrawableRes
            )
        }
        a.recycle()
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (redirectTouchesToStickyView) {
            ev.offsetLocation(
                0f,
                scrollY + stickyViewTopOffset - getTopForViewRelativeOnlyChild
                    (currentlyStickingView!!)
            )
        }
        if (ev.action == MotionEvent.ACTION_DOWN) {
            hasNotDoneActionDown = false
        }
        if (hasNotDoneActionDown) {
            val down = MotionEvent.obtain(ev)
            down.action = MotionEvent.ACTION_DOWN
            super.onTouchEvent(down)
            hasNotDoneActionDown = false
        }
        if (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_CANCEL) {
            hasNotDoneActionDown = true
        }
        return super.onTouchEvent(ev)
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        doTheStickyThing()
    }

    private fun doTheStickyThing() {
        var viewThatShouldStick: View? = null
        var approachingView: View? = null
        for (v in stickyViews) {
            val viewTop =
                getTopForViewRelativeOnlyChild(v) - scrollY + if (clippingToPadding) 0 else paddingTop
            if (viewTop <= 0) {
                if (viewThatShouldStick == null || viewTop > getTopForViewRelativeOnlyChild(
                        viewThatShouldStick
                    ) - scrollY + if (clippingToPadding) 0 else paddingTop
                ) {
                    viewThatShouldStick = v
                }
            } else {
                if (approachingView == null || viewTop < getTopForViewRelativeOnlyChild(
                        approachingView
                    ) - scrollY + if (clippingToPadding) 0 else paddingTop
                ) {
                    approachingView = v
                }
            }
        }
        if (viewThatShouldStick != null) {
            stickyViewTopOffset = if (approachingView == null) 0f else Math.min(
                0,
                getTopForViewRelativeOnlyChild(approachingView) - scrollY + (if (clippingToPadding) 0 else paddingTop) - viewThatShouldStick.getHeight()
            )
                .toFloat()
            if (viewThatShouldStick !== currentlyStickingView) {
                if (currentlyStickingView != null) {
                    stopStickingCurrentlyStickingView()
                }
                // only compute the left offset when we start sticking.
                stickyViewLeftOffset =
                    getLeftForViewRelativeOnlyChild(viewThatShouldStick).toFloat()
                startStickingView(viewThatShouldStick)
            }
        } else if (currentlyStickingView != null) {
            stopStickingCurrentlyStickingView()
        }
    }

    private fun startStickingView(viewThatShouldStick: View) {
        currentlyStickingView = viewThatShouldStick

        currentlyStickingView?.let { view ->
            if (getStringTagForView(view).contains(FLAG_HASTRANSPARANCY)) {
                hideView(view)
            }
            if ((view.getTag() as String).contains(FLAG_NONCONSTANT)) {
                post(invalidateRunnable)
            }
        }

    }

    private fun stopStickingCurrentlyStickingView() {
        currentlyStickingView?.let { view ->
            if (getStringTagForView(view).contains(FLAG_HASTRANSPARANCY)) {
                showView(view)
            }
        }
        currentlyStickingView = null
        removeCallbacks(invalidateRunnable)
    }

    /**
     * Notify that the sticky attribute has been added or removed from one or more views in the View hierarchy
     */
    fun notifyStickyAttributeChanged() {
        notifyHierarchyChanged()
    }

    private fun notifyHierarchyChanged() {
        if (currentlyStickingView != null) {
            stopStickingCurrentlyStickingView()
        }
        stickyViews!!.clear()
        findStickyViews(getChildAt(0))
        doTheStickyThing()
        invalidate()
    }

    private fun findStickyViews(v: View) {
        if (v is ViewGroup) {
            val vg = v as ViewGroup
            for (i in 0 until vg.childCount) {
                val tag = getStringTagForView(vg.getChildAt(i))
                if (tag != null && tag.contains(STICKY_TAG)) {
                    stickyViews!!.add(vg.getChildAt(i))
                } else if (vg.getChildAt(i) is ViewGroup) {
                    findStickyViews(vg.getChildAt(i))
                }
            }
        } else {
            val tag = v.getTag() as String
            if (tag != null && tag.contains(STICKY_TAG)) {
                stickyViews!!.add(v)
            }
        }
    }

    private fun getStringTagForView(v: View): String {
        val tagObject: Any = v?.getTag() ?: ""
        return tagObject.toString()
    }

    private fun hideView(v: View) {
        if (Build.VERSION.SDK_INT >= 11) {
            v.setAlpha(0f)
        } else {
            val anim = AlphaAnimation(1f, 0f)
            anim.duration = 0
            anim.fillAfter = true
            v.startAnimation(anim)
        }
    }

    private fun showView(v: View) {
        if (Build.VERSION.SDK_INT >= 11) {
            v.setAlpha(1f)
        } else {
            val anim = AlphaAnimation(0f, 1f)
            anim.duration = 0
            anim.fillAfter = true
            v.startAnimation(anim)
        }
    }

    companion object {
        /**
         * Tag for views that should stick and have constant drawing. e.g. TextViews, ImageViews etc
         */
        const val STICKY_TAG = "sticky"

        /**
         * Flag for views that should stick and have non-constant drawing. e.g. Buttons, ProgressBars etc
         */
        const val FLAG_NONCONSTANT = "-nonconstant"

        /**
         * Flag for views that have aren't fully opaque
         */
        const val FLAG_HASTRANSPARANCY = "-hastransparancy"

        /**
         * Default height of the shadow peeking out below the stuck view.
         */
        private const val DEFAULT_SHADOW_HEIGHT = 10 // dp;
    }
}