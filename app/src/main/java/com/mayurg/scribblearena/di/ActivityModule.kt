package com.mayurg.scribblearena.di

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import com.mayurg.scribblearena.data.remote.api.SetupApi
import com.mayurg.scribblearena.data.remote.ws.CustomMessageAdapter
import com.mayurg.scribblearena.data.remote.ws.DrawingApi
import com.mayurg.scribblearena.data.remote.ws.FlowStreamAdapter
import com.mayurg.scribblearena.repository.DefaultSetupRepository
import com.mayurg.scribblearena.repository.SetupRepository
import com.mayurg.scribblearena.util.Constants
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.lifecycle.android.AndroidLifecycle
import com.tinder.scarlet.retry.LinearBackoffStrategy
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Dependency module which contains dependencies with the lifespan of an Activity
 *
 * Created On 01/09/2021
 * @author Mayur Gajra
 */
@Module
@InstallIn(ActivityRetainedComponent::class)
object ActivityModule {

    @ActivityRetainedScoped
    @Provides
    fun provideSetupRepository(
        setupApi: SetupApi,
        @ApplicationContext context: Context
    ): SetupRepository = DefaultSetupRepository(setupApi, context)

    @ActivityRetainedScoped
    @Provides
    fun provideDrawingApi(
        app: Application,
        okkHttpClient: OkHttpClient,
        gson: Gson
    ): DrawingApi {
        return Scarlet.Builder()
            .backoffStrategy(LinearBackoffStrategy(Constants.RECONNECT_INTERVAL))
            .lifecycle(AndroidLifecycle.ofApplicationForeground(app))
            .webSocketFactory(
                okkHttpClient.newWebSocketFactory(
                    if (Constants.USE_LOCALHOST) Constants.WS_BASE_URL_LOCALHOST else Constants.WS_BASE_URL
                )
            ).addStreamAdapterFactory(FlowStreamAdapter.Factory)
            .addMessageAdapterFactory(CustomMessageAdapter.Factory(gson))
            .build()
            .create()
    }

    @ActivityRetainedScoped
    @Provides
    fun provideSetupApi(okkHttpClient: OkHttpClient): SetupApi {
        return Retrofit.Builder()
            .baseUrl(if (Constants.USE_LOCALHOST) Constants.HTTP_BASE_URL_LOCAL else Constants.HTTP_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okkHttpClient)
            .build()
            .create(SetupApi::class.java)
    }
}