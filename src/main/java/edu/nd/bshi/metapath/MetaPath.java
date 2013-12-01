package edu.nd.bshi.metapath;

import edu.nd.bshi.category.Category;
import edu.nd.bshi.category.CategoryNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.helpers.collection.ReverseArrayIterator;

import java.util.*;


public class MetaPath {

    static final Logger logger = LogManager.getLogger(MetaPath.class.getName());
    boolean checked = false;
    int startNode, stopNode;

    //original paths, each tuple contains the nodes in that path
    // **DOES NOT CONTAIN THE START/DESTINATION POINT**
    /*
        [
            [4, 3, 6],  //path 0
            [2, 8, 7],  //path 1
            [2, 1, 12], //path 2
            ...
        ]
     */
    private LinkedList<LinkedList<Integer>> pathList = new LinkedList<LinkedList<Integer>>();

    //divided by position of nodes, each tuple contains the nodes in the specific position in the path
    /*
        [
            [4, 2, 2], // all the nodes at position 0
            [3, 8, 1], // all the nodes at position 1
            [6, 7, 12] // all the nodes at position 2
        ]
     */
    private LinkedList<LinkedList<Integer>> nodePathList = new LinkedList<LinkedList<Integer>>();

    //divided by position & height of nodes
    /*
       {
            1 : [all nodes at level 1],
            2 : [all nodes at level 2],
            ...
       }
     */
    private LinkedList<HashMap<Integer, Set<Integer>>> nodePathHeightList = new
            LinkedList<HashMap<Integer, Set<Integer>>>();

    //path that already clustered at each position
    /*
        [
            [1, 2],    // path at position 0 that merged(clustered) by their parent categories
            [],
            [0, 2]
        ]
     */
    private LinkedList<LinkedHashSet<Integer>> clusterPathList = new LinkedList<LinkedHashSet<Integer>>();

    //meta path results
    /*
        [
            [998, 221, 998] // meta-paths that we get from the original paths, nodes in this list are category ids
        ]
     */
    private LinkedHashSet<LinkedList<Integer>> metaPathList = new LinkedHashSet<LinkedList<Integer>>();

    private int mostFrequentLength = 0;

    public MetaPath(LinkedList<LinkedList<Integer>> paths) {
        HashMap<Integer, Integer> lengthCount = new HashMap<Integer, Integer>();

        startNode = paths.getFirst().getFirst();
        stopNode = paths.getFirst().getLast();
        logger.trace("Construct new meta category path, " + startNode + "-->" + stopNode);

        for (LinkedList<Integer> path : paths) {
            path.removeFirst();
            path.removeLast();
        }

        for (LinkedList<Integer> path : paths) {
            if (!lengthCount.containsKey(path.size())) {
                lengthCount.put(path.size(), 0);
            }
            lengthCount.put(path.size(), lengthCount.get(path.size()) + 1);
            if (mostFrequentLength < lengthCount.get(path.size())) {
                mostFrequentLength = path.size();
            }
        }

        for (int i = 0; i < mostFrequentLength; i++) {
            nodePathList.add(new LinkedList<Integer>());
        }

        for (LinkedList<Integer> path : paths) {
            if (path.size() == mostFrequentLength) {
                pathList.add(new LinkedList<Integer>());
                int nodePosIndex = 0;
                for (Integer node : path) {
                    //WARN get the lowest-level category index instead of the actual node index,
                    // otherwise this WON'T work
                    pathList.getLast().add(node);
                    nodePathList.get(nodePosIndex++).add(node);
                }
            }
        }

        //initialize cluster container
        for (int i = 0; i < mostFrequentLength; i++) {
            this.clusterPathList.add(new LinkedHashSet<Integer>());
        }

        logger.trace("Most frequent path length without start/stop is " + mostFrequentLength);
        logger.trace("Original paths are " + pathList.toString());
        logger.trace("Paths divided by position are " + nodePathList.toString());

    }

