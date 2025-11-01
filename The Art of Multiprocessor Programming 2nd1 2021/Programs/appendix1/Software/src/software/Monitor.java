/*
 * Monitor.java
 *
 * Created on January 5, 2006, 10:36 PM
 *
 * From "Multiprocessor Synchronization and Concurrent Data Structures",
 * by Maurice Herlihy and Nir Shavit.
 * Copyright 2006 Elsevier Inc. All rights reserved.
 */

package software;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * C#-style monitor implemented in Java (solution to exercise)
 * @author Maurice Herlihy
 */
public class Monitor {
  static Map<Object,State> map = new HashMap<Object,State>();
  static class State{
    Lock lock;
    Condition condition;
    public State() {
      this.lock      = new ReentrantLock();
      this.condition = lock.newCondition();
    }
  }
  
  private static State  init(Object object) {
    State state = map.get(object);
    if (state == null) {
      state = new State();
      map.put(object, state);
    }
    return state;
  }
  
  public static void enter(Object object) {
    State state = init(object);
    state.lock.lock();
  }
  
  public static void exit(Object object) {
    State state = init(object);
    state.lock.unlock();
  }
  public static void wait(Object object) throws InterruptedException {
    State state = init(object);
    state.condition.wait();
  }
  public static void notify(Object object) {
    State state = init(object);
    state.condition.signal();
  }
  public static void notifyAll(Object object) {
    State state = init(object);
    state.condition.signalAll();
  }
  
}
