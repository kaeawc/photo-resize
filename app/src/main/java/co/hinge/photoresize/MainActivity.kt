package co.hinge.photoresize

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import timber.log.Timber
import kotlinx.android.synthetic.main.activity_main.*
import uk.co.senab.photoview.PhotoViewAttacher

class MainActivity : AppCompatActivity(), RequestListener<String, GlideDrawable> {

    lateinit var attacher: PhotoViewAttacher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val photoUrl: String = "http://res.cloudinary.com/hinge-dev/image/upload/v1490208043/plebgn18lp4a4pzllobo.jpg"

        Glide.with(baseContext)
                .load(photoUrl)
                .listener(this)
                .into(selectedPhoto)
    }

    override fun onException(e: Exception?, model: String?, target: Target<GlideDrawable>?, isFirstResource: Boolean): Boolean {
        Timber.e(e, "onException")
        return false
    }

    override fun onResourceReady(resource: GlideDrawable?, model: String?, target: Target<GlideDrawable>?, isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
        attacher = PhotoViewAttacher(selectedPhoto)
        return false
    }


    fun persistPhoto(photo: Photo, matrix: Matrix) {
        val boundingBox = calculateBoundingBox(photo, matrix) ?: return Timber.e("Not enough information available to create bounding box")
        if (boundingBox.size != 4) return Timber.e("Should have created 4 points for bounding box")
        val photoIndex = photos.indexOf(photo)
        if (photoIndex < 0 || photoIndex >= photos.size) return Timber.e("Could not find index of photo being edited")

        photos[photoIndex] = photo.copy(
                x1 = boundingBox[0],
                y1 = boundingBox[1],
                x2 = boundingBox[2],
                y2 = boundingBox[3]
        )
        hingeApi.patchMyPhotos(photos)
    }

    /**
     * Given a photo where some Zoom & Pan transformations have occurred, we need to calculate a new bounding box.
     */
    fun calculateBoundingBox(photo: Photo, matrix: Matrix): FloatArray? {
        val imageWidth = photo.width ?: return null
        val imageHeight = photo.height ?: return null
        val values: FloatArray = (0..9).map(Int::toFloat).toFloatArray()
        matrix.getValues(values)

        // Translation is always given as negative or zero we're moving the top & left
        // corner of the matrix in a negative direction. Therefore we take the absolute
        // value and bound it at zero.
        val transX = Math.max(Math.abs(values[Matrix.MTRANS_X]), 0f)
        val transY = Math.max(Math.abs(values[Matrix.MTRANS_Y]), 0f)

        // Scale goes from 1.0f up to 2.0f. Due to overshoot animations we should bound
        // the minimum scale to 1.0f.
        val scaleX = Math.max(values[Matrix.MSCALE_X], 1f)
        val scaleY = Math.max(values[Matrix.MSCALE_Y], 1f)
        val x = transX / scaleX
        val y = transY / scaleY

        // The viewable height & width is a square, and therefore are equal
        val viewableHeight = when {
            imageWidth > imageHeight -> imageHeight
            else -> imageWidth
        }
        val viewableWidth = viewableHeight

        // Divide the viewable area by the respective axis scale factor
        val scaledWidth = viewableWidth / scaleX
        val scaledHeight = viewableHeight / scaleY

        // The ratio point are given by the ratio of the actual points
        // with respect to the total image dimensions.
        val x1 = x / imageWidth
        val y1 = y / imageHeight
        val x2 = Math.min(x + scaledWidth, viewableWidth.toFloat()) / imageWidth
        val y2 = Math.min(y + scaledHeight, viewableHeight.toFloat()) / imageHeight

        // Finally we bound the coordinates within the 0.0f - 1.0f range and round them to
        // the nearest hundredth decimal place.
        return listOf(x1, y1, x2, y2)
                .map { boundCoordinate(it) }
                .map { roundToNearestHundredth(it) }
                .toFloatArray()
    }

    fun boundCoordinate(value: Float): Float {
        return Math.max(Math.min(value, 1f), 0f)
    }

    fun roundToNearestHundredth(value: Float): Float {
        return Math.round(value * 100f) / 100f
    }
}
