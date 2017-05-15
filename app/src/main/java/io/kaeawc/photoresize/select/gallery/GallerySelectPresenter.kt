package io.kaeawc.photoresize.select.gallery

import io.kaeawc.photoresize.select.PhotoSelectPresenter

class GallerySelectPresenter : PhotoSelectPresenter() {

    override val interactor = GallerySelectInteractor()

}
