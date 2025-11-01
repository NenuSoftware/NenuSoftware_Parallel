package software;
/*
 * HelloWorld.java
 *
 * Created on January 5, 2006, 6:12 PM
 *
 * From "Multiprocessor Synchronization and Concurrent Data Structures",
 * by Maurice Herlihy and Nir Shavit.
 * Copyright 2006 Elsevier Inc. All rights reserved.
 */

/**
 * Illustrates threads and anonymous classes. Test by running main().
 * @author Maurice Herlihy
 */
public class HelloWorld {
  
  /** Creates a new instance of HelloWorld */
  public HelloWorld() {
  }
  
  /**
   * Test program.
   * @param args ignored
   * @throws java.lang.InterruptedException better not happen
   */
  public static void main(String[] args) throws InterruptedException {
    Thread[] thread = new Thread[8];
    // create threads
    for (int i = 0; i < thread.length; i++) {
      final String message = "Hello world from thread " + i;
      thread[i] = new Thread(new Runnable() {
        public void run() {
          System.out.println(message);
        }
      });
    }
    // start threads
    for (int i = 0; i < thread.length; i++) {
      thread[i].start();
    }
    // wait for them to finish
    for (int i = 0; i < thread.length; i++) {
      thread[i].join();
    }
    System.out.println("done!");
  }
  
}
