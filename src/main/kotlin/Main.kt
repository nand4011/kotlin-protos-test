import com.google.protobuf.ByteString
import grpc.cache_client.*
import io.grpc.ManagedChannelBuilder
import io.grpc.Metadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {

    val authToken = CredentialProvider.fromEnvVar("MOMENTO_API_KEY")

    val builder = ManagedChannelBuilder.forAddress(authToken.cacheEndpoint, 443)
    builder.useTransportSecurity()
    builder.disableRetry()

    val channel = builder.executor(Dispatchers.IO.asExecutor()).build()

//    val pingStub = PingGrpcKt.PingCoroutineStub(channel)
//    val response = pingStub.ping(_PingRequest.newBuilder().build())
//    println(response)

    val metadata = Metadata()
    metadata.put(Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER), authToken.authToken)
    metadata.put(Metadata.Key.of("cache", Metadata.ASCII_STRING_MARSHALLER), "cache")

    val coroutineStub = ScsGrpcKt.ScsCoroutineStub(channel)

    val key = ByteString.copyFromUtf8("key1");
    val setRequest = _SetRequest.newBuilder().setCacheKey(key)
        .setCacheBody(ByteString.copyFromUtf8("value1"))
        .setTtlMilliseconds(100000L).build()
    val setResponse = coroutineStub.set(setRequest, metadata)
    println(setResponse)

    delay(2000)

    val getRequest = _GetRequest.newBuilder().setCacheKey(key).build()
    val getResponse = coroutineStub.get(getRequest, metadata)
    println(getResponse)
}

