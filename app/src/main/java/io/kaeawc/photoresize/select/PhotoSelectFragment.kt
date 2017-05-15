package io.kaeawc.photoresize.select

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

abstract class PhotoSelectFragment : Fragment(), PhotoSelectPresenter.View {

    abstract val presenter: PhotoSelectPresenter
    abstract val source: PhotoSelectSources
    lateinit var adapter: PhotoSelectGridAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Timber.i("Creating view based on source $source")
        @LayoutRes val layoutResId = source.getLayoutResId()
        return inflater.inflate(layoutResId, container, false)
    }

    abstract fun getPhotoRecyclerView(): RecyclerView

    override fun showPhotos(photos: List<Pair<Long, Photo>>) {
        val recyclerView = getPhotoRecyclerView()
        val data = photos.filter { !it.second.selected }.toMutableList()
        val selectedPhoto = photos.map { it.second }.filter {it.selected}.firstOrNull()
        adapter = PhotoSelectGridAdapter(context, data, selectedPhoto)
        recyclerView.adapter = adapter
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.adapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        presenter.setView(this)
    }

    override fun onPause() {
        super.onPause()
        presenter.destroy()
    }

    override fun changePhotoSelected(position: Int) {
        adapter.changePhotoSelected(position)
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
        recyclerView.layoutManager = GridLayoutManager(context, columnCount, GridLayoutManager.VERTICAL, false)
        adapter = PhotoSelectGridAdapter(context, mutableListOf<Pair<Long, Photo>>())
        recyclerView.adapter = adapter
        recyclerView.isNestedScrollingEnabled = false
    }
}
