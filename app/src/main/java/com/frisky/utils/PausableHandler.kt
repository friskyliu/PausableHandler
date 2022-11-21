package com.frisky.utils

import android.os.*
import android.util.Printer
import androidx.annotation.RequiresApi
import java.util.concurrent.CopyOnWriteArrayList

@Suppress("unused")
class PausableHandler {
    companion object {
        private var MAIN_THREAD_HANDLER: PausableHandler? = null

        @JvmStatic
        fun createAsync(looper: Looper): PausableHandler = PausableHandler(looper)

        @JvmStatic
        fun createAsync(looper: Looper, callback: Handler.Callback): PausableHandler = PausableHandler(looper, callback)

        @JvmStatic
        fun getMain(): PausableHandler {
            if (MAIN_THREAD_HANDLER == null) {
                MAIN_THREAD_HANDLER = PausableHandler(Looper.getMainLooper())
            }
            return MAIN_THREAD_HANDLER!!
        }

        @JvmStatic
        fun mainIfNull(handler: PausableHandler?): PausableHandler {
            return handler ?: getMain()
        }
    }

    private val handler: InnerHandler

    constructor() {
        handler = InnerHandler()
    }

    constructor(callback: Handler.Callback?) {
        handler = InnerHandler(callback)
    }

    constructor(looper: Looper) {
        handler = InnerHandler(looper)
    }

    constructor(looper: Looper, callback: Handler.Callback?) {
        handler = InnerHandler(looper, callback)
    }

    fun resume() = handler.resume()

    fun pause() = handler.pause()

    fun runningTaskSize() = handler.runningTaskSize()

    fun waitingTaskSize() = handler.waitingTaskSize()

    open fun dispatchMessage(msg: Message) = handler.dispatchMessage(msg)

    open fun handleMessage(msg: Message) = handler.handleMessage(msg)

    fun getMessageName(message: Message) = handler.getMessageName(message)

    fun obtainMessage(): Message = handler.obtainMessage()

    fun obtainMessage(what: Int) = handler.obtainMessage(what)

    fun obtainMessage(what: Int, obj: Any?) = handler.obtainMessage(what, obj)

    fun obtainMessage(what: Int, arg1: Int, arg2: Int): Message = handler.obtainMessage(what, arg1, arg2)

    fun obtainMessage(what: Int, arg1: Int, arg2: Int, obj: Any?): Message = handler.obtainMessage(what, arg1, arg2, obj)

    fun post(r: Runnable): Boolean = handler.post(r)

    fun postAtTime(r: Runnable, uptimeMillis: Long): Boolean = handler.postAtTime(r, uptimeMillis)

    fun postAtTime(r: Runnable, token: Any?, uptimeMillis: Long): Boolean = handler.postAtTime(r, token, uptimeMillis)

    fun postDelayed(r: Runnable, delayMillis: Long): Boolean = handler.postDelayed(r, delayMillis)

    @RequiresApi(Build.VERSION_CODES.P)
    fun postDelayed(r: Runnable, what: Int, delayMillis: Long): Boolean = handler.postDelayed(r, what, delayMillis)

    @RequiresApi(Build.VERSION_CODES.P)
    fun postDelayed(r: Runnable, token: Any?, delayMillis: Long): Boolean = handler.postDelayed(r, token, delayMillis)

    fun postAtFrontOfQueue(r: Runnable): Boolean {
        TODO("to do")
    }

    fun runWithScissors(r: Runnable, timeout: Long): Boolean {
        TODO("to do")
    }

    fun removeCallbacks(r: Runnable) {
        handler.removeCacheCallback(r, null)
        handler.removeCallbacks(r)
    }

    fun removeCallbacks(r: Runnable, token: Any?) {
        handler.removeCacheCallback(r, token)
        handler.removeCallbacks(r, token)
    }

    fun sendMessage(msg: Message): Boolean = handler.sendMessage(msg)

    fun sendEmptyMessage(what: Int): Boolean = handler.sendEmptyMessage(what)

