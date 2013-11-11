package edu.nd.bshi.category;

public class Category {
    private CategoryReference categoryReference = null;

    public Category() {
        categoryReference = CategoryReference.getInstance();
        CategoryNode categoryNodeRoot = new CategoryNode(0, 0);
        categoryReference.putNode(categoryNodeRoot);
    }

    public boolean putNode(CategoryNode node, int parentIndex) {
        return categoryReference.getNode(node.getIndex()) == null &&
                categoryReference.getNode(parentIndex).putChild(node) &&
                categoryReference.putNode(node);
    }

    public boolean putNode(int index, int parentIndex) {
        if ((categoryReference.getNode(index) != null) ||
                (categoryReference.getNode(parentIndex)==null)){
            return false;
        }
        CategoryNode node = new CategoryNode(index,
                categoryReference.getNode(parentIndex).getHeight()+1);
        return this.putNode(node, parentIndex);
    }

    public CategoryNode getNode(int index){
        return categoryReference.getNode(index);
    }

}
