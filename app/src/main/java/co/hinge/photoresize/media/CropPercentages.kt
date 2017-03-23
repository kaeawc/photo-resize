package co.hinge.photoresize.media

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapResource

class CropPercentages(
        context: Context,
        val x1: Float,
        val y1: Float,
        val x2: Float,
        val y2: Float) : Transformation<Bitmap> {

    val bitmapPool: BitmapPool = Glide.get(context).bitmapPool
    var top = 0f
    var left = 0f
    var right = 0f
    var bottom = 0f
    var sourceWidth = 0
    var sourceHeight = 0
    var width = 0
    var height = 0

    override fun transform(resource: Resource<Bitmap>, outWidth: Int, outHeight: Int): Resource<Bitmap> {
        val source = resource.get()
        sourceWidth = if (width == 0) source.width else width
        sourceHeight = if (height == 0) source.height else height
        top = y1 * sourceHeight
        left = x1 * sourceWidth
        right = x2 * sourceWidth
        bottom = y2 * sourceHeight
        width = right.toInt() - left.toInt()
        height = bottom.toInt() - top.toInt()

        val config = if (source.config != null) source.config else Bitmap.Config.ARGB_8888
        var bitmap: Bitmap? = bitmapPool.get(width, height, config)
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(width, height, config)
        }

        val targetRect = RectF(left, top, right, bottom)

        val canvas = Canvas(bitmap)
        canvas.drawBitmap(source, null, targetRect, null)
        return BitmapResource.obtain(bitmap, bitmapPool)
    }

    override fun getId(): String {
        return "CropPercentages(top=$top, left=$left, width=$width, height=$height)"
    }
}
