package pl.jsolve.templ4docx.tests.meta;

import java.io.File;

import org.junit.Before;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public abstract class AbstractMetaTest {

    @Before
    public void createTmpDirectory() {
        String tmpPath = System.getProperty("java.io.tmpdir");
        String testPath = String.format("%s%s%s", tmpPath, File.separator, "meta");
        File testDir = new File(testPath);
        if (!testDir.exists())
            testDir.mkdirs();
    }

}
