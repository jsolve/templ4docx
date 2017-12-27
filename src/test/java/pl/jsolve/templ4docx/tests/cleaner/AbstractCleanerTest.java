package pl.jsolve.templ4docx.tests.cleaner;

import java.io.File;

import org.junit.Before;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public class AbstractCleanerTest {

    @Before
    public void createTmpDirectory() {
        String tmpPath = System.getProperty("java.io.tmpdir");
        String testPath = String.format("%s%s%s", tmpPath, File.separator, "cleaner");
        File testDir = new File(testPath);
        if (!testDir.exists())
            testDir.mkdirs();
    }

}
