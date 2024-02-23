package ai.botstacks.sdk.ui.components.internal.camera

import ai.botstacks.sdk.R
import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.util.Objects

class ComposeFileProvider : FileProvider(R.xml.provider_paths) {
    companion object {
        fun getImageUri(context: Context): Uri {
            val tempFile = File.createTempFile(
                "picture_${System.currentTimeMillis()}", ".png", context.cacheDir
            ).apply {
                createNewFile()
            }
            val authority = context.applicationContext.packageName + ".botstacks.camera.provider"
            return getUriForFile(
                Objects.requireNonNull(context),
                authority,
                tempFile,
            )
        }

        fun getImageUri(context: Context, file: File): Uri {
            val authority = context.applicationContext.packageName + ".botstacks.camera.provider"
            return getUriForFile(
                Objects.requireNonNull(context),
                authority,
                file)
        }
    }
}