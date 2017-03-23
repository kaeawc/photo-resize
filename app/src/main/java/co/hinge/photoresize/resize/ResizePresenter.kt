package co.hinge.photoresize.resize

import co.hinge.photoresize.models.Photo
import co.hinge.photoresize.storage.Storage
import java.lang.ref.WeakReference

open class ResizePresenter(open val storage: Storage) : ResizeInteractor.ResizeViewModel {

    val interactor = ResizeInteractor(storage)
    var weakView: WeakReference<ResizeView>? = null

    open fun setView(view: ResizeView) {
        weakView = WeakReference(view)
        interactor.setViewModel(this)
        interactor.requestPhoto()
    }

    open fun destroy() {
        weakView?.clear()
    }

    override fun onPhotoLoaded(photo: Photo) {
        val view = weakView?.get() ?: return
        view.showPhoto(photo)
    }

    interface ResizeView {
        fun showPhoto(photo: Photo)
    }
}