    fun sendEmptyMessageDelayed(what: Int, delayMillis: Long): Boolean = handler.sendEmptyMessageDelayed(what, delayMillis)

    fun sendEmptyMessageAtTime(what: Int, uptimeMillis: Long): Boolean = handler.sendEmptyMessageAtTime(what, uptimeMillis)

    fun sendMessageDelayed(msg: Message, delayMillis: Long): Boolean = handler.sendMessageDelayed(msg, delayMillis)

    open fun sendMessageAtTime(msg: Message, uptimeMillis: Long): Boolean = handler.sendMessageAtTime(msg, uptimeMillis)

    fun sendMessageAtFrontOfQueue(msg: Message): Boolean {
        if (handler.isPaused()) {
            handler.addWaitingFirst(msg)
            return true
        }
        return handler.sendMessageAtFrontOfQueue(msg)
    }

    fun executeOrSendMessage(msg: Message): Boolean {
        /* if (handler.isPaused()) {
             handler.sendMessage(msg)
             return true
         }
         return handler.executeOrSendMessage(msg) */
        TODO("to do")
    }

    fun removeMessages(what: Int) {
        handler.removeMessages(what)
        handler.removeCacheWhat(what, null)
    }

    fun removeMessages(what: Int, obj: Any?) {
        handler.removeMessages(what, obj)
        handler.removeCacheWhat(what, obj)
    }

    fun removeEqualMessages(what: Int, obj: Any?) {
        TODO("to do")
    }

    fun removeCallbacksAndMessages(token: Any?) {
        handler.removeCallbacksAndMessages(null)
        handler.removeCacheObj(null)
    }

    fun removeCallbacksAndEqualMessages(token: Any?) {
        TODO("to do")
    }

    fun hasMessages(what: Int): Boolean {
        var ret = handler.hasMessageCacheWhat(what, null)
        if (!ret) {
            ret = handler.hasMessages(what, null)
        }
        return ret
    }

    fun hasMessagesOrCallbacks(): Boolean {
        TODO("to do")
    }

    fun hasMessages(what: Int, obj: Any?): Boolean {
        var ret = handler.hasMessageCacheWhat(what, obj)
        if (!ret) {
            ret = handler.hasMessages(what, obj)
        }
        return ret
    }

