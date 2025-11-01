/*
 * RegMRSWRegisterTest.java
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
public class RegMRSWRegisterTest extends TestCase {
  private final static int THREADS = 8;
  private final static byte FIRST = 11;
  private final static byte SECOND = 22;
  byte result = 0;
  
  Thread[] thread = new Thread[THREADS];
  
  RegMRSWRegister instance = new RegMRSWRegister(THREADS);
  
  public RegMRSWRegisterTest(String testName) {
    super(testName);
  }
  
  public static Test suite() {
    TestSuite suite = new TestSuite(RegMRSWRegisterTest.class);    
    return suite;
  }
  
  /**
   * Sequential calls.
   */
  public void testSequential() {
    System.out.println("sequential read and write");
    instance.write(FIRST);
    result = instance.read();
    assertEquals(result, FIRST);
  }
  /**
   * Parallel reads
   */
  public void testParallel() throws Exception {
    ThreadID.reset();
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
