package edu.nd.bshi;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

/**
 * GraphDataBase Singleton
 */
public class GraphDataBase {
    private static GraphDataBase instance = null;
    private static GraphDatabaseService graphDb = null;
    private static final String DB_PATH = "./db";
    protected GraphDataBase() {
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
    }

    public static GraphDataBase getInstance() {
        if(instance == null) {
            instance = new GraphDataBase();
        }
        return instance;
    }

    public GraphDatabaseService getGraphDB() {
        return graphDb;
    }
}
