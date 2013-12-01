package edu.nd.bshi;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * GraphDataBase Singleton
 */
public class GraphDataBase {
    private static GraphDataBase instance = null;
    private static GraphDatabaseService graphDb = null;
    private static final String DB_PATH = "./db";
    private Map<String, String> config;

    protected GraphDataBase() {

        config = new HashMap<String, String>();
        config.put("read_only", "true");
        config.put("neostore.nodestore.db.mapped_memory", "120M");
        config.put("neostore.relationsipstore.db.mapped_memory", "5000M");
        config.put("neostore.propertystore.db.mapped_memory", "800M");
        config.put("neostore.propertystore.db.strings.mapped_memory", "800M");
        config.put("dump_configuration", "true");

        graphDb = new GraphDatabaseFactory()
                .newEmbeddedDatabaseBuilder(DB_PATH)
                .setConfig(config)
                .newGraphDatabase();
        registerShutdownHook(graphDb);
    }

    public static GraphDataBase getInstance() {
        if (instance == null) {
            instance = new GraphDataBase();
        }
        return instance;
    }

    public GraphDatabaseService getGraphDB() {
        return graphDb;
    }

    private static void registerShutdownHook(final GraphDatabaseService graphDb) {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running application).
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                graphDb.shutdown();
            }
        });
    }

}
