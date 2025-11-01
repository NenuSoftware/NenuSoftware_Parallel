/*
 * TTASLock.java
 *
 * Created on January 20, 2006, 10:59 PM
 *
 * From "Multiprocessor Synchronization and Concurrent Data Structures",
 * by Maurice Herlihy and Nir Shavit.
 * Copyright 2006 Elsevier Inc. All rights reserved.
 */

package hardware;

/**
 * Test-and-test-and-set lock
 * @author Maurice Herlihy
 */
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.Condition;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
public class TTASLock implements Lock {
  volatile public int state = 0;
  private static final AtomicIntegerFieldUpdater<TTASLock> updater
      = AtomicIntegerFieldUpdater.newUpdater(TTASLock.class, "state");
  public void lock() {
    while (true) {
      while (updater.get(this) != 0) {};  // spin
      if (updater.getAndSet(this, 1) == 0)
        return;
    }
  }
  public void unlock() {
    updater.set(this, 0);
  }
  // Any class that implents Lock must provide these methods.
  public Condition newCondition() {
    throw new java.lang.UnsupportedOperationException();
  }
  public boolean tryLock(long time,
      TimeUnit unit)
      throws InterruptedException {
    throw new java.lang.UnsupportedOperationException();
  }
  public boolean tryLock() {
    throw new java.lang.UnsupportedOperationException();
  }
  public void lockInterruptibly() throws InterruptedException {
    throw new java.lang.UnsupportedOperationException();
  }
}

