package com.kaia.demo.viewmodel

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kaia.demo.model.Exercise
import com.kaia.demo.util.ResponseData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository class to communicate with webservice and cacbe tbe response.
 * The assumption being made is that the data on the webservice is static
 * and isn't changing.  By caching the first and only valid response (errors
 * aren't cached), we're saving round-trips.
 */
@Singleton
class ExercisesRepository @Inject constructor() {

    private var cachedData: ResponseData<List<Exercise>>? = null

    suspend fun getExerciseList() : Flow<ResponseData<List<Exercise>>> {
        return flow {
            if (cachedData != null && !cachedData!!.hasStatusMessage())
                emit(cachedData!!)
            else {
                cachedData = fetchResponse()
                emit(cachedData!!)
            }
        }.flowOn(Dispatchers.IO)
    }

    private fun fetchResponse() : ResponseData<List<Exercise>> {
        val client = OkHttpClient()
        var response: Response? = null
        var returnVal: List<Exercise>? = null
        var exceptionMessage: String? = null

        val url = ENDPOINT_URL.toHttpUrlOrNull()!!.newBuilder()
        val urlString = url.build().toString()
        val request = Request.Builder()
            .url(urlString)
            .build()

        try {
            response = client.newCall(request).execute()
            if (response.isSuccessful)
                returnVal = parseResponse(response)
        }
        catch (e: Exception) {
            exceptionMessage = e.localizedMessage
        }

        if (returnVal != null)
            return ResponseData(returnVal)
        else if (exceptionMessage != null)
            return ResponseData(exceptionMessage)
        else
            return ResponseData(response!!.message)
    }

    private fun parseResponse(response: Response) : List<Exercise> {
        val itemType = object : TypeToken<List<Exercise>>() {}.type
        return Gson().fromJson(response.body?.charStream(), itemType)
    }

    companion object {
        val ENDPOINT_URL = "https://jsonblob.com/api/jsonBlob/027787de-c76e-11eb-ae0a-39a1b8479ec2"
    }
}