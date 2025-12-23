package com.meir.clipscribe.util

object Formatters {
  fun secToMmSs(sec: Int): String {
    val m = sec / 60
    val s = sec % 60
    return "%02d:%02d".format(m, s)
  }

  fun msToMmSs(ms: Long): String = secToMmSs((ms / 1000).toInt())
}
