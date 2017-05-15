package io.kaeawc.photoresize.select

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import io.kaeawc.photoresize.select.facebook.FacebookSelectFragment
import io.kaeawc.photoresize.select.gallery.GallerySelectFragment
import io.kaeawc.photoresize.select.instagram.InstagramSelectFragment
import java.lang.ref.WeakReference

class PhotoSelectViewPagerAdapter(context: Context, fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    val weakContext: WeakReference<Context>? = WeakReference(context)

    override fun getItem(position: Int): Fragment {
        val screen = PhotoSelectSources.getByPosition(position)

        return when (screen) {
            PhotoSelectSources.Facebook -> FacebookSelectFragment()
            PhotoSelectSources.Instagram -> InstagramSelectFragment()
            PhotoSelectSources.Gallery -> GallerySelectFragment()
            else -> throw Exception("Unknown main position: $position")
        }
    }

    override fun getCount(): Int {
        return PhotoSelectSources.values().size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return ""
//        val screen = PhotoSelectSources.getByPosition(position)
//        val context = weakContext?.get() ?: return ""
//
//        return when {
//            screen != null -> screen.getTitle(context)
//            else -> ""
//        }

//        val image = ContextCompat.getDrawable(context, R.drawable.ic_facebook_active)
//        Timber.i("Found image: $image | image.intrinsicWidth: ${image.intrinsicWidth}, image.intrinsicHeight: ${image.intrinsicHeight}")
//        image.setBounds(0, 0, image.intrinsicWidth, image.intrinsicHeight)
//        val sb = SpannableString(" ")
//        val imageSpan = ImageSpan(image, ImageSpan.ALIGN_BOTTOM)
//        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//        Timber.i("Image span: $sb")
//        return sb
    }
}
