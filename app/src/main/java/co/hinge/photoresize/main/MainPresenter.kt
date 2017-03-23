package co.hinge.photoresize.main

import co.hinge.photoresize.models.Photo
import co.hinge.photoresize.storage.Storage
import java.lang.ref.WeakReference

open class MainPresenter(storage: Storage) : MainInteractor.MainViewModel {

    val interactor = MainInteractor(storage)
    var weakView: WeakReference<MainView>? = null

    open fun setView(view: MainView) {
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

    interface MainView {
        fun showPhoto(photo: Photo)
    }
}
