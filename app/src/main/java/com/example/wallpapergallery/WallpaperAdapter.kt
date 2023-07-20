package com.example.wallpapergallery
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class WallpaperAdapter(
    private val wallpapers: List<Wallpaper>,
    private val onDownloadClicked: (Wallpaper) -> Unit
) : RecyclerView.Adapter<WallpaperAdapter.WallpaperViewHolder>() {

    class WallpaperViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WallpaperViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_wallpaper, parent, false)
        return WallpaperViewHolder(view)
    }

    override fun onBindViewHolder(holder: WallpaperViewHolder, position: Int) {
        val wallpaper = wallpapers[position]

        Glide.with(holder.itemView)
            .load(wallpaper.imageUrl)
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            onDownloadClicked(wallpaper)
        }
    }

    override fun getItemCount(): Int {
        return wallpapers.size
    }
}
