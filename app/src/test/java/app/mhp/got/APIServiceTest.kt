import app.mhp.got.networking.APIService
import app.mhp.got.networking.RequestStatus
import app.mhp.got.networking.adapters.CallAdapterFactory
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.net.HttpURLConnection
import java.util.concurrent.TimeUnit

class APIServiceTest {

    private val mockWebServer = MockWebServer()
    private var apiService: APIService

    init {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
            .followRedirects(true)
            .followSslRedirects(true)
            .addInterceptor(httpLoggingInterceptor)
            .readTimeout(5, TimeUnit.SECONDS)
            .connectTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)

        apiService = Retrofit.Builder()
            .baseUrl("http://localhost/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addCallAdapterFactory(CallAdapterFactory(null))
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient.build())
            .build()
            .create(APIService::class.java)
    }

    @Before
    fun start() {
        mockWebServer.start()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `get successful house response`() {
        val dispatcher = object : Dispatcher() {
            @Throws(InterruptedException::class)
            override fun dispatch(request: RecordedRequest): MockResponse {
                return when (request.path) {
                    APIService.Endpoints.ENDPOINT_HOUSES -> {
                        MockResponse()
                            .setResponseCode(HttpURLConnection.HTTP_OK)
                            .setBody(javaClass.getResource("successful_house_response.json").readText())
                    }
                    else -> MockResponse().setResponseCode(404)
                }
            }
        }
        mockWebServer.dispatcher = dispatcher

        runBlocking {
            val response = apiService.getHouses(1)

            if(response is RequestStatus.Success){
                assertTrue(response.data.isNotEmpty())
                assertTrue(response.data.first().name == "House Ashford")
            }
        }
    }

    @Test
    fun `get empty house response`() {
        val dispatcher = object : Dispatcher() {
            @Throws(InterruptedException::class)
            override fun dispatch(request: RecordedRequest): MockResponse {
                return when (request.path) {
                    APIService.Endpoints.ENDPOINT_HOUSES -> {
                        MockResponse()
                            .setResponseCode(HttpURLConnection.HTTP_OK)
                            .setBody(javaClass.getResource("empty_house_response.json").readText())
                    }
                    else -> MockResponse().setResponseCode(404)
                }
            }
        }
        mockWebServer.dispatcher = dispatcher

        runBlocking {
            val response = apiService.getHouses(1)

            if(response is RequestStatus.Success){
                assertTrue(response.data.isEmpty())
            }
        }
    }

}
