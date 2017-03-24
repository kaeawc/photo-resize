package co.hinge.photoresize.resize

import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Matrix
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.view.View
import android.view.ViewTreeObserver
import android.view.Window
import co.hinge.photoresize.models.Photo
import co.hinge.photoresize.R
import co.hinge.photoresize.main.MainActivity
import co.hinge.photoresize.storage.Prefs
import co.hinge.photoresize.storage.Storage
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.activity_resize.*
import timber.log.Timber
import uk.co.senab.photoview.PhotoViewAttacher
import java.lang.reflect.Field
import android.transition.Explode
import android.transition.Fade


class ResizeActivity : AppCompatActivity(), ResizePresenter.ResizeView {

    lateinit var attacher: PhotoViewAttacher
    lateinit var presenter: ResizePresenter
    lateinit var photo: Photo

    var scale: Float = 1f
    var translateX: Float = 0f
    var translateY: Float = 0f
    val matrix = Matrix()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resize)

        if (sharedElementTransitions()) {
            postponeEnterTransition()

            val decor = window.decorView
            decor.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    decor.viewTreeObserver.removeOnPreDrawListener(this)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        startPostponedEnterTransition()
                    }
                    return true
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        presenter = ResizePresenter(Storage(Prefs(baseContext.getSharedPreferences("app", Context.MODE_PRIVATE))))
        presenter.setView(this)
    }

    fun sharedElementTransitions(): Boolean {
//        return false
         return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    }

    override fun onBackPressed() {
        if (transitionPhoto.visibility == View.GONE) {
            val width = selectedPhoto.width
            val height = selectedPhoto.height
            presenter.onExit(photo, width, height, selectedPhoto.imageMatrix)
        } else {
            val width = transitionPhoto.width
            val height = transitionPhoto.height
            presenter.onExit(photo, width, height, transitionPhoto.imageMatrix)
        }
    }

    override fun returnUpdatedPhoto(photo: Photo) {

        if (!sharedElementTransitions()) {
            super.onBackPressed()
        } else {
            val resultIntent = Intent()
            resultIntent.putExtra("url", photo.url)
            resultIntent.putExtra("width", photo.width)
            resultIntent.putExtra("height", photo.height)
            resultIntent.putExtra("x1", photo.x1)
            resultIntent.putExtra("y1", photo.y1)
            resultIntent.putExtra("x2", photo.x2)
            resultIntent.putExtra("y2", photo.y2)
            setResult(Activity.RESULT_OK, resultIntent)

            if (transitionPhoto.visibility == View.GONE) {
                transitionPhoto.imageMatrix = attacher.imageView.imageMatrix
                transitionPhoto.visibility = View.VISIBLE
                selectedPhoto.visibility = View.GONE
            }

            finish()
        }
    }

    override fun showPhoto(photo: Photo) {

        this.photo = photo

        scale = Math.min(Math.max(1f / (photo.x2 - photo.x1), 0f), PhotoViewAttacher.DEFAULT_MAX_SCALE)
        translateX = -(photo.width * photo.x1)
        translateY = -(photo.height * photo.y1)

        Glide.with(baseContext)
                .load(photo.url)
                .listener(object: RequestListener<String, GlideDrawable> {
                    override fun onException(e: Exception?, model: String?, target: Target<GlideDrawable>?, isFirstResource: Boolean): Boolean {
                        Timber.e(e, "onException")
                        return false
                    }

                    override fun onResourceReady(resource: GlideDrawable?, model: String?, target: Target<GlideDrawable>?, isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
                        matrix.reset()
                        matrix.postTranslate(translateX, translateY)
                        matrix.postScale(scale, scale, 0f, 0f)
                        transitionPhoto.imageMatrix = matrix
                        return false
                    }
                })
                .into(transitionPhoto)

        Glide.with(baseContext)
                .load(photo.url)
                .listener(object: RequestListener<String, GlideDrawable> {
                    override fun onException(e: Exception?, model: String?, target: Target<GlideDrawable>?, isFirstResource: Boolean): Boolean {
                        Timber.e(e, "onException")
                        return false
                    }

                    override fun onResourceReady(resource: GlideDrawable?, model: String?, target: Target<GlideDrawable>?, isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
                        attacher = PhotoViewAttacher(selectedPhoto)

                        selectedPhoto.postDelayed({
                            matrix.reset()
                            matrix.postTranslate(translateX, translateY)
                            matrix.postScale(scale, scale, 0f, 0f)
                            selectedPhoto.imageMatrix = matrix
//                            setPhotoViewMatrix(matrix, "mBaseMatrix")
//                            setPhotoViewMatrix(matrix, "mDrawMatrix")
                            setPhotoViewMatrix(matrix, "mSuppMatrix")
                            transitionPhoto.visibility = View.GONE
                        }, 500)

                        return false
                    }
                })
                .into(selectedPhoto)
    }


    fun setPhotoViewMatrix(matrix: Matrix, fieldName: String): Boolean {
        return try {
            val field: Field = attacher.javaClass.getDeclaredField(fieldName)
            field.isAccessible = true
            field.set(attacher, matrix)
            field.isAccessible = false
            Timber.i("Successfully set PhotoViewMatrix")
            true
        } catch (ex: NoSuchFieldException) {
            Timber.e(ex, "Could not set PhotoViewMatrix")
            false
        }
    }
}
