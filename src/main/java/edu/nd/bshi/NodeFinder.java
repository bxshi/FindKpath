package edu.nd.bshi;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;

public class NodeFinder {
    private static GraphDatabaseService graphDb;

    /**
     * @param dbInstance Singleton DatabaseInstance.
     */
    public NodeFinder(GraphDatabaseService dbInstance) {
        graphDb = dbInstance;
    }

    public NodeFinder() {
        this(GraphDataBase.getInstance().getGraphDB());
    }

    public String[] getAllNodeIndex() {
        return graphDb.index().nodeIndexNames();
    }

    public Node getSingleNodeByIndex(String indexName, String propertyName, Object value) {
        Index<Node> nodeIndex = graphDb.index().forNodes(indexName);
        return nodeIndex.get(propertyName, value).getSingle();
    }

}
