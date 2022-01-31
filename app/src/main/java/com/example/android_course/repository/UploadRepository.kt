package com.example.android_course.repository

import android.content.ContentResolver
import android.net.Uri
import com.example.android_course.data.network.ImgurApi
import com.example.android_course.data.network.response.Upload

import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

interface UploadRepository {
    suspend fun uploadFile(uri: Uri, title: String? = null): UploadRepositoryImp.UploadResult
}

@Suppress("BlockingMethodInNonBlockingContext")
class UploadRepositoryImp(private val imgurApi: ImgurApi, private val contentResolver: ContentResolver): UploadRepository{

    /**
     * Executes an upload to via retrofit and returns a Result.
     *
     * @param uri the selected image from the users system storage
     * @param title an optional title for the image, sent to the imgur api
     */
    override suspend fun uploadFile(uri: Uri, title: String?): UploadResult {
        return try{

            // copy inputstream from Uri to a temporary file for upload
            val file = copyStreamToFile(uri)

            val filePart = MultipartBody.Part.createFormData("image", file.name, file.asRequestBody())

            val response = imgurApi.uploadFile(
                filePart,
                name =  title?.toRequestBody() ?: file.name.toRequestBody()
            )

            if(response.isSuccessful){
                UploadResult.Success(response.body()!!.upload)
            }else{
                UploadResult.Error("Unknown network Exception.")
            }
        }catch (e: Exception){
           UploadResult.Error(e.message)
        }
    }

    /**
     * Creates a temporary file from a Uri, preparing it for upload.
     */
    private fun copyStreamToFile(uri: Uri): File {
        val outputFile = File.createTempFile("temp", null)

        contentResolver.openInputStream(uri)?.use { input ->
            val outputStream = FileOutputStream(outputFile)
            outputStream.use { output ->
                val buffer = ByteArray(4 * 1024) // buffer size
                while (true) {
                    val byteCount = input.read(buffer)
                    if (byteCount < 0) break
                    output.write(buffer, 0, byteCount)
                }
                output.flush()
            }
        }
        return outputFile
    }

    sealed class UploadResult {
        data class Success(val uploadResult : Upload) : UploadResult()
        data class Error(val msg : String?) : UploadResult()
    }
}