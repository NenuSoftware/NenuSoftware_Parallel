/*
 * ArraySumTest.java
 * JUnit based test
 *
 * Created on December 9, 2006, 5:48 PM
 */

package steal;

import junit.framework.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *
 * @author mph
 */
public class ArraySumTest extends TestCase {
  
  public ArraySumTest(String testName) {
    super(testName);
  }
  
  public void testRun() throws InterruptedException, ExecutionException {
    int[] a = {1, 2, 3};
    int[] b = {1, 2, 3, 4};
    int sum = ArraySum.sum(a);
    assertEquals(6, sum);
    sum = ArraySum.sum(b);
    assertEquals(10, sum);
    
  }
  
}
