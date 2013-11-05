package edu.nd.bshi;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;

/**
 * Created with IntelliJ IDEA.
 * User: bshi
 * Date: 11/5/13
 * Time: 3:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class NodeFinder {
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
