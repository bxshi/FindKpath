package edu.nd.bshi.category;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.util.ArrayUtil;
import org.neo4j.helpers.collection.ReverseArrayIterator;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


public class Category {
    static final Logger logger = LogManager.getLogger(Category.class.getName());

    private static CategoryReference categoryReference = null;
    private static Category categoryInstance = null;

    protected Category() {
        categoryReference = CategoryReference.getInstance();
        CategoryNode categoryNodeRoot = new CategoryNode(0, 0);
        categoryReference.putNode(categoryNodeRoot);
    }

    public static Category getInstance() {
        if (categoryInstance == null) {
            categoryInstance = new Category();
        }
        return categoryInstance;
    }

    public static boolean putNode(CategoryNode node, int parentIndex) {
        return categoryReference.getNode(node.getIndex()) == null &&
                categoryReference.getNode(parentIndex).putChild(node) &&
                categoryReference.putNode(node);
    }

    public static boolean putNode(int index, int parentIndex) {
        if ((categoryReference.getNode(index) != null) ||
                (categoryReference.getNode(parentIndex) == null)) {
            return false;
        }
        CategoryNode node = new CategoryNode(index,
                categoryReference.getNode(parentIndex).getHeight() + 1);
        return putNode(node, parentIndex);
    }

    public static CategoryNode getNode(int index) {
        return categoryReference.getNode(index);
    }

    public static HashMap<Integer, Set<Integer>> getNodesHeight(List<Integer> IndexList) {
        HashMap<Integer, Set<Integer>> nodeHeightMap = new HashMap<Integer, Set<Integer>>();
        try {
            for (int index : IndexList) {
                int height = getNode(index).getHeight();
                if (nodeHeightMap.get(height) == null) {
                    nodeHeightMap.put(height, new LinkedHashSet<Integer>());
                }
                nodeHeightMap.get(height).add(index);
            }
        } catch (NullPointerException e) {
            logger.warn("Node does not exists");
        }
        return nodeHeightMap;
    }

    public static ReverseArrayIterator<Integer> getSortedHeightIterator(HashMap<Integer, Set<Integer>> nodeHeightMap) {
        Integer[] heights = nodeHeightMap.keySet().toArray(new Integer[nodeHeightMap.keySet().size()]);
        ArrayUtil.quickSort(heights);
        return new ReverseArrayIterator<Integer>(heights);
    }
}
