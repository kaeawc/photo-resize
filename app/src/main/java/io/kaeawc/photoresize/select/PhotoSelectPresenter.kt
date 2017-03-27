package io.kaeawc.photoresize.select

import io.kaeawc.photoresize.models.Photo
import java.lang.ref.WeakReference

abstract class PhotoSelectPresenter : PhotoSelectInteractor.ViewModel {

    abstract val interactor: PhotoSelectInteractor

    var weakView: WeakReference<View>? = null

    open fun setView(view: View) {
        weakView = WeakReference(view)
        interactor.setViewModel(this)
        interactor.requestPhotos()
    }

    override fun onPhotosLoaded(photos: List<Pair<Long, Photo>>) {
        val view = weakView?.get() ?: return
        view.showPhotos(photos)
    }

    override fun onPhotoSelected(photo: Photo) {
        val view = weakView?.get() ?: return
        view.changePhotoSelected(photo)
    }

    interface View {
        fun showPhotos(photos: List<Pair<Long, Photo>>)
        fun changePhotoSelected(photo: Photo?)
    }
}