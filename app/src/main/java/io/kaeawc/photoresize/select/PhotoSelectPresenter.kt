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

    override fun onPhotoSelected(position: Int) {
        val view = weakView?.get() ?: return
        view.changePhotoSelected(position)
    }

    fun destroy() {
        interactor.destroy()
        weakView?.clear()
    }

    interface View {
        fun showPhotos(photos: List<Pair<Long, Photo>>)
        fun changePhotoSelected(position: Int)
    }
}