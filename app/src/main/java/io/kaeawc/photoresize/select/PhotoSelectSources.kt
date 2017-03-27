package io.kaeawc.photoresize.select

import android.content.Context
import android.support.annotation.DrawableRes
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import io.kaeawc.photoresize.R

enum class PhotoSelectSources(@StringRes val titleRes: Int, @LayoutRes val layoutRes: Int, @DrawableRes val drawableRes: Int) {
    Facebook(R.string.facebook, R.layout.photo_select_facebook, R.drawable.ic_facebook_active),
    Instagram(R.string.instagram, R.layout.photo_select_instagram, R.drawable.ic_instagram_inactive),
    Gallery(R.string.gallery, R.layout.photo_select_gallery, R.drawable.ic_instagram_inactive);

    @LayoutRes
    fun getLayoutResId(): Int {
        return layoutRes
    }

    fun getTitle(context: Context): String {
        return context.resources.getString(titleRes)
    }

    @DrawableRes
    fun getDrawableId(): Int {
        return drawableRes
    }

    companion object {
        fun getByPosition(position: Int): PhotoSelectSources? {
            val v = values()
            return when {
                position >= v.size -> null
                else -> v[position]
            }
        }
    }
}
