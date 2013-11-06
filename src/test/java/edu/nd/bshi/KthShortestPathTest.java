package edu.nd.bshi;

import org.junit.*;
import org.neo4j.graphdb.*;

import java.util.Iterator;

public class KthShortestPathTest extends ParentTestClass{

    static enum relType implements RelationshipType {TESTLINK}

    @Test
    public void testGetAllKthShortestPath() throws Exception {
        Transaction tx = graphDb.beginTx();
        try{
            Node startNode = graphDb.createNode();
            Node midNode = graphDb.createNode();
            Node stopNode = graphDb.createNode();
            startNode.createRelationshipTo(midNode, relType.TESTLINK);
            midNode.createRelationshipTo(stopNode, relType.TESTLINK);
            KthShortestPath kthShortestPath = new KthShortestPath();

            Iterator<Node> nodes = kthShortestPath.getAllKthShortestPath(startNode, stopNode, 2, 1)
                    .iterator().next().nodes().iterator();

            Assert.assertEquals(nodes.next(), startNode);
            Assert.assertEquals(nodes.next(), midNode);
            Assert.assertEquals(nodes.next(), stopNode);
            Assert.assertEquals(nodes.hasNext(), false);
            Iterator<Path> paths = kthShortestPath.getAllKthShortestPath(startNode, stopNode, 1, 1).iterator();
            Assert.assertEquals(paths.hasNext(), false);
            tx.success();
        }finally {
            tx.finish();
        }
    }
}
