package io.kaeawc.photoresize.select.facebook

import io.kaeawc.photoresize.select.PhotoSelectPresenter

class FacebookSelectPresenter : PhotoSelectPresenter() {

    override val interactor = FacebookSelectInteractor()

}
