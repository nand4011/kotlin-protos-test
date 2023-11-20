import javax.annotation.Nonnull

/**
 * Specifies the fields that are required for a Momento client to connect to and authenticate with
 * the Momento service.
 */
abstract class CredentialProvider {
    /**
     * Gets the token used to authenticate to Momento.
     *
     * @return The token.
     */
    abstract val authToken: String

    /**
     * Gets the endpoint with which the Momento client will connect to the Momento control plane.
     *
     * @return The endpoint.
     */
    abstract val controlEndpoint: String

    /**
     * Gets the endpoint with which the Momento client will connect to the Momento data plane.
     *
     * @return The endpoint.
     */
    abstract val cacheEndpoint: String

    companion object {
        /**
         * Creates a CredentialProvider using the provided auth token.
         *
         * @param authToken A Momento auth token.
         * @return The provider.
         */
        fun fromString(authToken: String): CredentialProvider {
            return StringCredentialProvider(authToken)
        }

        /**
         * Creates a CredentialProvider by loading an auth token from the provided environment variable.
         *
         * @param envVar An environment variable containing a Momento auth token.
         * @return The provider.
         */
        fun fromEnvVar(envVar: String): CredentialProvider {
            return EnvVarCredentialProvider(envVar)
        }
    }
}
