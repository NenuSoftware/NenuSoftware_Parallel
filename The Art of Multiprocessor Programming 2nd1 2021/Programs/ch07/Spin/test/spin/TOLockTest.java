/*
 * TOTest.java
 * JUnit based test
 *
 * Created on January 12, 2006, 8:27 PM
 */

package spin;

import java.util.concurrent.atomic.AtomicInteger;
import junit.framework.*;

/**
 * Crude & inadequate test of lock class.
 * @author Maurice Herlihy
 */
public class TOLockTest extends TestCase {
  private final static int TIMEOUT = 100;
  private final static int THREADS = 8;
  private final static int COUNT = 8 * 1024;
  private final static int PER_THREAD = COUNT / THREADS;
  Thread[] thread = new Thread[THREADS];
  int counter = 0;
  AtomicInteger failed = new AtomicInteger(0);
  
  TOLock instance = new TOLock();
  
  public TOLockTest(String testName) {
    super(testName);
  }
  
  public static Test suite() {
    TestSuite suite = new TestSuite(TOLockTest.class);
    
    return suite;
  }
  
  public void testParallel() throws Exception {
    System.out.println("locking");
    ThreadID.reset();
    for (int i = 0; i < THREADS; i++) {
      thread[i] = new MyThread();
    }
    for (int i = 0; i < THREADS; i++) {
      thread[i].start();
    }
    for (int i = 0; i < THREADS; i++) {
      thread[i].join();
    }
    
    assertEquals(counter, COUNT);
  }
  
  public void testTimeout() throws Exception {
    System.out.println("timeout");
    ThreadID.reset();
    for (int i = 0; i < THREADS; i++) {
      thread[i] = new TOThread();
    }
    for (int i = 0; i < THREADS; i++) {
      thread[i].start();
    }
    for (int i = 0; i < THREADS; i++) {
      thread[i].join();
    }
    
    assertEquals(counter, COUNT - failed.get());
    System.out.printf("timeouts: %d\n", failed.get());
  }
  
  class MyThread extends Thread {
    public void run() {
      for (int i = 0; i < PER_THREAD; i++) {
        instance.lock();
        try {
          counter = counter + 1;
        } finally {
          instance.unlock();
        }
      }
    }
  }
  class TOThread extends Thread {
    public void run() {
      for (int i = 0; i < PER_THREAD; i++) {
        boolean ok = instance.tryLock();
        if (ok) {
          try {
            counter = counter + 1;
            // force others to time out
            synchronized(this) {
              try {
                wait(TIMEOUT);
              } catch (InterruptedException ex) {
                ex.printStackTrace();
              }
            }
          } finally {
            instance.unlock();
          }
        } else {
          failed.getAndIncrement();
        }
      }
    }
  }
}
