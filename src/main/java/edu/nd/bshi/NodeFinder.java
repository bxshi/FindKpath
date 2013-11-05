package edu.nd.bshi;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;

class NodeFinder {
    private static GraphDatabaseService graphDb;

    /**
     *
     * @param dbInstance Singleton DatabaseInstance.
     *
     */
    public NodeFinder(GraphDataBase dbInstance) {
        graphDb = dbInstance.getGraphDB();
    }

    public NodeFinder() {
        this(GraphDataBase.getInstance());
    }

    public String[] getAllNodeIndex(){
        return graphDb.index().nodeIndexNames();
    }

    public Node getSingleNodeByIndex(String indexName, String propertyName, Object value){
        Index<Node> nodeIndex = graphDb.index().forNodes(indexName);
        return nodeIndex.get(propertyName, value).getSingle();

    }

}
