package edu.nd.bshi;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.Path;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.kernel.Traversal;

public class KthShortestPath {

    private final static int MAX_DEPTH = 15;
    private final static int MAX_HIT_COUNT = 15;
    private static GraphDatabaseService graphDb;

    /**
     *
     * @param dbInstance Singleton DatabaseInstance.
     *
     */
    public KthShortestPath(GraphDataBase dbInstance) {
        graphDb = dbInstance.getGraphDB();
    }

    public KthShortestPath() {
        this(GraphDataBase.getInstance());
    }

    /**
     * Return all the shortest paths between startNode and stopNode
     * @param startNode
     * @param stopNode
     * @param maxDepth  Max depth, path which length exceeds this depth will be dropped
     * @param maxHitCount Max number of paths that will be returned
     * @return And iterable path list
     */
    public Iterable<Path> getAllKthShortestPath(Node startNode, Node stopNode, int maxDepth, int maxHitCount) {
        PathFinder<Path> finder = GraphAlgoFactory.shortestPath(
                Traversal.pathExpanderForAllTypes(Direction.OUTGOING), maxDepth, maxHitCount);
        return finder.findAllPaths(startNode, stopNode);
    }

    public Iterable<Path> getAllKthShortestPath(Node startNode, Node stopNode) {
        return this.getAllKthShortestPath(startNode, stopNode, MAX_DEPTH, MAX_HIT_COUNT);
    }
}
