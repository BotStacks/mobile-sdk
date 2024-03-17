package ai.botstacks.sdk

enum class LogLevel {
    /** No logs. */
    NONE,

    NETWORK_ONLY,

    DEBUG,

    INFO,
    WARNING,
    ERROR,

    /**
     * Logs everything
     */
    VERBOSE,
}