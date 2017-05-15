package io.kaeawc.photoresize.select.instagram

import io.kaeawc.photoresize.select.PhotoSelectPresenter

class InstagramSelectPresenter : PhotoSelectPresenter() {

    override val interactor = InstagramSelectInteractor()

}
