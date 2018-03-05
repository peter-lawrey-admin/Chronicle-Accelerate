package cash.xcl.docgen;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;

public enum FileUtils {
    ;

    static Path findPathForClass(Path outPath, List<Path> found) {
        // a found is accepted only if it has the same hierarchy from top to
        // somewhere at the bottom, as long as the package name is
        int outPathLen = outPath.getNameCount();
        Path classFilePath = null;
        int matchDepth = 0;
        for (Path foundPath : found) {
            int foundPathLen = foundPath.getNameCount();
            int matches = 0;
            int i = 0;
            int j = 0;
            while (i < outPathLen) {
                boolean match = outPath.getName(i).equals(foundPath.getName(j));
                i++;
                if (match) {
                    matches++;
                    j++;
                    break;
                }
            }
            while ((i < outPathLen) && (j < foundPathLen)) {
                if (outPath.getName(i).equals(foundPath.getName(j))) {
                    matches++;
                    i++;
                    j++;
                } else {
                    break;
                }
            }
            if (j == (foundPathLen - 1)) {
                if (matches > matchDepth) {
                    matchDepth = matches;
                    classFilePath = foundPath;
                }
            }
        }
        return classFilePath;
    }

    static void deleteDirectory(Path dir) throws IOException {
        Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (exc != null) {
                    throw exc;
                }
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    static List<Path> findFile(Path rootDir, String fileName) throws IOException {
        FindFileByName finder = new FindFileByName(rootDir, fileName);
        Files.walkFileTree(rootDir, finder);
        return finder.found;
    }

    private static final class FindFileByName extends SimpleFileVisitor<Path> {
        private final Path rootDir;
        private final String fileName;
        List<Path> found = new LinkedList<>();

        FindFileByName(Path rootDir, String fileName) {
            this.rootDir = rootDir.toAbsolutePath();
            this.fileName = fileName;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if (file.getFileName().toString().equals(fileName)) {
                found.add(rootDir.relativize(file));
            }
            return FileVisitResult.CONTINUE;
        }
    }
}
