package io.kaeawc.photoresize.main

import io.kaeawc.photoresize.models.Photo
import io.kaeawc.photoresize.storage.Storage
import java.lang.ref.WeakReference

open class MainPresenter(storage: Storage) : MainInteractor.MainViewModel {

    open val interactor = MainInteractor(storage)
    var weakView: WeakReference<MainView>? = null

    open fun setView(view: MainView) {
        weakView = WeakReference(view)
        interactor.setViewModel(this)
    }

    open fun requestPhoto() {
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
