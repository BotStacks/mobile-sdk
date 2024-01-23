package ai.botstacks.sdk.state

import android.net.Uri
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ai.botstacks.sdk.API
import ai.botstacks.sdk.BotStacksChat
import ai.botstacks.sdk.Server
import ai.botstacks.sdk.type.AttachmentInput
import ai.botstacks.sdk.type.AttachmentType
import ai.botstacks.sdk.utils.Monitoring
import ai.botstacks.sdk.utils.bg
import ai.botstacks.sdk.utils.op
import ai.botstacks.sdk.utils.uuid
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.internal.closeQuietly
import okio.BufferedSink
import okio.source
import org.json.JSONObject
import java.util.UUID
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

fun Uri.contentType() = BotStacksChat.shared.appContext.contentResolver.getType(this)
    ?.toMediaTypeOrNull()

fun Uri.asRequestBody(): RequestBody {
    return object : RequestBody() {
        override fun contentType() = this@asRequestBody.contentType()


        override fun contentLength() = -1L

        override fun writeTo(sink: BufferedSink) {
            BotStacksChat.shared.appContext.contentResolver.openInputStream(
                this@asRequestBody
            )?.source()?.let { sink.writeAll(it) }
        }
    }
}


val uploadClient = OkHttpClient.Builder().build()

@Stable
data class Upload(val id: String = uuid(), val uri: Uri) {
    var uploading by mutableStateOf(false)
    var url by mutableStateOf<String?>(null)
    var error by mutableStateOf<Error?>(null)

    private var _await: Continuation<String>? = null
    suspend fun await() = suspendCoroutine<String> { cont ->
        url?.let {
            cont.resume(it)
        } ?: error?.let {
            cont.resumeWithException(Error(it))
        } ?: run {
            _await = cont
            if (!uploading) upload()
        }
    }

    suspend fun awaitAttachment() = await().let { attachment()!! }


    fun upload() {
        if (url != null) return
        if (uploading) return
        uploading = true
        error = null
        op({
            var response: Response? = null
            try {
                response = bg {
                    val body = uri.asRequestBody()
                    val request = Request.Builder()
                        .url(Server.http + "/misc/upload/${UUID.randomUUID()}")
                        .addHeader("X-API-Key", BotStacksChat.shared.apiKey)
                        .addHeader("X-Device-ID", API.deviceId)
                        .addHeader("Referer", BotStacksChat.shared.packageName)
                        .post(body)
                        .build()
                    uploadClient.newCall(request).execute()
                }
                if (!response.isSuccessful) {
                    Monitoring.error("Upload response code: " + response.code + " Message: " + response.message)
                } else {
                    url = response.body?.string()?.let { JSONObject(it).getString("url") }
                }
            } catch (err: Error) {
                println("Upload error")
                Monitoring.error(err)
                error = err
            } finally {
                response?.closeQuietly()
            }
            if (url == null && error == null) {
                error = Error("Unknown error occurred")
            }
            uploading = false
            url?.let {
                _await?.resume(it)
                _await = null
            } ?: error?.let {
                _await?.resumeWithException(it)
                _await = null
            }
        })
    }

    fun attachmentType(): AttachmentType {
        val mime = uri.contentType()?.type
        if (mime != null) {
            if (mime.startsWith("image")) return AttachmentType.image
            if (mime.startsWith("video")) return AttachmentType.video
            if (mime.startsWith("audio")) return AttachmentType.audio
        }
        return AttachmentType.file
    }

    fun attachment() = url?.let { AttachmentInput(url = it, type = attachmentType(), id = id) }

    fun localAttachment() = AttachmentInput(url = uri.toString(), type = attachmentType(), id = id)

}
