/*
 * AtomicMRMWRegisterTest.java
 * JUnit based test
 *
 * Created on January 12, 2006, 8:27 PM
 */

package register;

import junit.framework.*;

/**
 *
 * @author mph
 */
public class AtomicMRMWRegisterTest extends TestCase {
  private final static int THREADS = 8;
  private final static int FIRST = 11;
  private final static int SECOND = 22;
  Thread[] thread = new Thread[THREADS];
  
  AtomicMRMWRegister<Integer> instance = new AtomicMRMWRegister<Integer>(THREADS, THREADS);
  
  public AtomicMRMWRegisterTest(String testName) {
    super(testName);
  }
  
  public static Test suite() {
    TestSuite suite = new TestSuite(AtomicMRMWRegisterTest.class);
    
    return suite;
  }
  
  /**
   * Sequential calls.
   */
  public void testSequential() {
    System.out.println("sequential read and write");
    instance.write(FIRST);
    int result = instance.read();
    assertEquals(result, FIRST);
  }
  /**
   * Parallel reads
   */
  public void testParallel() throws Exception {
    instance.write(FIRST);
    instance.write(SECOND);
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
      if (instance.read() != SECOND) {
        fail("register returns wrong value");
      }
    }
  }
  
}
