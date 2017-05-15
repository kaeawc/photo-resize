package io.kaeawc.photoresize.select.gallery

import android.support.v7.widget.RecyclerView
import io.kaeawc.photoresize.select.PhotoSelectFragment
import io.kaeawc.photoresize.select.PhotoSelectSources
import kotlinx.android.synthetic.main.photo_select_gallery.*

class GallerySelectFragment : PhotoSelectFragment() {

    override val presenter = GallerySelectPresenter()

    override val source = PhotoSelectSources.Gallery

    override fun getPhotoRecyclerView(): RecyclerView {
        return photoSelectRecyclerView
    }
}
