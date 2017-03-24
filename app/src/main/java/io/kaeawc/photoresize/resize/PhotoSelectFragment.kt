package io.kaeawc.photoresize.resize

import android.graphics.Point
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.kaeawc.photoresize.models.Photo
import timber.log.Timber
import java.util.*

abstract class PhotoSelectFragment : Fragment() {

    abstract val source: PhotoSelectSources

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Timber.i("Creating view based on source $source")
        @LayoutRes val layoutResId = source.getLayoutResId()
        return inflater.inflate(layoutResId, container, false)
    }

    abstract fun getData(): MutableList<Pair<Long, Photo>>

    abstract fun getPhotoRecyclerView(): RecyclerView

    fun getSamplePhotos(): MutableList<Pair<Long, Photo>> {
        return listOf(
                "http://res.cloudinary.com/hinge-dev/image/upload/v1490208043/plebgn18lp4a4pzllobo.jpg",
                "http://res.cloudinary.com/hinge-dev/image/upload/v1490208005/i8ypkg0u3hwe5940fucv.jpg",
                "http://res.cloudinary.com/hinge-dev/image/upload/v1490208010/grn27yhybiipx0zlxm4y.jpg",
                "http://res.cloudinary.com/hinge-dev/image/upload/v1490208014/xnzgzziflomkl6thqdwp.jpg",
                "http://res.cloudinary.com/hinge-dev/image/upload/v1490208020/zspewyeyzt5ts5qfdnau.jpg",
                "http://res.cloudinary.com/hinge-dev/image/upload/v1490208024/ylyvr7azdry5bx1h6lch.jpg"
        ).map {
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
        }.toMutableList()
    }

    fun getEmpty(): MutableList<Pair<Long, Photo>> {
        return emptyList<Pair<Long, Photo>>().toMutableList()
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {

        val display = activity.windowManager.defaultDisplay
        val point = Point()
        display.getSize(point)
        val width = point.x
        val height = point.y
        val columnCount = when {
            width > height -> 6
            else -> 4
        }

        val recyclerView = getPhotoRecyclerView()
        val data = getData()
        Timber.i("Found ${data.size} photos for $source")

        data.forEach {
            Timber.i("${it.first} -> ${it.second.url}")
        }
        recyclerView.layoutManager = GridLayoutManager(context, columnCount, GridLayoutManager.VERTICAL, false)
        recyclerView.adapter = PhotoSelectGridAdapter(context, data)
        recyclerView.isNestedScrollingEnabled = false
    }
}
