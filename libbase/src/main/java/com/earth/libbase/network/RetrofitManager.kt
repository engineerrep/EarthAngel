package com.earth.libbase.network

import com.earth.libbase.Config
import com.earth.libbase.base.BaseApplication
import com.earth.libbase.util.LogUtils
import com.earth.libbase.util.PreferencesHelper
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object RetrofitManager {

    val apiService: ApiService = Retrofit.Builder()
        .baseUrl(Config.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(genericOkClient())
        .build()
        .create(ApiService::class.java)


    private fun genericOkClient(): OkHttpClient? {
        val httpLoggingInterceptor = HttpLoggingInterceptor(
            object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    // 如果是 json 格式内容则打印 json
                    if ((message.startsWith("{") && message.endsWith("}")) ||
                        (message.startsWith("[") && message.endsWith("]"))
                    )
                        LogUtils.json(message)
                    // Log.e("1",message)
                    else
                    // Log.e("2",message)
                        LogUtils.verbose(message)
                }
            })
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .connectTimeout(30_000L, TimeUnit.MILLISECONDS)
            .readTimeout(60_000L, TimeUnit.MILLISECONDS)
            .writeTimeout(90_000L, TimeUnit.MILLISECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    val request: Request.Builder = chain.request()
                        .newBuilder()
                    request.addHeader("Content-Type", "application/json;charset=utf-8")
                        .addHeader(
                            "token",
                            PreferencesHelper.getToken(BaseApplication.instance).toString()
                        )
                        /*  .addHeader(
                                "token",
                                "eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOiIxMTk3Iiwic3ViIjoie1wiaWRcIjoxMTk3fSIsImV4cCI6MTY1OTE0NTE2OSwibmJmIjoxNjI3NjA5MTY5fQ.sdhu0L2pi-MIgahsbDqQoSB95EJd99cLBpcwkhfvhh8060k_3-fz0nyJJ-YhWSWtv_S-fL99H4b6VXG-ifjvqQ"
                            )*/
                        .addHeader("device", "android")
                        .addHeader("language", "EN_US")
                    return chain.proceed(request.build())
                }
            })
            .build()
    }

    class IOExceptionWrapper(message: String?, cause: Throwable?) : IOException(message, cause)

}