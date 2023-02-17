package utils

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.buffer
import okio.source
import java.nio.charset.StandardCharsets

internal fun MockWebServer.enqueueResponse(fileName: String, code: Int): MockResponse? {
    val inputStream = javaClass.classLoader?.getResourceAsStream("api-response/$fileName")

    val source = inputStream?.let { inputStream.source().buffer() }
    val mockResponse = source?.readString(StandardCharsets.UTF_8)?.let {
        MockResponse()
            .setResponseCode(code)
            .setBody(it)
    }
    source?.let {
        mockResponse?.let { it1 ->
            enqueue(
                it1
            )
        }
    }
    return mockResponse
}