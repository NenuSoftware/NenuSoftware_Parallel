/*
 * SafeBooleanMRSWRegisterTest.java
 * JUnit based test
 *
 * Created on January 12, 2006, 8:27 PM
 */

package register;

import junit.framework.*;

/**
 *
 * @author Maurice Herlihy
 */
public class SafeBooleanMRSWRegisterTest extends TestCase {
  private final static int THREADS = 8;
  Thread[] thread = new Thread[THREADS];
  
  SafeBooleanMRSWRegister instance = new SafeBooleanMRSWRegister(THREADS);
  
  public SafeBooleanMRSWRegisterTest(String testName) {
    super(testName);
  }
  
  public static Test suite() {
    TestSuite suite = new TestSuite(SafeBooleanMRSWRegisterTest.class);
    
    return suite;
  }
  
  /**
   * Sequential calls.
   */
  public void testSequential() {
    System.out.println("sequential read and write");
    instance.write(true);
    boolean result = instance.read();
    assertEquals(result, true);
  }
  /**
   * Parallel reads
   */
  public void testParallel() throws Exception {
    instance.write(false);
    instance.write(true);
    System.out.println("parallel read");
    for (int i = 0; i < THREADS; i++) {
      thread[i] = new ReadThread();
    }
    for (int i = 0; i < THREADS; i ++) {
      thread[i].start();
    }
    for (int i = 0; i < THREADS; i ++) {
      thread[i].join();
    }
  }
  class ReadThread extends Thread {
    public void run() {
      if (!instance.read()) {
        fail("register returns false");
      }
    }
  }
  
}
