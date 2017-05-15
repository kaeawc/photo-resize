package io.kaeawc.photoresize.select

import io.kaeawc.photoresize.models.Photo
import timber.log.Timber
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

        val photoCount = Math.max(Math.min(count, 50), 0)

        if (photoCount == 0) {
            return emptyList<Pair<Long, Photo>>().toMutableList()
        }

        val urls = listOf(
                "https://scontent.xx.fbcdn.net/v/t31.0-8/16602040_10100460351134980_9180327545299035667_o.jpg?oh=53f48150930bb377ab9bf830879f2c90&oe=59BB94CE",
                "https://scontent.xx.fbcdn.net/v/t1.0-9/14192758_10100378157781120_4665744363439969252_n.jpg?oh=400d24c90b20ae12b1dd09ffb4fcb137&oe=597BDA9E",
                "https://scontent.xx.fbcdn.net/v/t1.0-9/14064274_10100373775273700_4776474758773901624_n.jpg?oh=1e2ccce0ef6e6047383b407fd0ead4d8&oe=59C1D05C",
                "https://scontent.xx.fbcdn.net/v/t1.0-9/13607003_10100352282774840_2160925153166305988_n.jpg?oh=6081d8ab6c0eb92af9ad1bdb4d1f2626&oe=59B62FCC",
                "https://scontent.xx.fbcdn.net/v/t1.0-9/13240153_10100335087075160_1315439773619025638_n.jpg?oh=8bb24869fa2a79d7162a42dc661d3e36&oe=5976D996"
        )

        return (0..photoCount).map {
            val gridId = Math.abs(Random().nextLong())
            val url = urls[Random().nextInt(4)]
            Timber.i("url: $url")
            val photo = Photo(
                    url = url,
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

    fun selectPhoto(photoId: Long, photo: Photo) {
        // TODO: Send selected photo to RxEventBus
    }

    interface ViewModel {
        fun onPhotosLoaded(photos: List<Pair<Long, Photo>>)
        fun onPhotoSelected(position: Int)
    }
}
