package ai.botstacks.sdk.auth

import android.util.Log
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException

import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyAuth(
    private val location: String = "",
    private val paramName: String = "",
    private var apiKey: String = ""
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        Log.v("BotStacksChat", "API KEY AUTH $location, $paramName, $apiKey")

        if ("query" == location) {
            var newQuery = request.url.toUri().query
            val paramValue = "$paramName=$apiKey"
            if (newQuery == null) {
                newQuery = paramValue
            } else {
                newQuery += "&$paramValue"
            }

            val newUri: URI
            try {
                val oldUri = request.url.toUri()
                newUri = URI(
                    oldUri.scheme, oldUri.authority,
                    oldUri.path, newQuery, oldUri.fragment
                )
            } catch (e: URISyntaxException) {
                throw IOException(e)
            }

            request = request.newBuilder().url(newUri.toURL()).build()
        } else if ("header" == location) {
            request = request.newBuilder()
                .addHeader(paramName, apiKey)
                .build()
        } else if ("cookie" == location) {
            request = request.newBuilder()
                .addHeader("Cookie", "$paramName=$apiKey")
                .build()
        }
        return chain.proceed(request)
    }
}
