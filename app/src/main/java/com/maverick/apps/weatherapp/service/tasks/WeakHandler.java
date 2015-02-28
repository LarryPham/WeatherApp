package com.maverick.apps.weatherapp.service.tasks;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;

/**
 * Copyright (C) 2014-2015 Techie Digital Benchwork Inc., Ltd. All rights reserved.
 *
 * Mobility Development Division, Digital Media & Communications Business, Techie Digital
 * Benchwork., Ltd.
 *
 * This software and its documentation are confidential and proprietary information of Techie
 * Digital Benchwork., Ltd.  No part of the software and documents may be copied, reproduced,
 * transmitted, translated, or reduced to any electronic medium or machine-readable form without the
 * prior written consent of Techie Digital Benchwork Inc.
 *
 * Techie Digital Benchwork makes no representations with respect to the contents, and assumes no
 * responsibility for any errors that might appear in the software and documents. This publication
 * and the contents hereof are subject to change without notice.
 *
 * History 2015.Feb.28      Phil Pham         The 1st Sprint Version
 */
public class WeakHandler {

  private final Handler.Callback mCallback;
  private final ExecHandler mExecutor;
  private final ChainedRef mRunnables = new ChainedRef(null);

  public WeakHandler() {
    this.mCallback = null;
    this.mExecutor = new ExecHandler();
  }

  public WeakHandler(@Nullable Handler.Callback callback) {
    this.mCallback = callback;
    this.mExecutor = new ExecHandler(new WeakReference<Handler.Callback>(callback));
  }

  public WeakHandler(@NonNull Looper looper) {
    this.mCallback = null;
    this.mExecutor = new ExecHandler(looper);
  }

  public WeakHandler(@NonNull Looper looper, @NonNull Handler.Callback callback) {
    this.mCallback = callback;
    this.mExecutor = new ExecHandler(looper, new WeakReference<Handler.Callback>(callback));
  }

  public final boolean post(@NonNull Runnable runnable) {
    return mExecutor.post(wrapRunnable(runnable));
  }

  public final boolean postAtTime(@NonNull Runnable runnable, long upTimeMillis) {
    return mExecutor.postAtTime(wrapRunnable(runnable), upTimeMillis);
  }

  public final boolean postAtTime(@NonNull Runnable runnable, Object token, long uptimeMillis) {
    return mExecutor.postAtTime(wrapRunnable(runnable), token, uptimeMillis);
  }

  public final boolean postDelayed(Runnable runnable, long delayedMillis) {
    return mExecutor.postDelayed(wrapRunnable(runnable), delayedMillis);
  }
  public final boolean postAtFrontOfQueue(Runnable runnable) {
    return mExecutor.postAtFrontOfQueue(wrapRunnable(runnable));
  }

  public final void removeCallbacks(Runnable runnable) {
    final ChainedRef runnableRef = mRunnables.findForward(runnable);
    if (runnableRef != null) {
      mExecutor.removeCallbacks(runnableRef.mWrapper);
    }
  }

  public final void removeCallbacks(Runnable runnable, Object token) {
    final ChainedRef runnableRef = mRunnables.findForward(runnable);
    if (runnableRef != null) {
      mExecutor.removeCallbacks(runnableRef.mWrapper, token);
    }
  }

  public final boolean sendMessage(Message msg) {
    return mExecutor.sendMessage(msg);
  }

  public final boolean sendEmptyMessage(int what) {
    return mExecutor.sendEmptyMessage(what);
  }

  public final boolean sendEmptyMessageDelayed(int what, long delayedMillis) {
    return mExecutor.sendEmptyMessageDelayed(what, delayedMillis);
  }

  public final boolean sendEmptyMessageAtTime(int what, long uptimeMillis) {
    return mExecutor.sendEmptyMessageAtTime(what, uptimeMillis);
  }

  public final boolean sendMessageDelayed(Message msg, long delayedMills) {
    return mExecutor.sendMessageDelayed(msg, delayedMills);
  }

