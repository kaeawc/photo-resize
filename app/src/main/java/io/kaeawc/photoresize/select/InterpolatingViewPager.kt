package io.kaeawc.photoresize.select

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.animation.DecelerateInterpolator
import android.widget.Scroller

open class InterpolatingViewPager : ViewPager {

    companion object {
        const val DEFAULT_SCROLL_DURATION = 400
        const val FAST_SCROLL_DURATION = 200
    }

    constructor(context: Context) : super(context) {
        setupScroller()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setupScroller()
    }

    private fun setupScroller() {

        if (isInEditMode) {
            return
        }

        setScrollSpeed(DEFAULT_SCROLL_DURATION)
    }

    fun setScrollSpeed(scrollDuration: Int) {
        val viewpager = ViewPager::class.java
        val scroller = viewpager.getDeclaredField("mScroller")
        scroller.isAccessible = true
        scroller.set(this, SmoothScroller(context, scrollDuration))
        scroller.isAccessible = false
    }

    private class SmoothScroller(context: Context?, val scrollDuration: Int) : Scroller(context, DecelerateInterpolator()) {

        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
            super.startScroll(startX, startY, dx, dy, scrollDuration)
        }
    }
}
