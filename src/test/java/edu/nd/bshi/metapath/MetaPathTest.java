package edu.nd.bshi.metapath;

import edu.nd.bshi.KthShortestPath;
import edu.nd.bshi.ParentTestClass;
import edu.nd.bshi.category.Category;
import edu.nd.bshi.category.CategoryNode;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Transaction;


public class MetaPathTest extends ParentTestClass {
    @Test
    public void testGetMetaPathWithNoHeightSort() throws Exception {
        MetaPath metaPath = null;

        Transaction tx = graphDb.beginTx();
        try {

            Node n1 = graphDb.createNode();
            Node n2 = graphDb.createNode();
            Node n3 = graphDb.createNode();
            Node n4 = graphDb.createNode();
            Node n5 = graphDb.createNode();
            Node n6 = graphDb.createNode();
            Node n7 = graphDb.createNode();

            n1.setProperty("id", 1);
            n2.setProperty("id", 2);
            n3.setProperty("id", 3);
            n4.setProperty("id", 4);
            n5.setProperty("id", 5);
            n6.setProperty("id", 6);
            n7.setProperty("id", 6);

            //paths
            n1.createRelationshipTo(n6, relType.TESTLINK);
            n6.createRelationshipTo(n5, relType.TESTLINK);

            n1.createRelationshipTo(n2, relType.TESTLINK);
            n2.createRelationshipTo(n4, relType.TESTLINK);
            n4.createRelationshipTo(n5, relType.TESTLINK);

            n1.createRelationshipTo(n2, relType.TESTLINK);
            n2.createRelationshipTo(n3, relType.TESTLINK);
            n3.createRelationshipTo(n4, relType.TESTLINK);
            n4.createRelationshipTo(n5, relType.TESTLINK);

            n1.createRelationshipTo(n3, relType.TESTLINK);
            n3.createRelationshipTo(n5, relType.TESTLINK);

            n1.createRelationshipTo(n7, relType.TESTLINK);
            n7.createRelationshipTo(n5, relType.TESTLINK);

            KthShortestPath kthShortestPath = new KthShortestPath();

            Iterable<Path> kthPaths = kthShortestPath.getAllKthShortestPath(n1, n5, 10, 10);

            metaPath = new MetaPath(kthPaths);

            Assert.assertEquals(1, metaPath.getMetaPath().size());

            tx.success();
        } finally {
            tx.finish();
        }

    }

    @Test
    public void testGetMetaPathWithHeightSort() throws Exception {
        CategoryNode cn0 = new CategoryNode(0, 0);
        CategoryNode cn1 = new CategoryNode(1, 1);
        CategoryNode cn2 = new CategoryNode(2, 1);
        CategoryNode cn3 = new CategoryNode(3, 2);
        CategoryNode cn4 = new CategoryNode(4, 2);
        CategoryNode cn5 = new CategoryNode(5, 3);
        CategoryNode cn6 = new CategoryNode(6, 3);
        CategoryNode cn7 = new CategoryNode(7, 3);
        CategoryNode cn8 = new CategoryNode(8, 3);
        CategoryNode cn9 = new CategoryNode(9, 3);
        CategoryNode cn11 = new CategoryNode(11, 1);

        Category.putNode(cn0, -1);
        Category.putNode(cn1, 0);
        Category.putNode(cn2, 0);
        Category.putNode(cn3, 1);
        Category.putNode(cn4, 2);
        Category.putNode(cn5, 3);
        Category.putNode(cn6, 3);
        Category.putNode(cn7, 3);
        Category.putNode(cn8, 4);
        Category.putNode(cn9, 4);
        Category.putNode(cn11, 0);

        Transaction tx = graphDb.beginTx();
        try {

            Node nstart = graphDb.createNode();
            Node nstop = graphDb.createNode();
            Node n5 = graphDb.createNode();
            Node n11 = graphDb.createNode();
            Node n9 = graphDb.createNode();
            Node n3 = graphDb.createNode();
            Node n4 = graphDb.createNode();
            Node n6 = graphDb.createNode();
            Node n1 = graphDb.createNode();
            Node n2 = graphDb.createNode();

            nstart.setProperty("id", 7);
            nstop.setProperty("id", 8);
            n5.setProperty("id", 5);
            n11.setProperty("id", 11);
            n9.setProperty("id", 9);
            n3.setProperty("id", 3);
            n4.setProperty("id", 4);
            n6.setProperty("id", 6);
            n1.setProperty("id", 1);
            n2.setProperty("id", 2);

            nstart.createRelationshipTo(n5, relType.TESTLINK);
            nstart.createRelationshipTo(n6, relType.TESTLINK);
            n5.createRelationshipTo(n11, relType.TESTLINK);
            n5.createRelationshipTo(n3, relType.TESTLINK);
            n6.createRelationshipTo(n1, relType.TESTLINK);
            n11.createRelationshipTo(n9, relType.TESTLINK);
            n3.createRelationshipTo(n4, relType.TESTLINK);
            n1.createRelationshipTo(n2, relType.TESTLINK);
            n9.createRelationshipTo(nstop, relType.TESTLINK);
            n4.createRelationshipTo(nstop, relType.TESTLINK);
            n2.createRelationshipTo(nstop, relType.TESTLINK);

            KthShortestPath kthShortestPath = new KthShortestPath();

            Iterable<Path> kthPaths = kthShortestPath.getAllKthShortestPath(nstart, nstop, 10, 10);

            MetaPath metaPath = new MetaPath(kthPaths);

            System.out.println(metaPath.getMetaPath().toString());

            tx.success();
        } finally {
            tx.finish();
        }

    }

}
