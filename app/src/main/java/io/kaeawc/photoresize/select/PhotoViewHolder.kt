package io.kaeawc.photoresize.select

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import io.kaeawc.photoresize.R
import io.kaeawc.photoresize.models.Photo
import timber.log.Timber

class PhotoViewHolder(view: View) : GalleryViewHolder(view), RequestListener<String, GlideDrawable> {

    val photoImage: ImageView? by lazy { view.findViewById(R.id.photoSelectImage) as? ImageView? }

    fun bindData(context: Context, photo: Photo) {
        if (photoImage == null) return
        Glide.with(context)
                .load(photo.url)
                .centerCrop()
                .listener(this)
                .into(photoImage)

        photoImage?.setOnClickListener {
            onPhotoTapped(photo)
        }
    }

    override fun onException(e: Exception?, model: String?, target: Target<GlideDrawable>?, isFirstResource: Boolean): Boolean {
        Timber.e(e, "onException")
        return false
    }

    override fun onResourceReady(resource: GlideDrawable?, model: String?, target: Target<GlideDrawable>?, isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
        return false
    }

    fun onPhotoTapped(photo: Photo) {
        // pass event to eventbus
    }
}
