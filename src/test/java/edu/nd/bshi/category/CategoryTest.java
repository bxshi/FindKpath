package edu.nd.bshi.category;

import org.junit.Test;
import org.junit.Assert;

public class CategoryTest {

    static final int NODE1 = 8888;
    static final int NODE2 = 9888;

    @Test
    public void testPutAndGetNode() throws Exception {
        Category category = new Category();
        CategoryNode categoryNode = new CategoryNode(NODE1, 0);
        CategoryNode categoryNode1 = new CategoryNode(NODE1, 1);

        //Can not put another root into the CategoryTree
        Assert.assertFalse(category.putNode(categoryNode, 0));
        Assert.assertNull(category.getNode(NODE1));

        Assert.assertTrue(category.putNode(categoryNode1, 0));
        Assert.assertFalse(category.putNode(categoryNode1, 0));
        Assert.assertSame(categoryNode1, category.getNode(NODE1));

        Assert.assertFalse(category.putNode(NODE1, NODE2));
        Assert.assertFalse(category.putNode(NODE2, NODE2));
        Assert.assertTrue(category.putNode(NODE2, NODE1));

        Assert.assertEquals(2, category.getNode(NODE2).getHeight());
        Assert.assertSame(categoryNode1, category.getNode(NODE2).getParent());
    }
}
