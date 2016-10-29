package eu.codetopic.anty.ev3projectsandroid;

import org.junit.Test;

import eu.codetopic.anty.ev3projectsbase.slam.base.scan.ScanResults;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void testScanResultsTest() throws Exception {
        ScanResults results = new ScanResults();
        results.add(5f, 10f, 0f);
        System.out.println(results);
        results.offset(0f, 0f, 10f);
        System.out.println(results);
    }
}