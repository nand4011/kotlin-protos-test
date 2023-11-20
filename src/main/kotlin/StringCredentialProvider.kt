import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.nio.charset.StandardCharsets
import java.util.*
import javax.annotation.Nonnull

/** Parses connection and authentication information from a JWT provided as a string.  */
open class StringCredentialProvider @JvmOverloads constructor(
    @Nonnull authToken: String, controlHost: String? = null, cacheHost: String? = null
) : CredentialProvider() {
    private class TokenAndEndpoints(val controlEndpoint: String, val cacheEndpoint: String, val authToken: String)

    final override val authToken: String
    final override val controlEndpoint: String
    final override val cacheEndpoint: String
    /**
     * Parses connection and authentication information from the given token.
     *
     * @param authToken a Momento authentication token.
     * @param controlHost URI to use for control plane operations.
     * @param cacheHost URI to use for data plane operations.
     */
    /**
     * Parses connection and authentication information from the given token.
     *
     * @param authToken a Momento authentication token.
     */
    init {
        val data: TokenAndEndpoints = try {
            processV1Token(authToken)
        } catch (e: NullPointerException) {
            throw Exception("Auth token must not be null")
        }
        this.authToken = data.authToken
        controlEndpoint = controlHost ?: data.controlEndpoint
        cacheEndpoint = cacheHost ?: data.cacheEndpoint
    }

//    private fun processLegacyToken(authToken: String): TokenAndEndpoints {
//        val unsignedAuthToken = stripAuthTokenSignature(authToken)
//        val claims: Claims
//        claims = try {
//            Jwts.parserBuilder().build().parseClaimsJwt(unsignedAuthToken).getBody()
//        } catch (e: Exception) {
//            throw InvalidArgumentException("Unable to parse auth token", e)
//        }
//        val controlEp: String = claims.get(CONTROL_ENDPOINT_CLAIM_NAME, String::class.java)
//            ?: throw InvalidArgumentException("Unable to parse control endpoint from auth token")
//        val cacheEp: String = claims.get(CACHE_ENDPOINT_CLAIM_NAME, String::class.java)
//            ?: throw InvalidArgumentException("Unable to parse cache endpoint from auth token")
//        return TokenAndEndpoints(controlEp, cacheEp, authToken)
//    }

    @Serializable
    data class V1Token(
        @SerialName("endpoint") val host: String,
        @SerialName("api_key") val apiKey: String
    )

    private fun processV1Token(authToken: String): TokenAndEndpoints {
        val decodedBase64Token = Base64.getDecoder().decode(authToken)
        val decodedString = String(decodedBase64Token, StandardCharsets.UTF_8)
        val v1Token = Json.decodeFromString<V1Token>(decodedString)
//        val type = object : TypeToken<Map<String?, String?>?>() {}.type
//        val tokenData: Map<String, String> = Gson().fromJson(decodedString, type)
//        val host = tokenData["endpoint"]
//        val apiKey = tokenData["api_key"]
//        if (host == null || host.isEmpty() || apiKey == null || apiKey.isEmpty()) {
//            throw InvalidArgumentException("Unable to parse auth token")
//        }
        return TokenAndEndpoints("control.${v1Token.host}", "cache.${v1Token.host}", v1Token.apiKey)
    }

    private fun stripAuthTokenSignature(authToken: String?): String {
        if (authToken == null) {
            throw Exception("Auth token must not be null")
        }

        // https://github.com/jwtk/jjwt/issues/280
        val splitToken = authToken.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (splitToken.size < 2) {
            throw Exception("Malformed auth token")
        }
        return splitToken[0] + "." + splitToken[1] + "."
    }

    companion object {
        private const val CONTROL_ENDPOINT_CLAIM_NAME = "cp"
        private const val CACHE_ENDPOINT_CLAIM_NAME = "c"
    }
}
