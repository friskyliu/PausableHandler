/*
 * Copyright (c) 2022 friskyliu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.frisky.utils

import android.os.*
import android.util.Log
import android.util.Printer
import androidx.annotation.RequiresApi
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

@Suppress("unused")
open class PausableHandler{
    companion object {
        private const val TASK_BLOCKING_WATCH = false
        private const val TASK_BLOCKING_DURATION = 3000L   /* milliseconds */

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
    private val msgCallback = object : IMessageCallback {
        override fun handleMessageCallback(msg: Message) {
            this@PausableHandler.handleMessage(msg)
        }

        override fun dispatchMessageCallback(msg: Message) {
            this@PausableHandler.dispatchMessage(msg)
        }

        override fun sendMessageAtTimeCallback(msg: Message, uptimeMillis: Long): Boolean {
            return this@PausableHandler.sendMessageAtTime(msg, uptimeMillis)
        }
    }

    constructor() {
        handler = InnerHandler(msgCallback)
    }

    constructor(callback: Handler.Callback?) {
        handler = InnerHandler(callback, msgCallback)
    }

    constructor(looper: Looper) {
        handler = InnerHandler(looper, msgCallback)
    }

    constructor(looper: Looper, callback: Handler.Callback?) {
        handler = InnerHandler(looper, callback, msgCallback)
    }

    fun resume() = handler.resume()

    fun pause() = handler.pause()

    fun isPaused(): Boolean = handler.isPaused()

    fun runningTaskSize(): Int = handler.runningTaskSize()

    fun waitingTaskSize(): Int = handler.waitingTaskSize()

    open fun dispatchMessage(msg: Message) = handler.dispatchMessageNew(msg)

    open fun handleMessage(msg: Message) {}

    open fun sendMessageAtTime(msg: Message, uptimeMillis: Long): Boolean = handler.sendMessageAtTimeNew(msg, uptimeMillis)

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
        /*handler.removeEqualMessages(what, obj)
        handler.removeCacheWhat(what, obj, true)*/
        TODO("to do")
    }

    fun removeCallbacksAndMessages(token: Any?) {
        handler.removeCallbacksAndMessages(token)
        handler.removeCacheObj(token)
    }

    fun removeCallbacksAndEqualMessages(token: Any?) {
        /*handler.removeCallbacksAndEqualMessages(token)
        handler.removeCacheObj(token, true) */
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
        /*var ret = runningTaskSize() > 0 || waitingTaskSize() > 0
        if (!ret) {
            ret = handler.hasMessagesOrCallbacks()
        }
        return ret*/
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
        /*var ret = handler.hasMessageCacheWhat(what, obj, true)
        if (!ret) {
            ret = handler.hasEqualMessages(what, obj)
        }
        return ret*/
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
        /*handler.dumpMine(pw, prefix)*/
        TODO("to do")
    }

    override fun toString(): String = handler.toString()

    private interface IMessageCallback {
        fun handleMessageCallback(msg: Message)
        fun dispatchMessageCallback(msg: Message)
        fun sendMessageAtTimeCallback(msg: Message, uptimeMillis: Long): Boolean
    }

    private class WaitingMsgTime(var delayTime: Long)
    private class RunningMsgTime(var whenTime: Long, var lastSumPauseTime: Long)
    private class InnerHandler : Handler {
        private var startPauseTime = 0L
        private var sumPauseDuration = 0L
        private val waitingQueue = LinkedList<Pair<Message, WaitingMsgTime>>()
        private val runningQueue = LinkedList<Pair<Message, RunningMsgTime>>()
        private var waitingLock = ReentrantReadWriteLock()
        private var runningLock = ReentrantReadWriteLock()
        private val msgCallback: IMessageCallback

        companion object {
            private lateinit var blockingWatchHandler: Handler
            private lateinit var blockingWatchThread: HandlerThread

            init {
                if (TASK_BLOCKING_WATCH) {
                    blockingWatchThread = HandlerThread("TASK-BLOCKING-WATCH")
                    blockingWatchThread.start()
                    blockingWatchHandler = Handler(blockingWatchThread.looper)
                }
            }
        }

        constructor(msgCallback: IMessageCallback) : super() {
            this.msgCallback = msgCallback
        }

        constructor(callback: Callback?, msgCallback: IMessageCallback) : super(callback) {
            this.msgCallback = msgCallback
        }

        constructor(looper: Looper, msgCallback: IMessageCallback) : super(looper) {
            this.msgCallback = msgCallback
        }

        constructor(looper: Looper, callback: Callback?, msgCallback: IMessageCallback) : super(looper, callback) {
            this.msgCallback = msgCallback
        }

        fun resume(): Long {
            var pauseDuration = 0L
            if (startPauseTime != 0L) {
                pauseDuration = SystemClock.uptimeMillis() - startPauseTime
                sumPauseDuration += pauseDuration
                startPauseTime = 0L
            }

            var pair = waitingLock.write { waitingQueue.removeFirstOrNull() }
            while (pair != null) {
                sendMessageAtTime(pair.first, SystemClock.uptimeMillis() + pair.second.delayTime)
                pair = waitingLock.write { waitingQueue.removeFirstOrNull() }
            }
            return pauseDuration
        }

        fun pause() {
            if (startPauseTime == 0L) {
                startPauseTime = SystemClock.uptimeMillis()
            }
        }

        override fun handleMessage(msg: Message) {
            //to PausableHandler.handleMessage
            msgCallback.handleMessageCallback(msg)
        }

        override fun sendMessageAtTime(msg: Message, uptimeMillis: Long): Boolean {
            //to PausableHandler.sendMessageAtTime  -> InnerHandler.sendMessageAtTimeNew
            return msgCallback.sendMessageAtTimeCallback(msg, uptimeMillis)
        }

        //call from PausableHandler.sendMessageAtTime
        fun sendMessageAtTimeNew(msg: Message, uptimeMillis: Long): Boolean {
            if (startPauseTime == 0L) {
                runningLock.write {
                    runningQueue.add(Pair(msg, RunningMsgTime(uptimeMillis, sumPauseDuration)))
                }
                return super.sendMessageAtTime(msg, uptimeMillis)
            }

            waitingLock.write {
                waitingQueue.add(Pair(msg, WaitingMsgTime(uptimeMillis - SystemClock.uptimeMillis())))
            }
            return true
        }

        override fun dispatchMessage(msg: Message) {
            //to PausableHandler.dispatchMessage  -> InnerHandler.dispatchMessageNew
            msgCallback.dispatchMessageCallback(msg)
        }

        //call from PausableHandler.dispatchMessage
        fun dispatchMessageNew(msg: Message) {
            val runningTime = runningLock.write {
                runningQueue.ktRemoveIf { it.first === msg }?.second
            }
            if (runningTime == null) {
                dispatchMessageNow(msg)
                return
            }

            if (startPauseTime != 0L) {
                val delayTime =
                    SystemClock.uptimeMillis() - startPauseTime + sumPauseDuration - runningTime.lastSumPauseTime

                val newMsg = Message.obtain(msg)
                waitingLock.write {
                    waitingQueue.add(Pair(newMsg, WaitingMsgTime(delayTime)))
                }
                return
            }

            if (sumPauseDuration - runningTime.lastSumPauseTime > 0) {
                val newMsg = Message.obtain(msg)
                val whenTime = SystemClock.uptimeMillis() + sumPauseDuration - runningTime.lastSumPauseTime
                sendMessageAtTime(newMsg, whenTime)
                return
            }

            dispatchMessageNow(msg)
        }

        private fun dispatchMessageNow(msg: Message) {
            if (TASK_BLOCKING_WATCH) {
                val watchRunnable = Runnable {
                    Log.e("PausableHandler", "TASK-BLOCKING-WATCH", TaskBlockingError.create(TASK_BLOCKING_DURATION, looper.thread))
                }
                blockingWatchHandler.postDelayed(watchRunnable, TASK_BLOCKING_DURATION)
                super.dispatchMessage(msg)
                blockingWatchHandler.removeCallbacks(watchRunnable)
            } else {
                super.dispatchMessage(msg)
            }
        }

        fun isPaused(): Boolean {
            return startPauseTime != 0L
        }

        fun addWaitingFirst(msg: Message): Boolean {
            waitingLock.write {
                waitingQueue.add(0, Pair(msg, WaitingMsgTime(0)))
            }
            return true
        }

        fun removeCacheObj(token: Any?, equal: Boolean = false) {
            waitingLock.write {
                waitingQueue.removeAll {
                    objEquals(it.first.obj, token, equal)
                }
            }
            runningLock.write {
                runningQueue.removeAll {
                    objEquals(it.first.obj, token, equal)
                }
            }
        }

        fun removeCacheWhat(what: Int, token: Any?, equal: Boolean = false) {
            waitingLock.write {
                waitingQueue.removeAll {
                    val msg = it.first
                    msg.what == what && objEquals(msg.obj, token, equal)
                }
            }

            runningLock.write {
                runningQueue.removeAll {
                    val msg = it.first
                    msg.what == what && objEquals(msg.obj, token, equal)
                }
            }
        }

        fun removeCacheCallback(runnable: Runnable, token: Any?, equal: Boolean = false) {
            waitingLock.write {
                waitingQueue.removeAll {
                    val msg = it.first
                    msg.callback === runnable && objEquals(msg.obj, token, equal)
                }
            }

            runningLock.write {
                runningQueue.removeAll {
                    val msg = it.first
                    msg.callback === runnable && objEquals(msg.obj, token, equal)
                }
            }
        }

        fun hasMessageCacheObj(token: Any?, equal: Boolean = false): Boolean {
            return waitingLock.read {
                waitingQueue.find { objEquals(it.first.obj, token, equal) } != null
            }
        }

        fun hasMessageCacheWhat(what: Int, token: Any?, equal: Boolean = false): Boolean {
            return waitingLock.read {
                waitingQueue.find {
                    val msg = it.first
                    msg.what == what && objEquals(msg.obj, token, equal)
                } != null
            }
        }

        fun hasMessageCacheCallback(runnable: Runnable, token: Any?, equal: Boolean = false): Boolean {
            return waitingLock.read {
                waitingQueue.find {
                    val msg = it.first
                    msg.callback === runnable && objEquals(msg.obj, token, equal)
                } != null
            }
        }

        fun runningTaskSize(): Int {
            return runningLock.read {
                runningQueue.size
            }
        }

        fun waitingTaskSize(): Int {
            return waitingLock.read {
                waitingQueue.size
            }
        }

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

    private class TaskBlockingError(throwable: Throwable, duration: Long) :
        Error("Task Blocking for at least $duration ms.", throwable) {

        companion object {
            fun create(duration: Long, thread: Thread): TaskBlockingError {
                val name = "${thread.name} (state = ${thread.state})"
                return TaskBlockingError(TmpError(thread.stackTrace).InnerError(name), duration)
            }
        }

        class TmpError(private val _stackTrace: Array<StackTraceElement>) {
            inner class InnerError(_name: String) :
                Throwable(_name, null) {

                override fun fillInStackTrace(): Throwable {
                    stackTrace = _stackTrace
                    return this
                }
            }
        }

        override fun fillInStackTrace(): Throwable {
            stackTrace = arrayOf()
            return this
        }
    }
}