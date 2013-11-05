package edu.nd.bshi;

import org.neo4j.graphdb.Path;


public class Main {
    public static void main(String[] args) {
        KthShortestPath kthShortestPath = new KthShortestPath();
        NodeFinder nodeFinder = new NodeFinder();
        Iterable<Path> paths = kthShortestPath.getAllKthShortestPath(
                nodeFinder.getSingleNodeByIndex("wikipage", "id", 260),
                nodeFinder.getSingleNodeByIndex("wikipage", "id", 621169));

        System.out.println(paths.toString());
    }
}
