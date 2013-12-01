package edu.nd.bshi.category;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.util.ArrayUtil;
import org.neo4j.helpers.collection.ReverseArrayIterator;

import java.sql.*;
import java.util.*;


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

    public static int getTopCategory(int node) {
        CategoryNode categoryNode = Category.getNode(node);
        if (categoryNode != null) {
            while (categoryNode.getHeight() != 1) {
                categoryNode = categoryNode.getParent();
            }
            return categoryNode.getIndex();
        } else {
            return 0;
        }
    }

    public static int getTopCategory(CategoryNode node) {
        return getTopCategory(node.getIndex());
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

    public static LinkedList<LinkedList<Integer>> getPathsCategories(LinkedList<LinkedList<Integer>> paths, Statement stat) {
        ResultSet res;
        LinkedList<LinkedList<Integer>> catePaths = new LinkedList<LinkedList<Integer>>();
        try {
            for (LinkedList<Integer> path : paths) {
                catePaths.add(new LinkedList<Integer>());
                for (int node : path) {
                    int cate = 0;
                    res = stat.executeQuery("SELECT " +
                            "parentID, childID, `left(A.path, locate(',', A.path)-1)` as mainCate " +
                            "FROM wikipedia.clel, wikipedia.articleToMainTopicCategories where childID=" + node + " and `stop`=" + node);
                    while (res.next()) {
                        int id = res.getInt("parentID");
                        int topCategory = res.getInt("mainCate");
                        logger.info("parentID " + id + " topCate " + topCategory + " actual topCate " + Category.getTopCategory(id));
                        if (topCategory == Category.getTopCategory(id)) {
                            cate = Category.getNode(id).getHeight() > Category.getNode(cate).getHeight() ?
                                    id : cate;
                        }
                    }
                    catePaths.getLast().add(cate);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return catePaths;
    }

    public static boolean loadCategoriesFromMySQL(String url, String user, String passwd) {
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection(url, user, passwd);
            stat = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            stat.setFetchSize(Integer.MIN_VALUE);
            rs = stat.executeQuery("SELECT path FROM wikipedia.pathsFromMainTopicCategories where namespace=14");
            int i = 0;
            while (rs.next()) {

                if (i++ % 50000 == 0 && logger.isInfoEnabled()) {
                    logger.info("Category tree constructing... " + i + " nodes processed");
                }

                String path = rs.getString(1);
                String[] pathList = path.split(",");
                int index = Integer.parseInt(pathList[pathList.length - 1]);
                int height = pathList.length;
                int parent = pathList.length > 1 ? Integer.parseInt(pathList[pathList.length - 2]) : 0;
                CategoryNode categoryNode = new CategoryNode(index, height);
                Category.putNode(categoryNode, parent);
            }

        } catch (SQLException e) {
            logger.fatal("MySQL error! " + e.toString());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            logger.fatal("Construction error! " + e.toString());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (stat != null) {
                    stat.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                logger.fatal("MySQL error! " + e.toString());
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

}
