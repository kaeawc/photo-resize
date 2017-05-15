package io.kaeawc.photoresize.resize

import android.content.Context
import android.graphics.Matrix
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.chrisbanes.photoview.PhotoViewAttacher
import io.kaeawc.photoresize.R
import io.kaeawc.photoresize.main.MainActivity
import io.kaeawc.photoresize.models.Photo
import io.kaeawc.photoresize.select.PhotoSelectSources
import io.kaeawc.photoresize.select.PhotoSelectViewPagerAdapter
import io.kaeawc.photoresize.storage.Prefs
import io.kaeawc.photoresize.storage.Storage
import kotlinx.android.synthetic.main.activity_resize.*
import timber.log.Timber
import java.lang.reflect.Field

class ResizeFragment : Fragment(), ResizePresenter.ResizeView {

    companion object {
        const val DELAY_PHOTO_VIEW_ATTACHMENT: Long = 500
    }

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

        photoSelectViewPager.adapter = PhotoSelectViewPagerAdapter(context, fragmentManager)
        photoTablLayout.setupWithViewPager(photoSelectViewPager)
        photoSelectViewPager.isNestedScrollingEnabled = false

        (0..photoTablLayout.tabCount - 1).forEach {
            val drawableRes = PhotoSelectSources.getByPosition(it)?.getDrawableId() ?: return@forEach
            val drawable = ContextCompat.getDrawable(context, drawableRes)
            photoTablLayout.getTabAt(it)?.icon = drawable
        }
    }

    fun sharedElementTransitions(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    }

    override fun onBackPressed() {
        if (!isVisible) return

        if (transitionPhoto.visibility == View.GONE) {
            val width = zoomAndPanPhoto.width
            val height = zoomAndPanPhoto.height
            presenter.onExit(photo, width, height, zoomAndPanPhoto.imageMatrix)
        } else {
            val width = transitionPhoto.width
            val height = transitionPhoto.height
            presenter.onExit(photo, width, height, transitionPhoto.imageMatrix)
        }
    }

    override fun returnUpdatedPhoto(photo: Photo) {

        if (sharedElementTransitions()) {

            if (transitionPhoto.visibility == View.GONE) {
                // TODO: mImageView is private, need to find a way to get image matrix
                //transitionPhoto.imageMatrix = attacher.imageView.imageMatrix
                transitionPhoto.visibility = View.VISIBLE
                zoomAndPanPhoto.visibility = View.GONE
            }

            val activity = activity
            if (activity is MainActivity) {
                activity.popBackStack()
            }
        }
    }

    override fun showPhoto(photo: Photo) {

        this.photo = photo

        scale = Math.min(Math.max(1f / (photo.x2 - photo.x1), 0f), 3.0f)
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
                        attacher = PhotoViewAttacher(zoomAndPanPhoto)

                        zoomAndPanPhoto.postDelayed({
                            onZoomAndPanImageViewReady()
                        }, DELAY_PHOTO_VIEW_ATTACHMENT)

                        return false
                    }
                })
                .into(zoomAndPanPhoto)
    }

    fun onZoomAndPanImageViewReady() {
        if (!isVisible) return
        matrix.reset()
        matrix.postTranslate(translateX, translateY)
        matrix.postScale(scale, scale, 0f, 0f)
        zoomAndPanPhoto.imageMatrix = matrix
        setPhotoViewMatrix(matrix, "mSuppMatrix")
        transitionPhoto.visibility = View.GONE
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
