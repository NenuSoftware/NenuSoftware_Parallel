/*
 * Queue.java
 *
 * Created on January 5, 2006, 6:54 PM
 *
 * From "Multiprocessor Synchronization and Concurrent Data Structures",
 * by Maurice Herlihy and Nir Shavit.
 * Copyright 2006 Elsevier Inc. All rights reserved.
 */

package software;

/**
 * Illustrates use of simulated C# monitors.
 * @author mph
 */
public class MonitorQueue<T> {
  int head = 0;   // next item to dequeue
  int tail = 0;   // next empty slot
  Object[] items; // queue contents
  
  /** Creates a new instance of Queue */
  public MonitorQueue(int capacity) {
    head = tail = 0;
    items = new Object[capacity];
  }
  
  /**
   * Remove and return element at head of the queue.
   * @return newly-removed item at head of queue
   */
  public T deq() {
    Monitor.enter(this);
    try {
      while (head == tail) {
        try {
          Monitor.wait(this);
        } catch (InterruptedException ex) {}
      }
      T y = (T)items[(head++) % items.length];
      Monitor.notifyAll(this);
      return y;
    } finally {
      Monitor.exit(this);
    }
  }
  /**
   * Append item to tail of queue
   * @param x
   */
  public void enq(T x) {
    Monitor.enter(this);
    try {
      while (tail - head == items.length) {
        try {
          Monitor.wait(this);
        } catch (InterruptedException ex) {}
      }
      items[(tail++) % items.length] = x;
      Monitor.notify(this);
    } finally {
      Monitor.exit(this);
    }
  }
  
}
