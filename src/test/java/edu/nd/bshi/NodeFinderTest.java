package edu.nd.bshi;

import org.junit.*;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

public class NodeFinderTest extends ParentTestClass{

    @Test
    public void testGetAllNodeIndex() throws Exception {
        Assert.assertEquals(nodeFinder.getAllNodeIndex().length, 0);

        Transaction tx = graphDb.beginTx();
        try{
            Node tmpNode = graphDb.createNode();
            tmpNode.setProperty("key","val");
            graphDb.index().forNodes("newIdx").add(tmpNode, "newKey", "1");
            tx.success();
        }finally {
            tx.finish();
        }

        Assert.assertEquals(nodeFinder.getAllNodeIndex().length, 1);
        Assert.assertEquals(nodeFinder.getAllNodeIndex()[0], "newIdx");
    }

    @Test
    public void testGetSingleNodeByIndex() throws Exception {
        Node tmpNode;
        Transaction tx = graphDb.beginTx();
        try{
            tmpNode = graphDb.createNode();
            tmpNode.setProperty("test","val");
            graphDb.index().forNodes("testGetSingleNodeByIndex").add(tmpNode,"test1", "val1");
            tx.success();
        }finally {
            tx.finish();
        }
        Assert.assertEquals(nodeFinder.getSingleNodeByIndex("testGetSingleNodeByIndex", "test1", "val1"), tmpNode);
    }
}
