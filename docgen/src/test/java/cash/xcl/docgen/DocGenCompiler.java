package cash.xcl.docgen;

import static cash.xcl.docgen.FileUtils.deleteDirectory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.processing.Processor;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;


public final class DocGenCompiler {

    static void compile(Processor annotationProcessor, String... sourceFiles) throws Exception {
        compile(System.getProperty("java.class.path"), annotationProcessor, sourceFiles);
    }

    static void compile(String classpath, Processor annotationProcessor, String... sourceFiles) throws Exception {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        Path outPath = null;
        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)) {
            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromStrings(Arrays.asList(sourceFiles));
            outPath = Files.createTempDirectory("docgen");
            List<String> options = new ArrayList<>();
            options.add("-d");
            options.add(outPath.toAbsolutePath().toString());
            options.add("-parameters");
            options.add("-classpath");
            options.add(classpath);
            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, options, null, compilationUnits);
            task.setProcessors(Collections.singleton(annotationProcessor));
            if (!task.call()) {
                throw new IllegalArgumentException("Compilation failed");
            }
        } finally {
            if (outPath != null) {
                deleteDirectory(outPath);
            }
        }
    }

}
