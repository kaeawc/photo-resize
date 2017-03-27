package io.kaeawc.photoresize.select

import io.kaeawc.photoresize.models.Photo
import io.kaeawc.photoresize.resize.ResizeInteractor
import java.lang.ref.WeakReference
import java.util.*

abstract class PhotoSelectInteractor {

    var weakViewModel: WeakReference<ViewModel>? = null

    abstract val samplePhotoCount: Int

    open fun setViewModel(viewModel: ViewModel) {
        weakViewModel = WeakReference(viewModel)
    }

    open fun destroy() {
        weakViewModel?.clear()
    }

    open fun requestPhotos() {
        val viewModel = weakViewModel?.get() ?: return
        viewModel.onPhotosLoaded(getSamplePhotos(samplePhotoCount))
    }

    fun getSamplePhotos(count: Int): List<Pair<Long, Photo>> {

        val photoCount = Math.max(Math.min(count, 6), 0)

        if (photoCount == 0) {
            return emptyList<Pair<Long, Photo>>().toMutableList()
        }

        return listOf(
                "http://res.cloudinary.com/hinge-dev/image/upload/v1490208043/plebgn18lp4a4pzllobo.jpg",
                "http://res.cloudinary.com/hinge-dev/image/upload/v1490208005/i8ypkg0u3hwe5940fucv.jpg",
                "http://res.cloudinary.com/hinge-dev/image/upload/v1490208010/grn27yhybiipx0zlxm4y.jpg",
                "http://res.cloudinary.com/hinge-dev/image/upload/v1490208014/xnzgzziflomkl6thqdwp.jpg",
                "http://res.cloudinary.com/hinge-dev/image/upload/v1490208020/zspewyeyzt5ts5qfdnau.jpg",
                "http://res.cloudinary.com/hinge-dev/image/upload/v1490208024/ylyvr7azdry5bx1h6lch.jpg"
        ).subList(0, photoCount).map {
            val gridId = Math.abs(Random().nextLong())
            val photo = Photo(
                    url = it,
                    width = 0,
                    height = 0,
                    x1 = 0f,
                    y1 = 0f,
                    x2 = 0f,
                    y2 = 0f
            )
            gridId to photo
        }
    }

    fun selectPhoto() {
        // TODO: Send selected photo to RxEventBus
    }

    interface ViewModel {
        fun onPhotosLoaded(photos: List<Pair<Long, Photo>>)
        fun onPhotoSelected(photo: Photo)
    }
}
