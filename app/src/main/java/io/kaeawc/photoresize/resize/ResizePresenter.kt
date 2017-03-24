package io.kaeawc.photoresize.resize

import android.graphics.Matrix
import io.kaeawc.photoresize.models.Photo
import io.kaeawc.photoresize.storage.Storage
import java.lang.ref.WeakReference

open class ResizePresenter(open val storage: Storage) : ResizeInteractor.ResizeViewModel {

    open val interactor = ResizeInteractor(storage)
    var weakView: WeakReference<ResizeView>? = null

    open fun setView(view: ResizeView) {
        weakView = WeakReference(view)
        interactor.setViewModel(this)
        interactor.requestPhoto()
    }

    open fun onExit(photo: Photo, width: Int, height: Int, matrix: Matrix) {
        interactor.persistPhoto(photo, width, height, matrix)
        weakView?.clear()
    }

    override fun onPhotoLoaded(photo: Photo) {
        val view = weakView?.get() ?: return
        view.showPhoto(photo)
    }

    override fun onPhotoUpdated(photo: Photo) {
        val view = weakView?.get() ?: return
        view.returnUpdatedPhoto(photo)
    }

    interface ResizeView {
        fun showPhoto(photo: Photo)
        fun returnUpdatedPhoto(photo: Photo)
    }
}
