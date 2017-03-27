package io.kaeawc.photoresize.select

import android.support.v7.widget.RecyclerView
import kotlinx.android.synthetic.main.photo_select_facebook.*

class FacebookSelectFragment : PhotoSelectFragment() {

    override val source = PhotoSelectSources.Facebook

    override fun getData() = getSamplePhotos()

    override fun getPhotoRecyclerView(): RecyclerView {
        return photoSelectRecyclerView
    }
}
