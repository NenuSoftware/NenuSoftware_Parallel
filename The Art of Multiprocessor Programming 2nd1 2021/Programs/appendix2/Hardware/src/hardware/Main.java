/*
 * Main.java
 *
 * Created on April 30, 2006, 10:03 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package hardware;

import java.util.concurrent.locks.Lock;

/**
 *
 * @author mph
 */
public class Main {
  static int counter = 0;
  static final int LIMIT = 1024 * 1024;
  
  /** Creates a new instance of Main */
  public Main() {
  }
  
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    int[] threads = {2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28};
    Lock[] locks = {new TASLock(), new TTASLock()};
    Worker[] workers;
    System.out.println("hello");
    for (int t : threads) {
      System.out.printf("%d\t", t);
    }
    System.out.println();
    for (Lock lock : locks) {
      for (int thread : threads) {
        long duration = trial(thread, lock);
        System.out.printf("%d\t", duration);
      }
      System.out.println();
    }
    // TODO code application logic here
  }
  
  private static long trial(int threads, Lock lock) {
    Worker[] workers = new Worker[threads];
    long start = System.currentTimeMillis();
    counter = 0;
    for (int i = 0; i < workers.length; i++) {
      workers[i] = new Worker(lock);      
    }
    for (int i = 0; i < workers.length; i++) {
      workers[i].start();      
    }
    for (int i = 0; i < workers.length; i++) {
      try {
        workers[i].join();      
      } catch (InterruptedException ex) {
        System.exit(0);
      }      
    }
    long stop = System.currentTimeMillis();
    return stop - start;
  }
  
  static class Worker extends java.lang.Thread {
    Lock lock;
    Worker(Lock lock) {
      this.lock = lock;
    }
    
    public void run() {
      while (true) {
        lock.lock();
        try {
          if (counter == LIMIT) {
            return;
          } else {
            counter++;
          }
        } finally {
          lock.unlock();
        }
      }
    }
    
  }
  
}
