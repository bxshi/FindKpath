package edu.nd.bshi;

import org.neo4j.kernel.impl.util.FileUtils;

import java.io.File;
import java.io.IOException;

public class Util {
    static void clearUpDB(String path) throws IOException {
        FileUtils.deleteRecursively(new File(path));
    }
}
