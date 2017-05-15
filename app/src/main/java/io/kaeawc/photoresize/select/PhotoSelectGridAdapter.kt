package io.kaeawc.photoresize.select

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import io.kaeawc.photoresize.R
import io.kaeawc.photoresize.models.Photo
import java.lang.ref.WeakReference
import java.util.*

class PhotoSelectGridAdapter(context: Context, val data: MutableList<Pair<Long, Photo>>, currentlySelected: Photo? = null) : RecyclerView.Adapter<GalleryViewHolder>() {

    var weakContext: WeakReference<Context>? = WeakReference(context)
    var selectedPhoto: Photo? = currentlySelected
    val footerId = Math.abs(Random().nextLong())

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): GalleryViewHolder {
        val context = parent?.context
        val inflater = LayoutInflater.from(context)
        val layout = when (viewType) {
            0 -> R.layout.photo_select_grid_item
            else -> R.layout.footer_item
        }
        val view = inflater.inflate(layout, parent, false)

        return when (viewType) {
            0 -> PhotoViewHolder(view)
            else -> FooterViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return data.size + 1
    }

    override fun getItemId(position: Int): Long {
        return when (position) {
            data.size -> footerId
            else -> data[position].first
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            data.size -> 1
            else -> 0
        }
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        if (position < 0 || position >= data.size) return
        val item = data[position]
        val context = weakContext?.get() ?: return
        if (holder is PhotoViewHolder) {
            holder.bindData(context, item.second)
        }
    }

    fun changePhotoSelected(position: Int) {
        if (position >= data.size) return
        val previouslySelected = selectedPhoto
        data[position].second.selected = true
        selectedPhoto = data[position].second

        if (previouslySelected != null) {
            data[position] = Pair(data[position].first, previouslySelected)
            notifyItemChanged(position)
        } else {
            notifyItemRemoved(position)
        }
    }
}
