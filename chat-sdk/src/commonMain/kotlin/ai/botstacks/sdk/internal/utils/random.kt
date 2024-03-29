/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.internal.utils

import kotlin.random.Random


internal fun randomImage() = randomImage(300)

internal fun randomImage(size: Int) = "https://picsum.photos/seed/${Random.nextInt()}/$size"


internal fun randomImage(width: Int, height: Int) =
  "https://picsum.photos/seed/${Random.nextInt()}/$width/$height"


internal fun chance(num: Int, outOf: Int): Boolean {
  return Random.nextInt(outOf) + 1 <= num
}

internal fun <T> random(count: Int, item: () -> T): List<T> {
  val num = Random.nextInt(count + 1)
  if (num > 0) {
    return (0..(num - 1)).map({ item() })
  }
  return listOf()
}

internal fun <T> randomAmount(from: Collection<T>): List<T> {
  if (from.isEmpty()) {
    return listOf()
  }
  var count = Random.nextInt(from.size + 1)
  var els = from.toMutableList()
  if (count > 0) {
    var ret = mutableListOf<T>()
    while (count > 0) {
      val i = Random.nextInt(els.size)
      count = count.minus(1)
      ret.add(
        els.removeAt(i)
      )
    }
    return ret
  } else {
    return listOf()
  }
}