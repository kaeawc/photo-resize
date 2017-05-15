package io.kaeawc.photoresize.select.instagram

import android.support.v7.widget.RecyclerView
import io.kaeawc.photoresize.select.PhotoSelectFragment
import io.kaeawc.photoresize.select.PhotoSelectSources
import kotlinx.android.synthetic.main.photo_select_instagram.*

class InstagramSelectFragment : PhotoSelectFragment() {

    override val presenter = InstagramSelectPresenter()

    override val source = PhotoSelectSources.Instagram

    override fun getPhotoRecyclerView(): RecyclerView {
        return photoSelectRecyclerView
    }
}
