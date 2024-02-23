package ai.botstacks.sdk.utils

import java.net.URLEncoder


actual fun urlEncode(value: String, encoding: String): String {
    return URLEncoder.encode(value, encoding)
}