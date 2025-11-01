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
 * Illustrates use of Java monitors.
 * @author mph
 */
public class Queue<T> {
  int head = 0;                     // next item to dequeue
  int tail = 0;                     // next empty slot
  Object[] items;
  
  /** Creates a new instance of Queue */
  public Queue(int capacity) {
    head = tail = 0;
    items = new Object[capacity];
  }
  
  /**
   * Remove and return element at head of the queue.
   * @return newly-removed item at head of queue
   */
  public T deq() {
    while (head == tail) {
      try {
        wait();
      } catch (InterruptedException ex) {}
    }
    T y = (T)items[(head++) % items.length];
    notify();
    return y;
  }
  /**
   * Append item to tail of queue
   * @param x 
   */
  public synchronized void enq(T x) {
    while (tail - head == items.length) {
      try {
        wait();
      } catch (InterruptedException ex) {}
    }
    items[(tail++) % items.length] = x;
    notify();
  }
  
}
