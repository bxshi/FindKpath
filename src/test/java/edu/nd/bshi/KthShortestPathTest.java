package edu.nd.bshi;

import org.junit.Assert;
import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Transaction;

import java.util.Iterator;

public class KthShortestPathTest extends ParentTestClass {

    @Test
    public void testGetAllKthShortestPath() throws Exception {
        Transaction tx = graphDb.beginTx();
        try {
            Node startNode = graphDb.createNode();
            Node midNode = graphDb.createNode();
            Node stopNode = graphDb.createNode();
            startNode.createRelationshipTo(midNode, relType.TESTLINK);
            midNode.createRelationshipTo(stopNode, relType.TESTLINK);
            KthShortestPath kthShortestPath = new KthShortestPath();

            Iterator<Node> nodes = kthShortestPath.getAllKthShortestPath(startNode, stopNode, "TESTLINK", 2, 1)
                    .iterator().next().nodes().iterator();

            Assert.assertEquals(startNode, nodes.next());
            Assert.assertEquals(midNode, nodes.next());
            Assert.assertEquals(stopNode, nodes.next());
            Assert.assertFalse(nodes.hasNext());
            Iterator<Path> paths = kthShortestPath.getAllKthShortestPath(startNode, stopNode).iterator();
            Assert.assertFalse(paths.hasNext());
            tx.success();
        } finally {
            tx.finish();
        }
    }
}
