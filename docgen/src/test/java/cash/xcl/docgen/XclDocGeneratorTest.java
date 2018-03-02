package cash.xcl.docgen;

import java.io.File;
import java.util.Arrays;

import org.junit.Test;

public class XclDocGeneratorTest {
    private static final char SEPARATOR = File.separatorChar;

    @Test
    public void xclCommandAnnotation() throws Exception {
        String[] sources = {GenerateDocumentationCommand.class.getSimpleName(), GenerateDocumentationEvent.class.getSimpleName(),
                GenerateDocumentationFailure.class.getSimpleName()};
        DocGenCompiler.compile(new XclDocGenerator(),
                Arrays.stream(sources).map(XclDocGeneratorTest::computePathForSourceFile).toArray(String[]::new));
    }


    static String computePathForSourceFile(String simpleClassName) {
        return "." + SEPARATOR + "src" + SEPARATOR + "test" + SEPARATOR + "java" + SEPARATOR + "cash" + SEPARATOR + "xcl" + SEPARATOR
                + "docgen" + SEPARATOR
                + simpleClassName + ".java";
    }
}
