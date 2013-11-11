package edu.nd.bshi.category;

import java.util.HashMap;

public class CategoryNode {
    private int index;
    private int height;
    private CategoryNode parent;
    HashMap<Integer, CategoryNode> children = new HashMap<Integer, CategoryNode>();

    public CategoryNode(int index, int height) {
        this.index = index;
        this.height = height;
    }

    //TODO add another constructor that do not need to assign height

    private boolean setParent(CategoryNode father) {
        //Keep the hierarchy is in order
        //Do we need strictly make sure that this.height = father.getHeight()+1
        if (this.height > father.getHeight()) {
            this.parent = father;
            return true;
        } else {
            return false;
        }
    }

    public boolean putChild(CategoryNode node) {
        if (children.containsKey(node.index)) {
            return false;
        } else {
            if (node.setParent(this)) {
                children.put(node.index, node);
                return true;
            }
            return false;
        }
    }

    public CategoryNode getChild(int index) {
        return children.get(index);
    }

    public int getIndex() {
        return this.index;
    }

    public int getHeight() {
        return this.height;
    }

    public CategoryNode getParent() {
        return this.parent;
    }

    public HashMap<Integer, CategoryNode> getAllChildren() {
        return this.children;
    }

}