    fun hasEqualMessages(what: Int, obj: Any?): Boolean {
        TODO("to do")
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun hasCallbacks(r: Runnable): Boolean {
        var ret = handler.hasMessageCacheCallback(r, null)
        if (!ret) {
            ret = handler.hasCallbacks(r)
        }
        return ret
    }

    fun getLooper(): Looper = handler.getLooper()

    fun dump(pw: Printer, prefix: String) = handler.dump(pw, prefix)

    fun dumpMine(pw: Printer, prefix: String) {
        TODO("to do")
    }

    override fun toString(): String = handler.toString()

    private class WaitingMsgTime(var delayTime: Long)
    private class RunningMsgTime(var whenTime: Long, var lastSumPauseTime: Long)
    private class InnerHandler : Handler {
        private var startPauseTime = 0L
        private var sumPauseDuration = 0L
        private val waitingQueue = CopyOnWriteArrayList<Pair<Message, WaitingMsgTime>>()
        private val runningQueue = CopyOnWriteArrayList<Pair<Message, RunningMsgTime>>()

        constructor() : super()
        constructor(callback: Callback?) : super(callback)
        constructor(looper: Looper) : super(looper)
        constructor(looper: Looper, callback: Callback?) : super(looper, callback)

        fun resume(): Long {
            var pauseDuration = 0L
            if (startPauseTime != 0L) {
                pauseDuration = SystemClock.uptimeMillis() - startPauseTime
                sumPauseDuration += pauseDuration
                startPauseTime = 0L
            }

            var pair = waitingQueue.removeFirstOrNull()
            while (pair != null) {
                sendMessageAtTime(pair.first, SystemClock.uptimeMillis() + pair.second.delayTime)
                pair = waitingQueue.removeFirstOrNull()
            }

            return pauseDuration
        }

        fun pause() {
            if (startPauseTime == 0L) {
                startPauseTime = SystemClock.uptimeMillis()
            }
        }

        override fun sendMessageAtTime(msg: Message, uptimeMillis: Long): Boolean {
            if (startPauseTime == 0L) {
                runningQueue.add(Pair(msg, RunningMsgTime(uptimeMillis, sumPauseDuration)))
                return super.sendMessageAtTime(msg, uptimeMillis)
            }

            waitingQueue.add(Pair(msg, WaitingMsgTime(uptimeMillis - SystemClock.uptimeMillis())))
            return true
        }

        override fun dispatchMessage(msg: Message) {
            val runningTime = runningQueue.ktRemoveIf { it.first === msg }?.second
            if (runningTime == null) {
                super.dispatchMessage(msg)
                return
            }

            if (startPauseTime != 0L) {
                val delayTime = SystemClock.uptimeMillis() - startPauseTime + sumPauseDuration - runningTime.lastSumPauseTime

                val newMsg = Message.obtain(msg)
                waitingQueue.add(Pair(newMsg, WaitingMsgTime(delayTime)))
                return
            }

            if (sumPauseDuration - runningTime.lastSumPauseTime > 0) {
                val newMsg = Message.obtain(msg)
                val whenTime = SystemClock.uptimeMillis() + sumPauseDuration - runningTime.lastSumPauseTime
                sendMessageAtTime(newMsg, whenTime)
                return
            }


            super.dispatchMessage(msg)
        }

        fun isPaused(): Boolean {
            return startPauseTime != 0L
        }

        fun addWaitingFirst(msg: Message): Boolean {
            waitingQueue.add(0, Pair(msg, WaitingMsgTime(0)))
            return true
        }

        fun removeCacheObj(token: Any?, equal: Boolean = false) {
            waitingQueue.removeAll {
                objEquals(it.first.obj, token, equal)
            }
            runningQueue.removeAll {
                objEquals(it.first.obj, token, equal)
            }
        }

        fun removeCacheWhat(what: Int, token: Any?, equal: Boolean = false) {
            waitingQueue.removeAll {
                val msg = it.first
                msg.what == what && objEquals(msg.obj, token, equal)
            }

            runningQueue.removeAll {
                val msg = it.first
                msg.what == what && objEquals(msg.obj, token, equal)
            }
        }

        fun removeCacheCallback(runnable: Runnable, token: Any?, equal: Boolean = false) {
            waitingQueue.removeAll {
                val msg = it.first
                msg.callback === runnable && objEquals(msg.obj, token, equal)
            }

            runningQueue.removeAll {
                val msg = it.first
                msg.callback === runnable && objEquals(msg.obj, token, equal)
            }
        }

        fun hasMessageCacheObj(token: Any?, equal: Boolean = false): Boolean {
            return waitingQueue.find { objEquals(it.first.obj, token, equal) } != null
        }

        fun hasMessageCacheWhat(what: Int, token: Any?, equal: Boolean = false): Boolean {
            return waitingQueue.find {
                val msg = it.first
                msg.what == what && objEquals(msg.obj, token, equal)
            } != null
        }

        fun hasMessageCacheCallback(runnable: Runnable, token: Any?, equal: Boolean = false): Boolean {
            return waitingQueue.find {
                val msg = it.first
                msg.callback === runnable && objEquals(msg.obj, token, equal)
            } != null
        }

        fun runningTaskSize() = runningQueue.size

        fun waitingTaskSize() = waitingQueue.size

        private fun <T> MutableList<T>.ktRemoveIf(condition: (t: T) -> Boolean): T? {
            this.forEach {
                if (condition(it) && this.remove(it)) {
                    return it
                }
            }
            return null
        }

        private fun objEquals(obj: Any?, token: Any?, equal: Boolean): Boolean {
            if (equal) {
                return (token == null && obj == null) || token === obj
            }

            return token == null || token === obj
        }
    }
}