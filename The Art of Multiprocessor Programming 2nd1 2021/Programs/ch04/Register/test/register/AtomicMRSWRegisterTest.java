/*
 * AtomicMRSWRegisterTest.java
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
public class AtomicMRSWRegisterTest extends TestCase {
  private final static int THREADS = 8;
  Thread[] thread = new Thread[THREADS];
  
  AtomicMRSWRegister<Integer> instance = new AtomicMRSWRegister<Integer>(0, THREADS);
  
  public AtomicMRSWRegisterTest(String testName) {
    super(testName);
  }
  
  public static Test suite() {
    TestSuite suite = new TestSuite(AtomicMRSWRegisterTest.class);
    
    return suite;
  }
  
  /**
   * Sequential calls.
   */
  public void testSequential() {
    ThreadID.reset();
    System.out.println("sequential read and write");
    instance.write(2006);
    int result = instance.read();
    assertEquals(result, 2006);
  }
  /**
   * Parallel reads
   */
  public void testParallel() throws Exception {
    ThreadID.reset();
    instance.write(99);
    instance.write(100);
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
    if (instance.read() != 100) {
        fail("register returns wrong value");
      }
    }
  }
  
}
