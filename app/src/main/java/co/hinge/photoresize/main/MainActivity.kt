package co.hinge.photoresize.main

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import co.hinge.photoresize.R
import co.hinge.photoresize.media.CropPercentages
import co.hinge.photoresize.models.Photo
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

    lateinit var attacher: PhotoViewAttacher
    lateinit var presenter: MainPresenter
    lateinit var storage: Storage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        storage = Storage(Prefs(baseContext.getSharedPreferences("app", Context.MODE_PRIVATE)))
        presenter = MainPresenter(storage)
        presenter.setView(this)
    }

    override fun showPhoto(photo: Photo) {
        Glide.with(baseContext)
                .load(photo.url)
                .bitmapTransform(CropPercentages(baseContext, photo.x1, photo.y1, photo.x2, photo.y2))
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
}