    /**
     * Instantiates a new Meta path.
     *
     * @param kthAllPaths the kth all paths
     */
    public MetaPath(Iterable<Path> kthAllPaths) {

        //TODO add check if the length is 0 (direct connected)

        HashMap<Integer, Integer> lengthCount = new HashMap<Integer, Integer>();
        //TODO extract start and destination nodes out of the list
        for (Path path : kthAllPaths) {
            //the length of path is the total nodes number in path minus 1
            if (!lengthCount.containsKey(path.length())) {
                lengthCount.put(path.length(), 0);
            }
            lengthCount.put(path.length(), lengthCount.get(path.length()) + 1);
            if (mostFrequentLength < lengthCount.get(path.length())) {
                mostFrequentLength = path.length();
            }
        }

        for (int i = 0; i <= mostFrequentLength; i++) {
            nodePathList.add(new LinkedList<Integer>());
        }

        for (Path path : kthAllPaths) {
            if (path.length() == mostFrequentLength) {
                pathList.add(new LinkedList<Integer>());
                int nodePosIndex = 0;
                for (Node node : path.nodes()) {
                    //TODO get the lowest-level category index instead of the actual node index, otherwise this WON'T work
                    pathList.getLast().add((Integer) node.getProperty("id"));
                    nodePathList.get(nodePosIndex++).add((Integer) node.getProperty("id"));
                }
            }

            //DirtyTrick: Remove start node and stop node
            pathList.getLast().remove(0);
            pathList.getLast().remove(pathList.getLast().size() - 1);
        }

        //DirtyTrick: Remove start node and stop node
        nodePathList.remove(0);
        nodePathList.remove(nodePathList.size() - 1);

        //Change mostFrequentLength to path length without start & stop nodes

        mostFrequentLength--;

        //initialize cluster container
        for (int i = 0; i < mostFrequentLength; i++) {
            this.clusterPathList.add(new LinkedHashSet<Integer>());
        }

        logger.trace("Most frequent path length without start/stop is " + mostFrequentLength);
        logger.trace("Original paths are " + pathList.toString());
        logger.trace("Path divided by position would be " + nodePathList.toString());

    }

    private static HashMap<Integer, Set<Integer>> sortByHeight(LinkedList<Integer> nodeList) {
        return Category.getNodesHeight(nodeList);
    }

    private static boolean mergeNodesBeforeHeightSort(LinkedList<Integer> nodeList,
                                                      LinkedHashSet<Integer> clusterSet) {
        HashMap<Integer, Integer> nodeCount = new HashMap<Integer, Integer>();
        boolean merged = false;
        for (int node : nodeList) {
            if (!nodeCount.containsKey(node)) {
                nodeCount.put(node, 0);
            }
            nodeCount.put(node, nodeCount.get(node) + 1);
        }

        for (int node : nodeCount.keySet()) {
            if (nodeCount.get(node) > 1) {
                putNodeIntoCluster(node, null, clusterSet, nodeList);
                merged = true;
            }
        }

        logger.trace("Merge duplicated nodes " + (merged ? "success" : "failed"));
        logger.trace("Merged cluster now is " + clusterSet.toString());

        return merged;

    }

    private boolean mergeNodes(HashMap<Integer, Set<Integer>> nodeHeightMap,
                               LinkedHashSet<Integer> clusterSet,
                               LinkedList<Integer> nodePath) {

        while (true) {
            ReverseArrayIterator<Integer> it = Category.getSortedHeightIterator(nodeHeightMap);
            boolean merged = false;
            while (it.hasNext()) {
                int index = it.next();
                logger.trace("height " + index);
                merged = false;
                if (index <= 1) {   // there is no chance to merge
                    return merged;
                }
                if (nodeHeightMap.get(index).size() != 0) {
                    for (int node : nodeHeightMap.get(index)) {
                        CategoryNode parent = Category.getNode(node).getParent();
                        logger.trace("Node information " + Category.getNode(node).toString());
                        if (nodeHeightMap.get(parent.getHeight()) == null) {
                            nodeHeightMap.put(parent.getHeight(), new HashSet<Integer>());
                        }
                        if (nodeHeightMap.get(parent.getHeight()).contains(parent.getIndex())) {
                            merged = true;
                            //add into cluster
                            putNodeIntoCluster(node, parent, clusterSet, nodePath);
                        } else {
                            nodeHeightMap.get(parent.getHeight()).add(parent.getIndex());
                        }
                    }
                    nodeHeightMap.remove(index);
                    if (merged) {
                        return merged;
                    }
                }
            }
        }
    }

