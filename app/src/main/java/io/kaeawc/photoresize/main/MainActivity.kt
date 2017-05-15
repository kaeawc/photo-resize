package io.kaeawc.photoresize.main

import android.content.Context
import android.graphics.Matrix
import android.os.Build
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import io.kaeawc.photoresize.R
import io.kaeawc.photoresize.models.Photo
import io.kaeawc.photoresize.storage.Prefs
import io.kaeawc.photoresize.storage.Storage
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import io.kaeawc.photoresize.resize.ResizeFragment
import timber.log.Timber
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : FragmentActivity(), RequestListener<String, GlideDrawable>, MainPresenter.MainView {

    var fragment: ResizeFragment? = null
    var presenter: MainPresenter? = null
    lateinit var storage: Storage

    var scale: Float = 1f
    var translateX: Float = 0f
    var translateY: Float = 0f
    val matrix = Matrix()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        storage = Storage(Prefs(baseContext.getSharedPreferences("app", Context.MODE_PRIVATE)))
        if (presenter == null) {
            presenter = MainPresenter(storage)
            presenter?.setView(this)
            presenter?.loadPhoto()
        }
    }

    override fun onBackPressed() {
        val fragment = fragment
        if (fragment == null) {
            super.onBackPressed()
            return
        }

        fragment.onBackPressed()
    }

    fun popBackStack() {
        presenter?.loadPhoto()
        fragment = null
        supportFragmentManager.popBackStack()
        transitionPhoto.bringToFront()

        transitionPhoto.setOnClickListener {
            transitionPhoto.setOnClickListener(null)
            container.bringToFront()
            routeToEdit()
        }
    }

    override fun showPhoto(photo: Photo) {
        scale = Math.min(Math.max(1f / (photo.x2 - photo.x1), 0f), 3.0f)
        translateX = -Math.max(Math.min(photo.width * photo.x1, photo.width.toFloat()), 0f)
        translateY = -Math.max(Math.min(photo.height * photo.y1, photo.height.toFloat()), 0f)

        Glide.with(baseContext)
                .load(photo.url)
                .listener(this)
                .into(transitionPhoto)
    }

    override fun onException(e: Exception?, model: String?, target: Target<GlideDrawable>?, isFirstResource: Boolean): Boolean {
        Timber.e(e, "onException")
        return false
    }

    override fun onResourceReady(resource: GlideDrawable?, model: String?, target: Target<GlideDrawable>?, isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {

        matrix.reset()
        matrix.postTranslate(translateX, translateY)
        matrix.postScale(scale, scale, 0f, 0f)
        transitionPhoto.imageMatrix = matrix

        transitionPhoto.setOnClickListener {
            transitionPhoto.setOnClickListener(null)
            container.bringToFront()
            routeToEdit()
        }

        return false
    }

    fun sharedElementTransitions(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    }

    fun routeToEdit() {
        fragment = ResizeFragment()
        val transaction = supportFragmentManager.beginTransaction()
        if (sharedElementTransitions()) transaction.addSharedElement(transitionPhoto, "photo")
        transaction.replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit()
    }
}
