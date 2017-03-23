package co.hinge.photoresize.resize

import android.graphics.Matrix
import co.hinge.photoresize.models.Photo
import co.hinge.photoresize.storage.Storage
import timber.log.Timber
import java.lang.ref.WeakReference

open class ResizeInteractor(open val storage: Storage) {

    var weakViewModel: WeakReference<ResizeViewModel>? = null

    open fun setViewModel(viewModel: ResizeViewModel) {
        weakViewModel = WeakReference(viewModel)
    }

    open fun destroy() {
        weakViewModel?.clear()
    }

    open fun requestPhoto() {
        val viewModel = weakViewModel?.get() ?: return
        viewModel.onPhotoLoaded(storage.getPhoto())
    }

    open fun persistPhoto(photo: Photo, matrix: Matrix) {
        val boundingBox = calculateBoundingBox(photo, matrix) ?: return Timber.e("Not enough information available to create bounding box")
        if (boundingBox.size != 4) return Timber.e("Should have created 4 points for bounding box")

        storage.x1 = boundingBox[0]
        storage.y1 = boundingBox[1]
        storage.x2 = boundingBox[2]
        storage.y2 = boundingBox[3]
    }

    /**
     * Given a photo where some Zoom & Pan transformations have occurred, we need to calculate a new bounding box.
     */
    open fun calculateBoundingBox(photo: Photo, matrix: Matrix): FloatArray? {
        val imageWidth = photo.width
        val imageHeight = photo.height
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

    open fun boundCoordinate(value: Float): Float {
        return Math.max(Math.min(value, 1f), 0f)
    }

    open fun roundToNearestHundredth(value: Float): Float {
        return Math.round(value * 100f) / 100f
    }

    interface ResizeViewModel {
        fun onPhotoLoaded(photo: Photo)
    }
}
