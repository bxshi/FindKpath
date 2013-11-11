package edu.nd.bshi.category;

import org.junit.Assert;
import org.junit.Test;


public class CategoryNodeTest {

    static final int NODE1 = 8888;
    static final int NODE2 = 1889;
    static final int NODE3 = 189;

    @Test
    public void testPutAndGetChild() throws Exception {

        CategoryNode categoryNode1 = new CategoryNode(NODE1, 0);
        CategoryNode categoryNode2 = new CategoryNode(NODE2, 1);

        //Can not put a high height node into a low height node
        Assert.assertFalse(categoryNode2.putChild(categoryNode1));
        Assert.assertNull(categoryNode2.getChild(NODE1));

        //Put low height node into high height node
        Assert.assertTrue(categoryNode1.putChild(categoryNode2));
        Assert.assertSame(categoryNode2, categoryNode1.getChild(NODE2));

        //Can not put the same node twice
        Assert.assertFalse(categoryNode1.putChild(categoryNode2));
        Assert.assertNull(categoryNode1.getChild(NODE1));

    }

    @Test
    public void testGetMethods() throws Exception {

        CategoryNode categoryNode1 = new CategoryNode(NODE1, 0);
        CategoryNode categoryNode2 = new CategoryNode(NODE2, 1);

        categoryNode1.putChild(categoryNode2);

        Assert.assertEquals(NODE1, categoryNode1.getIndex());
        Assert.assertEquals(0, categoryNode1.getHeight());
        Assert.assertSame(categoryNode1, categoryNode2.getParent());
        Assert.assertEquals(1, categoryNode1.getAllChildren().size());
        Assert.assertSame(categoryNode2, categoryNode1.getAllChildren().get(NODE2));

    }

    @Test
    public void testObjectReference() throws Exception {

        CategoryNode categoryNode1 = new CategoryNode(NODE1, 0);
        CategoryNode categoryNode2 = new CategoryNode(NODE2, 1);
        CategoryNode categoryNode3 = new CategoryNode(NODE3, 2);

        Assert.assertTrue(categoryNode1.putChild(categoryNode2));
        Assert.assertTrue(categoryNode2.putChild(categoryNode3));
        Assert.assertSame(categoryNode3, categoryNode1.getChild(NODE2).getChild(NODE3));

    }
}
