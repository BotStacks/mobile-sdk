package ai.botstacks.sdk.utils

inline fun <T> Iterable<T>.contains(predicate: (T) -> Boolean): Boolean {
    return firstOrNull(predicate) != null
}