    private static void putNodeIntoCluster(int child, CategoryNode parent, LinkedHashSet<Integer> clusterSet,
                                           LinkedList<Integer> nodePath) {
        //add into cluster
        if (parent == null) {
            for (int i = 0; i < nodePath.size(); i++) {
                if (nodePath.get(i) == child)
                    clusterSet.add(i);
            }
        } else {
            clusterSet.add(nodePath.indexOf(child));
        }
        if ((parent != null) && (nodePath.indexOf(parent.getIndex()) != -1) &&
                (!clusterSet.contains(nodePath.indexOf(parent.getIndex())))) {
            clusterSet.add(nodePath.indexOf(parent.getIndex()));
        }
        if (parent != null) {
            //update in nodPathList
            for (int i = 0; i < nodePath.size(); i++) {
                if (nodePath.get(i) == child) {
                    nodePath.remove(i);
                    nodePath.add(i, parent.getIndex());
                }
            }
        }

        logger.trace("put new node into cluster, updated cluster is " + clusterSet.toString());

    }

    private boolean findMetaPathByCluster() {
        HashMap<Integer, Integer> pathMatchCount = new HashMap<Integer, Integer>();
        boolean found = false;
        for (LinkedHashSet<Integer> cluster : this.clusterPathList) {
            for (Integer node : cluster) {
                if (!pathMatchCount.containsKey(node)) {
                    pathMatchCount.put(node, 0);
                }
                pathMatchCount.put(node, pathMatchCount.get(node) + 1);
            }
        }
        for (int key : pathMatchCount.keySet()) {
            if (pathMatchCount.get(key) == this.clusterPathList.size()) {
                LinkedList<Integer> metaPath = new LinkedList<Integer>();
                for (LinkedList<Integer> nodePath : this.nodePathList) {
                    metaPath.add(nodePath.get(key));
                }
                this.metaPathList.add(metaPath);
                found = true;
            }
        }
        return found;
    }

    private LinkedList<Integer> getLowestCategoryPath() {
        LinkedList<Integer> lowestCategoryPath = null;
        int totalDepth = 0;
        for (LinkedList<Integer> path : this.metaPathList) {
            int depth = 0;
            for (int node : path) {
                depth += Category.getNode(node).getHeight();
            }
            logger.trace("Path " + path.toString() + " total Depth=" + depth);
            lowestCategoryPath = totalDepth < depth ? path : lowestCategoryPath;
            totalDepth = totalDepth < depth ? depth : totalDepth;
        }
        return lowestCategoryPath;
    }

    private void calcMetaPath() {

        boolean duplicatedNodes = false;
        checked = true;

        //merge nodes at same position before doing a category based merge
        for (int i = 0; i < mostFrequentLength; i++) {
            if (mergeNodesBeforeHeightSort(this.nodePathList.get(i), this.clusterPathList.get(i))) {
                duplicatedNodes = true;
            }
        }

        if (duplicatedNodes) {
            findMetaPathByCluster();
            if (this.metaPathList.size() > 0) {
                logger.trace("meta paths are " + this.metaPathList);
                return;
            }
        }

        //sort all nodes by their height
        for (LinkedList<Integer> nodeList : nodePathList) {
            this.nodePathHeightList.add(sortByHeight(nodeList));
            if (this.nodePathHeightList.getLast().size() == 0)
                return;
        }


        logger.trace("Sorted nodes " + nodePathHeightList.toString());

        while (true) {
            int count = 0;
            for (int i = 0; i < this.nodePathHeightList.size(); i++) {
                if (mergeNodes(this.nodePathHeightList.get(i), this.clusterPathList.get(i), this.nodePathList.get(i))) {
                    //merged
                    if (this.allClusterIsNotEmpty()) {
                        //all position in the path have merged nodes
                        if (findMetaPathByCluster()) {
                            return;
                        }
                    }
                } else {
                    //not merged
                    if (checkClusterIsEmpty(i)) {
                        //do not have merged point, then there is no common-meta path
                        return;
                    }
                }
            }
        }


    }

    private boolean allClusterIsNotEmpty() {
        for (LinkedHashSet<Integer> linkedHashSet : this.clusterPathList) {
            if (linkedHashSet.size() == 0) {
                return false;
            }
        }
        return true;
    }

    private boolean checkClusterIsEmpty(int index) {
        return this.clusterPathList.get(index).size() == 0;
    }


    /**
     * Get meta path.
     *
     * @return the linked hash set
     */
    public LinkedHashSet<LinkedList<Integer>> getMetaPath() {

        //TODO change to generate multiple meta paths (the interface does not need to be changed)
        if (!this.checked) {
            calcMetaPath();
        }

        if (this.metaPathList.size() > 1) {
            LinkedHashSet<LinkedList<Integer>> singlePathList = new LinkedHashSet<LinkedList<Integer>>();
            singlePathList.add(this.getLowestCategoryPath());
            return singlePathList;
        }
        return this.metaPathList;
    }

}
