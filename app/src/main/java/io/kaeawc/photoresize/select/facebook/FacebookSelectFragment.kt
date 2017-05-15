package io.kaeawc.photoresize.select.facebook

import android.support.v7.widget.RecyclerView
import io.kaeawc.photoresize.select.PhotoSelectFragment
import io.kaeawc.photoresize.select.PhotoSelectSources
import kotlinx.android.synthetic.main.photo_select_facebook.*

class FacebookSelectFragment : PhotoSelectFragment() {

    override val presenter = FacebookSelectPresenter()

    override val source = PhotoSelectSources.Facebook

    override fun getPhotoRecyclerView(): RecyclerView {
        return photoSelectRecyclerView
    }
}
