package edu.nd.bshi.category;

import org.junit.Test;
import org.junit.Assert;

public class CategoryReferenceTest {
    @Test
    public void testGetInstance() throws Exception {
        CategoryReference categoryReference = CategoryReference.getInstance();
        CategoryReference categoryReference1 = CategoryReference.getInstance();
        Assert.assertSame(categoryReference, categoryReference1);
    }

    @Test
    public void testPutAndGetNode() throws Exception {
        CategoryReference categoryReference = CategoryReference.getInstance();
        CategoryNode categoryNode = new CategoryNode(1,1);
        Assert.assertTrue(categoryReference.putNode(categoryNode));
        Assert.assertFalse(categoryReference.putNode(categoryNode));
        Assert.assertSame(categoryNode, categoryReference.getNode(1));
    }
}
