package albums

import com.google.common.truth.Truth.assertThat
import com.justmusic.network.ApiService
import com.justmusic.network.BuildConfig
import com.justmusic.network.utils.BaseAPIRequest
import com.justmusic.shared.HttpStatusEnum
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import utils.*
import java.net.HttpURLConnection.HTTP_OK
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class AlbumsNetworkApiTest {
    private val mockWebServer = MockWebServer()
    private lateinit var client: OkHttpClient
    private lateinit var apiService: ApiService
    private lateinit var baseAPIRequest: BaseAPIRequest

    @get:Rule
    val coroutineRule = MainCoroutineRuleForNetwork()

    @Before
    fun setUp() {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        mockWebServer.start()
        client = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .build()

        apiService = Retrofit.Builder()
            .baseUrl(BuildConfig.HOST_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()
            .create(ApiService::class.java)

        baseAPIRequest = mock(BaseAPIRequest::class.java)
    }

    @Test
    fun `should fetch albums correctly given 200 response`() = coroutineRule.runBlockingTest {
        val mokeResponse =
            mockWebServer.enqueueResponse(NetworkTestConstants.ALBUMS_JSON_FILE_NAME, HTTP_OK)
        val actualResponse = apiService.getAlbums(2)
        assertThat(mokeResponse.toString().contains("200")).isEqualTo(
            actualResponse.code().toString().contains("200")
        )
    }

    @Test
    fun `check if no internet`() = coroutineRule.runBlockingTest {
        try {
            apiService.getAlbums(2).body()
        } catch (e: Exception) {
            val httpStatusEnum = baseAPIRequest.parseException<Unit>(e)
            assertThat(httpStatusEnum.httpStatusEnum).isEqualTo(HttpStatusEnum.NO_INTERNET)
        }
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }
}