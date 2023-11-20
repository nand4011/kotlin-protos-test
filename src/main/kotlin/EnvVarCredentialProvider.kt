import javax.annotation.Nonnull

/**
 * Parses connection and authentication information from a JWT read from an environment variable.
 */
class EnvVarCredentialProvider : StringCredentialProvider {
    /**
     * Parses connection and authentication information from an authentication token read from the
     * given environment variable.
     *
     * @param envVarName the environment variable containing the Momento authentication token.
     */
    constructor(@Nonnull envVarName: String?) : super(System.getenv(envVarName), null, null)

    /**
     * Parses connection and authentication information from an authentication token read from the
     * given environment variable.
     *
     * @param envVarName the environment variable containing the Momento authentication token.
     * @param controlHost URI to use for control plane operations.
     * @param cacheHost URI to use for data plane operations.
     */
    constructor(
        @Nonnull envVarName: String?, controlHost: String?, cacheHost: String?
    ) : super(System.getenv(envVarName), controlHost, cacheHost)
}
