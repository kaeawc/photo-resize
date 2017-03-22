package co.hinge.photoresize

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import timber.log.Timber
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), RequestListener<String, GlideDrawable> {

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
        return false
    }
}
