package co.hinge.photoresize.media

import android.content.Context
import android.support.annotation.DrawableRes
import android.util.DisplayMetrics
import android.widget.ImageView
import com.bumptech.glide.Glide

class CloudinaryImage(displayMetrics: DisplayMetrics) {

    companion object {
        const val LARGE_WIDTH = 1055
        const val MEDIUM_WIDTH = 956
        const val SMALL_WIDTH = 637

        const val PARAMS_PREFIX = "image/upload"
    }

    val density = displayMetrics.density
    val width = when {
        density >= DisplayMetrics.DENSITY_XXHIGH -> LARGE_WIDTH
        density >= DisplayMetrics.DENSITY_XHIGH -> MEDIUM_WIDTH
        density < DisplayMetrics.DENSITY_XHIGH -> SMALL_WIDTH
        else -> SMALL_WIDTH
    }

    fun loadPreCropped(
            context: Context,
            url: String,
            x1: Float,
            y1: Float,
            x2: Float,
            y2: Float,
            imageView: ImageView,
            @DrawableRes placeholder: Int? = null) {

        val insert = "x_$x1,y_$y1,w_$x2,h_$y2,c_crop/w_$width,f_auto"
        val urlWithParams = url.replace(PARAMS_PREFIX, "${PARAMS_PREFIX}/$insert")
        val builder = Glide.with(context)
                .load(urlWithParams)

        if (placeholder != null) {
            builder.placeholder(placeholder)
        }

        builder.into(imageView)
    }

    fun load(
            context: Context,
            url: String,
            imageView: ImageView,
            @DrawableRes placeholder: Int? = null) {

        val insert = "w_$width,f_auto"
        val urlWithParams = url.replace(PARAMS_PREFIX, "${PARAMS_PREFIX}/$insert")
        val builder = Glide.with(context)
                .load(urlWithParams)

        if (placeholder != null) {
            builder.placeholder(placeholder)
        }

        builder.into(imageView)
    }
}
