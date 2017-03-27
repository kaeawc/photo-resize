package io.kaeawc.photoresize.select

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import io.kaeawc.photoresize.R
import io.kaeawc.photoresize.models.Photo
import timber.log.Timber

class PhotoViewHolder(val view: View) : RecyclerView.ViewHolder(view), RequestListener<String, GlideDrawable> {

    var photoImage: ImageView? = view.findViewById(R.id.photoSelectImage) as ImageView?

    fun bindData(context: Context, photo: Photo) {
        if (photoImage != null) {
            Glide.with(context)
                    .load(photo.url)
                    .centerCrop()
                    .listener(this)
                    .into(photoImage)
        }
    }

    override fun onException(e: Exception?, model: String?, target: Target<GlideDrawable>?, isFirstResource: Boolean): Boolean {
        Timber.e(e, "onException")
        return false
    }

    override fun onResourceReady(resource: GlideDrawable?, model: String?, target: Target<GlideDrawable>?, isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
        return false
    }
}
