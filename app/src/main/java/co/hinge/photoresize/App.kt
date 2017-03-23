package co.hinge.photoresize

import android.app.Application
import co.hinge.photoresize.media.GlideProvider
import okhttp3.OkHttpClient
import timber.log.Timber

class App : Application() {

    lateinit var okHttp: OkHttpClient

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        Timber.i("App Started")

        okHttp = OkHttpClient.Builder()
                .build()

        Timber.i("Register glide")
        GlideProvider.registerComponents(this, okHttp)
    }
}
