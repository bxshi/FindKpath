package edu.nd.bshi;

import org.junit.After;
import org.junit.Before;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class ParentTestClass {
    static GraphDatabaseService graphDb;
    static NodeFinder nodeFinder;
    static final String TEST_DB_PATH = "./test_db";

    static enum relType implements RelationshipType {TESTLINK}

    @Before
    public void setUp() throws Exception {
        Util.clearUpDB(TEST_DB_PATH);
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(TEST_DB_PATH);
        nodeFinder = new NodeFinder(graphDb);
    }

    @After
    public void tearDown() throws Exception {
        graphDb.shutdown();
        Util.clearUpDB(TEST_DB_PATH);
    }

}
