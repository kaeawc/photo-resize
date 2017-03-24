package io.kaeawc.photoresize.main

import io.kaeawc.photoresize.models.Photo
import io.kaeawc.photoresize.storage.Storage
import java.lang.ref.WeakReference

open class MainInteractor(open val storage: Storage) {

    var weakViewModel: WeakReference<MainViewModel>? = null

    open fun setViewModel(viewModel: MainViewModel) {
        weakViewModel = WeakReference(viewModel)
    }

    open fun destroy() {
        weakViewModel?.clear()
    }

    open fun requestPhoto() {
        val viewModel = weakViewModel?.get() ?: return
        viewModel.onPhotoLoaded(storage.getPhoto())
    }

    interface MainViewModel {
        fun onPhotoLoaded(photo: Photo)
    }
}
