package resourceSystem;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class VFS {

    private static VFS instance;

    private VFS() {}

    public static VFS getInstance() {
        if (instance == null) {
            instance = new VFS();
        }
        return instance;
    }

    public Iterator<String> getIterator(String startDir) {
        return new FileIterator(startDir);
    }

    private class FileIterator implements Iterator<String> {

        private Queue<File> files = new LinkedList<>();

        public FileIterator(String path) {
            files.add(new File(path));
        }

        @Override
        public boolean hasNext() {
            return !files.isEmpty();
        }

        @Override
        public String next() {
            File file;
            do {
                file = files.poll();
                if (file.isDirectory()) {
                    for (File subFile : file.listFiles()) {
                        files.add(subFile);
                    }
                }
            } while (file.isDirectory());

            return file.getPath();
        }

        @Override
        public void remove() {}
    }

}
