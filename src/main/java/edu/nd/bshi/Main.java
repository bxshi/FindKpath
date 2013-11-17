package edu.nd.bshi;

import edu.nd.bshi.category.Category;
import edu.nd.bshi.category.CategoryNode;
import org.apache.logging.log4j.Logger;
import org.neo4j.graphdb.Path;

import org.apache.logging.log4j.LogManager;

import java.sql.*;


public class Main {

    static Logger logger = LogManager.getLogger(Main.class.getName());
    static Category category = Category.getInstance();
    static Runtime runtime = Runtime.getRuntime();


    public static void main(String[] args) {

        logger.info("-=-=-=-=Step One-=-=-=-=-=");
        logger.info("Constructing Category Tree");

        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;

        String url = "jdbc:mysql://dsg1.crc.nd.edu:3306/wikipedia";
        String user = "bshi";
        String passwd = "passwd";

        int[] x = new int[39582845];

        long startTime = System.nanoTime();

        try{
            conn = DriverManager.getConnection(url, user, passwd);
            stat = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            stat.setFetchSize(Integer.MIN_VALUE);
            rs = stat.executeQuery("SELECT path FROM wikipedia.pathsFromMainTopicCategories where namespace=14");
            int i = 0;
            while(rs.next()) {
                if(i++ % 50000 == 0) {
                    logger.info("Processed "+i);
                }
                String path = rs.getString(1);
                String[] pathList = path.split(",");
                int index = Integer.parseInt(pathList[pathList.length - 1]);
                int height = pathList.length;
                int parent = pathList.length>1 ? Integer.parseInt(pathList[pathList.length-2]) : 0;
                CategoryNode categoryNode = new CategoryNode(index, height);
                logger.trace(index+" "+parent);
                category.putNode(categoryNode, parent);
            }
        } catch(SQLException e){
            logger.fatal("MySQL error! "+e.toString());
            e.printStackTrace();
        } catch(Exception e){
            logger.fatal("Construction error! "+e.toString());
            e.printStackTrace();
        } finally {
            try{
                if(rs!=null)
                    rs.close();
                if(stat != null){
                    stat.close();
                }
                if(conn != null){
                    conn.close();
                }
            }catch (SQLException e){
                logger.fatal("MySQL error! "+e.toString());
                e.printStackTrace();
            }
        }

        long stopTime = System.nanoTime();

        logger.info("Category tree constructed, cost "+((stopTime-startTime)/1000000)+" seconds");
        logger.info("Memory Consumption are "+((runtime.totalMemory () - runtime.freeMemory ())/1024) +" KBytes");

//
//        KthShortestPath kthShortestPath = new KthShortestPath();
//        NodeFinder nodeFinder = new NodeFinder();
//        Iterable<Path> paths = kthShortestPath.getAllKthShortestPath(
//                nodeFinder.getSingleNodeByIndex("wikipage", "id", 260),
//                nodeFinder.getSingleNodeByIndex("wikipage", "id", 621169));
//
//        System.out.println(paths.toString());
    }
}
