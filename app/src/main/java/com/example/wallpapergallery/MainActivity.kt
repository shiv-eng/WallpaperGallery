package com.example.wallpapergallery

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

data class Wallpaper(val id: String, val imageUrl: String, val name: String)


interface UnsplashApiService {
    @GET("photos")
    suspend fun getWallpapers(@Header("Authorization") clientId: String, @Query("per_page") perPage: Int): List<Wallpaper>
}

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var wallpaperAdapter: WallpaperAdapter
    private val wallpapers = mutableListOf<Wallpaper>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        wallpaperAdapter = WallpaperAdapter(wallpapers) { wallpaper ->
            downloadWallpaper(wallpaper)
        }

        recyclerView.adapter = wallpaperAdapter

        fetchWallpapers()
    }

    private fun fetchWallpapers() {
        val clientId = "ACCESS KEY" // Replace with your Unsplash API Access Key
        val perPage = 30 // You can adjust the number of wallpapers to fetch per request
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://api.unsplash.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val unsplashApiService = retrofit.create(UnsplashApiService::class.java)

                val response = unsplashApiService.getWallpapers("Client-ID $clientId", perPage)
                withContext(Dispatchers.Main) {
                    wallpapers.clear()
                    wallpapers.addAll(response)
                    wallpaperAdapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        }
    }

    private fun downloadWallpaper(wallpaper: Wallpaper) {
        // Use a library like Glide to load the image from the URL
        Glide.with(this)
            .asBitmap()
            .load(wallpaper.imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    // Save the image to the device's external storage
                    val filename = "${wallpaper.name}.jpg"
                    val wallpaperDirectory =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    val wallpaperFile = java.io.File(wallpaperDirectory, filename)

                    try {
                        wallpaperFile.outputStream().use { outStream ->
                            resource.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
                        }
                        // Show a success message to the user
                        Toast.makeText(
                            this@MainActivity,
                            "Wallpaper downloaded successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
                        // Handle download error
                        Toast.makeText(
                            this@MainActivity,
                            "Download failed",
                            Toast.LENGTH_SHORT
                        ).show()
                        e.printStackTrace()
                    }
                }

                override fun onLoadCleared(placeholder: android.graphics.drawable.Drawable?) {
                    // Not used
                }
            })
    }


}
