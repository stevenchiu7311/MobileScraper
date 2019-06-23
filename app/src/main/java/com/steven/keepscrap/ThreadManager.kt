package com.steven.keepscrap

import java.util.concurrent.Executors

/**
 * Created by steven on 2019/5/16.
 */
object ThreadManager {
    var singleExecutor = Executors.newSingleThreadExecutor()
}