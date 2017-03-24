package io.kaeawc.photoresize.resize

import android.support.v7.widget.RecyclerView
import kotlinx.android.synthetic.main.photo_select_gallery.*

class GallerySelectFragment : PhotoSelectFragment() {

    override val source = PhotoSelectSources.Gallery

    override fun getData() = getSamplePhotos()

    override fun getPhotoRecyclerView(): RecyclerView {
        return photoSelectRecyclerView
    }
}