  public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
    return mExecutor.sendMessageAtTime(msg, uptimeMillis);
  }

  public final boolean sendMessageAtFrontOfQueue(Message msg) {
    return mExecutor.sendMessageAtFrontOfQueue(msg);
  }

  public final void removeMessages(int what) {
    mExecutor.removeMessages(what);
  }

  public final void removeMessages(int what, Object obj) {
    mExecutor.removeMessages(what, obj);
  }

  public final void removeCallbacksAndMessages(Object token) {
    mExecutor.removeCallbacksAndMessages(token);
  }

  public final boolean hasMessages(int what) {
    return mExecutor.hasMessages(what);
  }

  public final boolean hasMessages(int what, Object obj) {
    return mExecutor.hasMessages(what, obj);
  }

  public final Looper getLooper() {
    return mExecutor.getLooper();
  }

  public final WeakRunnable wrapRunnable(Runnable runnable) {
    final ChainedRef hardRef = ChainedRef.obtain(runnable);
    mRunnables.insertAbove(hardRef);
    return hardRef.mWrapper = new WeakRunnable(new WeakReference<Runnable>(runnable), new WeakReference<ChainedRef>(hardRef));
  }
  static class ExecHandler extends Handler {
    private final WeakReference<Callback> mCallback;

    ExecHandler() {
      this.mCallback = null;
    }

    ExecHandler(WeakReference<Callback> callback) {
      this.mCallback = callback;
    }

    ExecHandler(Looper looper) {
      super(looper);
      mCallback = null;
    }

    ExecHandler(Looper looper, WeakReference<Callback> callback) {
      super(looper);
      mCallback = callback;
    }

    @Override
    public void handleMessage(Message msg) {
      if (mCallback == null) return;
      final Callback callback = mCallback.get();
      if (callback == null) return;
      callback.handleMessage(msg);
    }
  }
  static class WeakRunnable implements Runnable {
    private final WeakReference<Runnable> mDelegate;
    private final WeakReference<ChainedRef> mReference;

    WeakRunnable(WeakReference<Runnable> delegate, WeakReference<ChainedRef> reference) {
      mDelegate = delegate;
      mReference= reference;
    }

    @Override
    public void run() {
      final Runnable delegate = mDelegate.get();
      final ChainedRef reference = mReference.get();
      if (reference != null) {
        reference.remove();
      }

      if (delegate != null) {
        delegate.run();
      }
    }
  }

  static class ChainedRef {
    @Nullable
    ChainedRef mNext;
    @Nullable
    ChainedRef mPrev;
    @Nullable
    Runnable mRunnable;
    @Nullable
    WeakRunnable mWrapper;
    @Nullable
    static ChainedRef sPool;
    static int sPoolSize;
    static final int MAX_POOL_SIZE = 15;

    public ChainedRef(Runnable runnable) {
      this.mRunnable = runnable;
    }

    public void remove() {
      if (mPrev != null) {
        this.mPrev.mNext = mNext;
      }

      if (mNext != null) {
        mNext.mPrev = mPrev;
      }

      this.mPrev = null;
      this.mRunnable = null;
      this.mWrapper = null;
      synchronized (ChainedRef.class) {
        if (sPoolSize > MAX_POOL_SIZE) {
          return;
        }

        this.mNext = sPool;
        sPool = this;
        sPoolSize++;
      }
    }

    public void insertAbove(@NonNull ChainedRef candidate) {
      if (this.mNext != null) {
        this.mNext.mPrev = candidate;
      }

      candidate.mNext = this.mNext;
      this.mNext = candidate;
      candidate.mPrev = this;
    }

    @Nullable
    public ChainedRef findForward(Runnable object) {
      ChainedRef current = this;
      while (current != null) {
        if (current.mRunnable != null) {
          if (current.mRunnable.equals(object)) {
            return current;
          }
        }
        else if (object == null) {
          return current;
        }
        current = current.mNext;
      }
      return null;
    }

    public static ChainedRef obtain(Runnable runnable) {
      ChainedRef result = null;
      synchronized (ChainedRef.class) {
        if (sPool != null) {
          result = sPool;
          sPool = sPool.mNext;
          sPoolSize --;
        }
      }
      if (result != null) {
        result.mRunnable = runnable;
        return result;
      }
      return new ChainedRef(runnable);
    }
  }
}
