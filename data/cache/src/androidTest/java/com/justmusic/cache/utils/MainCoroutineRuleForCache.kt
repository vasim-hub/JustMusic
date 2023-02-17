@file:OptIn(ExperimentalCoroutinesApi::class)

package com.justmusic.cache.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class MainCoroutineRuleForCache(
    val testDispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        super.starting(description)
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        super.finished(description)
        Dispatchers.resetMain()
    }
}

@ExperimentalCoroutinesApi
fun MainCoroutineRuleForCache.runBlockingTest(block: suspend () -> Unit) = runTest(this.testDispatcher) {
    block()
}
