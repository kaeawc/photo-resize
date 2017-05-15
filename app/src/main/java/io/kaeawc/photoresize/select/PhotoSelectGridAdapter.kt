package io.kaeawc.photoresize.select

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import io.kaeawc.photoresize.R
import io.kaeawc.photoresize.models.Photo
import java.lang.ref.WeakReference

class PhotoSelectGridAdapter(context: Context, val data: MutableList<Pair<Long, Photo>>, currentlySelected: Photo? = null) : RecyclerView.Adapter<PhotoViewHolder>() {

    var weakContext: WeakReference<Context>? = WeakReference(context)
    var selectedPhoto: Photo? = currentlySelected

    init {
        setHasStableIds(true)
        val selected = data.filter { it.second.url == selectedPhoto?.url }.firstOrNull()
        if (selected != null) {
            data.remove(selected)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PhotoViewHolder {
        val context = parent?.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.photo_select_grid_item, parent, false)
        return PhotoViewHolder(view)
    }

    override fun getItemCount(): Int {
        val value = data.size
        return value
    }

    override fun getItemId(position: Int): Long {
        return data[position].first
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        if (position < 0 || position >= data.size) return
        val item = data[position]
        val context = weakContext?.get() ?: return
        holder.bindData(context, item.second)
    }

    fun changePhotoSelected(position: Int) {
        val previouslySelected = selectedPhoto
        data[position].second.selected = true
        selectedPhoto = data[position].second

        if (previouslySelected != null) {
            data[position] = Pair(data[position].first, previouslySelected)
            notifyItemChanged(position)
        } else {
            notifyItemRemoved(position)
        }

        notifyItemChanged(position)
    }
}
