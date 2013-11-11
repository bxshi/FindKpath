package edu.nd.bshi.category;

import java.util.HashMap;

public class CategoryReference {
    private static HashMap<Integer, CategoryNode> categoryRefMap = null;
    private static CategoryReference instance = null;

    protected CategoryReference() {
        categoryRefMap = new HashMap<Integer, CategoryNode>();
    }

    public static CategoryReference getInstance() {
        if (instance == null) {
            instance = new CategoryReference();
        }
        return instance;
    }

    public boolean putNode(CategoryNode node) {
        if (categoryRefMap.containsKey(node.getIndex())) {
            return false;
        } else {
            categoryRefMap.put(node.getIndex(), node);
            return true;
        }
    }

    public CategoryNode getNode(int index) {
        return categoryRefMap.get(index);
    }

}
