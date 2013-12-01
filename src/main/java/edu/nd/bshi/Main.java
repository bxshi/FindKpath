package edu.nd.bshi;

import edu.nd.bshi.category.Category;
import edu.nd.bshi.metapath.MetaPath;
import edu.nd.bshi.util.DataSaver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Random;


public class Main {

    static Logger logger = LogManager.getLogger(Main.class.getName());
    static Runtime runtime = Runtime.getRuntime();
    static final Category category = Category.getInstance();
    static final int TIME_TO_SEC = 1000000;
    static final int MAX_ID = 39582844;
    static final DataSaver dataLogger = new DataSaver("result");


    public static void main(String[] args) {

        logger.info("-=-=-=-=Step One-=-=-=-=-=");
        logger.info("Constructing Category Tree");


        String url = "jdbc:mysql://dsg1.crc.nd.edu:3306/wikipedia";
        String user = "bshi";
        String passwd = "passwd";

        long startTime = System.nanoTime();

        if (!Category.loadCategoriesFromMySQL(url, user, passwd)) {
            logger.fatal("Category tree construct error, process halt.");
            System.exit(1);
        }

        long stopTime = System.nanoTime();

        logger.info("Category tree constructed, cost " + ((stopTime - startTime) / TIME_TO_SEC) + " seconds");
        logger.info("Memory Consumption are " + ((runtime.totalMemory() - runtime.freeMemory()) / 1024) + " KBytes");


        logger.info("-=-=-=-=Step Two-=-=-=-=-=");
        logger.info("Compute meta-path between random nodes");

        KthShortestPath kthShortestPath = new KthShortestPath();
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection(url, user, passwd);
            stat = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            stat.setFetchSize(Integer.MAX_VALUE);

            //Get two random nodes, try get the relations between them
            Random random = new Random();
            int startNodeId;
            int stopNodeId;

            boolean get = false;
            while (!get) {
                startNodeId = random.nextInt(MAX_ID);
                stopNodeId = random.nextInt(MAX_ID);

                rs = stat.executeQuery("SELECT page_id FROM page where page_id=" + startNodeId);
                if (!rs.first()) {
                    logger.warn("Node " + startNodeId + " does not exist, skip it");
                    continue;
                }

                rs = stat.executeQuery("SELECT page_id FROM page where page_id=" + stopNodeId);
                if (!rs.first()) {
                    logger.warn("Node " + stopNodeId + " does not exist, skip it");
                    continue;
                }

                logger.info("Try find path form " + startNodeId + " --> " + stopNodeId);

                //start combine two nodes
                LinkedList<LinkedList<Integer>> paths = kthShortestPath.getAllKthShortestPath(startNodeId, stopNodeId, 4, "wikipage", "WIKILINK");
                logger.info("Paths are\n" + paths.toString());
                if (paths.size() == 0) {
                    continue;
                }
                paths = Category.getPathsCategories(paths, stat);
                logger.info("converted paths are " + paths.toString());
                MetaPath metaPath = new MetaPath(paths);
                LinkedHashSet<LinkedList<Integer>> metapaths = metaPath.getMetaPath();
                logger.info(metaPath.getMetaPath().toString());
                dataLogger.write(metaPath.getMetaPath(), startNodeId, stopNodeId);
                if (metapaths.size() != 0) {
                    get = true;
                }
            }


        } catch (SQLException e) {
            logger.fatal("MySQL error! " + e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            logger.fatal("Construction error! " + e.toString());
            e.printStackTrace();
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
            }
        }
    }
}
