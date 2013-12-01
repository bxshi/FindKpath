package edu.nd.bshi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.kernel.Traversal;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KthShortestPath {

    static final Logger logger = LogManager.getLogger(KthShortestPath.class.getName());


    private final static int MAX_DEPTH = 15;
    private final static int MAX_HIT_COUNT = 15;
    static final ExecutionEngine engine = new ExecutionEngine(GraphDataBase.getInstance().getGraphDB());

    /**
     * Return all the shortest paths between startNode and stopNode
     *
     * @param startNode   Start point
     * @param stopNode    Stop point
     * @param maxDepth    Max depth, path which length exceeds this depth will be dropped
     * @param maxHitCount Max number of paths that will be returned
     * @return Iterable path list
     */
    public Iterable<Path> getAllKthShortestPath(Node startNode, Node stopNode, String linkType, int maxDepth, int maxHitCount) {
        PathFinder<Path> finder = GraphAlgoFactory.shortestPath(
                Traversal.expanderForTypes(
                        DynamicRelationshipType.withName(linkType), Direction.OUTGOING),
                maxDepth, maxHitCount);
        return finder.findAllPaths(startNode, stopNode);
    }

    /**
     * Return all shortest paths starts at startNode and ends at stopNode
     * The default k=15 and will return 15 paths at most.
     *
     * @param startNode Start node
     * @param stopNode  Stop node
     * @return Iterable path list
     */
    public Iterable<Path> getAllKthShortestPath(Node startNode, Node stopNode) {
        return this.getAllKthShortestPath(startNode, stopNode, "WIKILINK", MAX_DEPTH, MAX_HIT_COUNT);
    }


    /**
     * Gets all kth shortest path.
     *
     * @param startId  the start id
     * @param stopId   the stop id
     * @param maxDepth the max depth
     * @return LinkedList<LinkedList<Node>> the all kth shortest path
     */
    public LinkedList<LinkedList<Integer>> getAllKthShortestPath(int startId, int stopId, int maxDepth) {
        return this.getAllKthShortestPath(startId, stopId, maxDepth, "wikipage", "WIKILINK");
    }

    public LinkedList<LinkedList<Integer>> getAllKthShortestPath(int startId, int stopId, int maxDepth, String nodeIdx, String linkRel) {

        //TODO add timeout function to avoid long execution

        String query = "START src=node:" + nodeIdx + "(id=\"" + startId + "\"), " +
                "dest=node:" + nodeIdx + "(id=\"" + stopId + "\") " +
                "MATCH p=allShortestPaths(src-[r:" + linkRel + "*.." + maxDepth + "]->dest) " +
                "return extract(x in nodes(p) : x.id) as paths;";
        logger.debug("Cypher Query: " + query);
        ExecutionResult result = engine.execute(query);
        logger.debug("Cypher Query result: " + result.toString());
        LinkedList<LinkedList<Integer>> paths = new LinkedList<LinkedList<Integer>>();
        logger.info(paths);
        while (result.iterator().hasNext()) {

            LinkedList<Integer> path = new LinkedList<Integer>();
            String pathStr = result.iterator().next().get("paths").toString();
            logger.trace("path in kthshortestpath is " + pathStr);
            Matcher matcher = Pattern.compile("\\d+").matcher(pathStr);
            while (matcher.find()) {
                path.add(Integer.parseInt(pathStr.substring(matcher.start(), matcher.end())));
            }
            paths.add(path);
        }
        logger.debug("Path start:" + startId + " stop:" + stopId + " pathList: " + paths.toString());
        return paths;
    }

}
