package io.kaeawc.photoresize.resize

import android.content.Context
import android.graphics.Matrix
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import io.kaeawc.photoresize.R
import io.kaeawc.photoresize.main.MainActivity
import io.kaeawc.photoresize.models.Photo
import io.kaeawc.photoresize.storage.Prefs
import io.kaeawc.photoresize.storage.Storage
import kotlinx.android.synthetic.main.activity_resize.*
import timber.log.Timber
import uk.co.senab.photoview.PhotoViewAttacher
import java.lang.reflect.Field

class ResizeFragment : Fragment(), ResizePresenter.ResizeView {

    lateinit var attacher: PhotoViewAttacher
    lateinit var presenter: ResizePresenter
    lateinit var photo: Photo

    var scale: Float = 1f
    var translateX: Float = 0f
    var translateY: Float = 0f
    val matrix = Matrix()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postponeEnterTransition()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_resize, container, false)
        view.viewTreeObserver.addOnPreDrawListener(object: ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                view.viewTreeObserver.removeOnPreDrawListener(this)
                activity.startPostponedEnterTransition()
                return true
            }
        })

        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        presenter = ResizePresenter(Storage(Prefs(context.getSharedPreferences("app", Context.MODE_PRIVATE))))
        presenter.setView(this)
    }


    fun sharedElementTransitions(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    }

    override fun onBackPressed() {
        if (!isVisible) return

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

        if (sharedElementTransitions()) {

            if (transitionPhoto.visibility == View.GONE) {
                transitionPhoto.imageMatrix = attacher.imageView.imageMatrix
                transitionPhoto.visibility = View.VISIBLE
                selectedPhoto.visibility = View.GONE
            }

            val activity = activity
            if (activity is MainActivity) {
                activity.popBackStack()
            }
        }
    }

    override fun showPhoto(photo: Photo) {

        this.photo = photo

        scale = Math.min(Math.max(1f / (photo.x2 - photo.x1), 0f), PhotoViewAttacher.DEFAULT_MAX_SCALE)
        translateX = -(photo.width * photo.x1)
        translateY = -(photo.height * photo.y1)

        Glide.with(context)
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

        Glide.with(context)
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
            true
        } catch (ex: NoSuchFieldException) {
            Timber.e(ex, "Could not set PhotoViewMatrix")
            false
        }
    }
}
