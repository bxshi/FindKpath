package edu.nd.bshi;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.Path;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.kernel.Traversal;


public class FindKthShortestPath {

    private final static String DB_PATH = "./db/";
    private static GraphDatabaseService graphDb;

    private static enum RelTypes implements RelationshipType {
        WIKILINK
    }

    public static void main(String[] args) {
        // write your code here
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
        registerShutdownHook(graphDb);
        //Add shortest path finder
        String li[] = graphDb.index().nodeIndexNames();
        for(String s:li){
            System.out.println(s);
        }
        Index<Node> nodeIndex = graphDb.index().forNodes("wikipage");
        Node startNode = nodeIndex.get("id", 260).getSingle();
        Node stopNode = nodeIndex.get("id", 621169).getSingle();
        PathFinder<Path> finder = GraphAlgoFactory.shortestPath(
                Traversal.expanderForTypes(RelTypes.WIKILINK, Direction.OUTGOING), 5);
        Iterable<Path> paths = finder.findAllPaths(startNode, stopNode);
        System.out.println(paths.toString());
        graphDb.shutdown();
    }

    private static void registerShutdownHook(final GraphDatabaseService graphDb)
    {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running application).
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                graphDb.shutdown();
            }
        });
    }
}
