/*
 * HCLHLockTest.java
 * JUnit based test
 *
 * Created on April 11, 2006, 10:54 PM
 */

package spin;

import junit.framework.*;

/**
 *
 * @author mph
 */
public class HCLHLockTest extends TestCase {
  private final static int THREADS = 32;
  private final static int COUNT = 32 * 128;
  private final static int PER_THREAD = COUNT / THREADS;
  Thread[] thread = new Thread[THREADS];
  int counter = 0;
  
  HCLHLock instance = new HCLHLock();
  
  public HCLHLockTest(String testName) {
    super(testName);
  }
  
  public static Test suite() {
    TestSuite suite = new TestSuite(HCLHLockTest.class);
    
    return suite;
  }
  
  public void testParallel() throws Exception {
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
  
  class MyThread extends Thread {
    public void run() {
      for (int i = 0; i < PER_THREAD; i++) {
        instance.lock();
        try {
          counter = counter + 1;
          System.out.println(counter);
        } finally {
          instance.unlock();
        }
      }
    }
  }
}
