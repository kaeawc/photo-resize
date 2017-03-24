package co.hinge.photoresize.main

import android.content.Context
import android.content.Intent
import android.graphics.Matrix
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.view.View
import android.view.Window
import co.hinge.photoresize.R
import co.hinge.photoresize.models.Photo
import co.hinge.photoresize.resize.ResizeActivity
import co.hinge.photoresize.storage.Prefs
import co.hinge.photoresize.storage.Storage
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import timber.log.Timber
import kotlinx.android.synthetic.main.activity_main.*
import uk.co.senab.photoview.PhotoViewAttacher

class MainActivity : AppCompatActivity(), RequestListener<String, GlideDrawable>, MainPresenter.MainView {

    companion object {
        const val EDIT_PHOTO_RESULT = 4023
    }

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
        } else {
//            transitionPhoto.setImageResource(android.R.color.white)
//            presenter?.loadPhoto()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != EDIT_PHOTO_RESULT) {
            Timber.i("Giving up and presenter?.loadPhoto()")
            presenter?.loadPhoto()
            return
        }

        val url = data?.getStringExtra("url")
        if (url == null) {
            Timber.i("Giving up and presenter?.loadPhoto()")
            presenter?.loadPhoto()
            return
        }

        Timber.i("Obtaining photo coordinates from activity result")
        val photo = Photo(
                url = data.getStringExtra("url") ?: "",
                width = data.getIntExtra("width", 100),
                height = data.getIntExtra("height", 100),
                x1 = data.getFloatExtra("x1", 0f),
                y1 = data.getFloatExtra("y1", 0f),
                x2 = data.getFloatExtra("x2", 0f),
                y2 = data.getFloatExtra("y2", 0f)
        )

        scale = Math.min(Math.max(1f / (photo.x2 - photo.x1), 0f), PhotoViewAttacher.DEFAULT_MAX_SCALE)
        translateX = -Math.max(Math.min(photo.width * photo.x1, photo.width.toFloat()), 0f)
        translateY = -Math.max(Math.min(photo.height * photo.y1, photo.height.toFloat()), 0f)

        matrix.reset()
        matrix.postTranslate(translateX, translateY)
        matrix.postScale(scale, scale, 0f, 0f)
        transitionPhoto.imageMatrix = matrix
    }

    override fun showPhoto(photo: Photo) {
        scale = Math.min(Math.max(1f / (photo.x2 - photo.x1), 0f), PhotoViewAttacher.DEFAULT_MAX_SCALE)
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
            routeToEdit()
        }

        return false
    }

    fun sharedElementTransitions(): Boolean {
        // return false
         return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    }

    fun routeToEdit() {

        val intent = Intent(baseContext, ResizeActivity::class.java)

        if (sharedElementTransitions()) {
            val statusBar = findViewById(android.R.id.statusBarBackground)
            val navBar = findViewById(android.R.id.navigationBarBackground)
            val photo = android.support.v4.util.Pair<View, String>(transitionPhoto, "photo")
            val statusBarPair = android.support.v4.util.Pair<View, String>(statusBar, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME)
            val navBarPair = android.support.v4.util.Pair<View, String>(navBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, photo, statusBarPair, navBarPair)
            startActivityForResult(intent, EDIT_PHOTO_RESULT, options.toBundle())
        } else {
            startActivityForResult(intent, EDIT_PHOTO_RESULT)
        }
    }
}
