package com.frisky.utils;

import java.io.Serializable;

import androidx.annotation.NonNull;

/**
 * It is important to notice that, in an ANRError, all the "Caused by" are not really the cause
 * of the exception. Each "Caused by" is the stack trace of a running thread. Note that the main
 * thread always comes first.
 */
public class ANRError extends Error {

    private static class $Error implements Serializable {
        private final String _name;
        private final StackTraceElement[] _stackTrace;

        private class _Thread extends Throwable {
            private _Thread(_Thread other) {
                super(_name, other);
            }

            @Override
            @NonNull
            public Throwable fillInStackTrace() {
                setStackTrace(_stackTrace);
                return this;
            }
        }

        private $Error(String name, StackTraceElement[] stackTrace) {
            _name = name;
            _stackTrace = stackTrace;
        }
    }

    private static final long serialVersionUID = 1L;

    /**
     * The minimum duration, in ms, for which the main thread has been blocked. May be more.
     */
    @SuppressWarnings("WeakerAccess")
    public final long duration;

    private ANRError($Error._Thread st, long duration) {
        super("Application Not Responding for at least " + duration + " ms.", st);
        this.duration = duration;
    }

    @Override
    @NonNull
    public Throwable fillInStackTrace() {
        setStackTrace(new StackTraceElement[]{});
        return this;
    }

    @NonNull
    static ANRError New(long duration, Thread thread) {
        final StackTraceElement[] stackTrace = thread.getStackTrace();
        return new ANRError(new $Error(getThreadTitle(thread), stackTrace).new _Thread(null), duration);
    }

    private static String getThreadTitle(Thread thread) {
        return thread.getName() + " (state = " + thread.getState() + ")";
    }
}