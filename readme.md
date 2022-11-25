PausableHandler support Pause、Resume、Task Blocking watch。implement all Handler functions

PausableHandler [Code](app/src/main/java/com/frisky/utils/PausableHandler.kt)

------

PausableHandler 支持暂停、恢复、阻塞监听。此类实现了Handler所有函数，可修改类名直接替换。
[源码位置](app/src/main/java/com/frisky/utils/PausableHandler.kt)

------

```kotlin
class PausableHandler {
    companion object {
        /** Task Bocking watch switch */
        private const val TASK_BLOCKING_WATCH = true
        private const val TASK_BLOCKING_DURATION = 3000L   /* milliseconds */
        // ... other code ...
    }
    
    // ... other code ...
    
    fun resume() = handler.resume()

    fun pause() = handler.pause()

    fun isPaused(): Boolean = handler.isPaused()

    fun runningTaskSize(): Int = handler.runningTaskSize()

    fun waitingTaskSize(): Int = handler.waitingTaskSize()

    // ... other code ...
}
```

-----------


