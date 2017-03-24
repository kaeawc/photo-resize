package io.kaeawc.photoresize.resize

import android.support.v7.widget.RecyclerView
import kotlinx.android.synthetic.main.photo_select_instagram.*

class InstagramSelectFragment : PhotoSelectFragment() {

    override val source = PhotoSelectSources.Instagram

    override fun getData() = getSamplePhotos()

    override fun getPhotoRecyclerView(): RecyclerView {
        return photoSelectRecyclerView
    }
}
