/**
 * @author Austin Bruch
 * CNT4007C Spring 2015
 * Programming Assignment 3
 * LinkState Class
**/

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class LinkState {

   // Name of the specified Network File
   private String networkFile;
   private BufferedReader networkFileBufferedReader;

   // The number of nodes in the network that this LinkState instance is working with
   private int numNodes;

   // List of all Nodes in this Network, in order from 1 to N
   private ArrayList<Node> nodes;

   // Distances from the specified Source Node to each other Node in the Network
   private ArrayList<Integer> distances;

   // P Values for each Node 
   private ArrayList<String> pValues;

   private String header;

   public LinkState(String networkFile) {
      this.networkFile = networkFile;
      this.numNodes = 0;
      this.nodes = new ArrayList<Node>();
      this.header = null;
      this.distances = new ArrayList<Integer>();
      this.pValues = new ArrayList<String>();
   }

   private void initialize() {
      // Setup the Network File Reader
      try {
         this.networkFileBufferedReader = new BufferedReader(new FileReader(this.networkFile));
      } catch (FileNotFoundException e) {
         System.out.println("The specified Network File was not found.");
         System.exit(0);
      }
   }

   // Parses the Network file
   // Creates all Nodes and adds them to this LinkState's ArrayList of Nodes
   private void parseNetworkFile() {

      int currentNode = 0;
      String temp = null;
      try {
         while( (temp = this.networkFileBufferedReader.readLine()) != null ) {
            if (!temp.equals("EOF.")) {
               this.numNodes++;
               currentNode++;
               System.out.println(temp);

               Node node = new Node(currentNode);
               node.parseCosts(temp);
               this.nodes.add(node);
            }
         }
      } catch (IOException e) {
         System.out.println("An I/O Error occurred while reading the Network file.");
         System.exit(0);
      } finally {
         try {
            this.networkFileBufferedReader.close();
         } catch (IOException e) {
            System.out.println("An I/O Error occurred while attempting to close the BufferedReader for the Network file.");
            System.exit(0);
         }
      }

      System.out.println("Number of Nodes in this Network: " + this.numNodes);

   }

   // Perform the Dijkstra's Algorithm routing
   private void route(int sourceNodeIndex) {
      this.header = buildHeader(sourceNodeIndex);
      printDashedLine();
      System.out.println(header);
      printDashedLine();

   }

   // Print the header for the Dijkstra's Algorithm output
   // Specify which source node we are using to not print columns for that Node
   private String buildHeader(int sourceNodeIndex) {
      String header = "";

      header += "Step\t";

      int numTabsNeeded = this.numNodes / 6;
      if (this.numNodes % 6 != 0) {
         numTabsNeeded++;
      }

      header += "N\'";

      for (int i = 0; i < numTabsNeeded; i++) {
         header += "\t";
      }

      for (int i = 1; i <= this.numNodes; i++) {
         if (i != sourceNodeIndex) {
            header += "D(" + i + "),p(" + i + ")\t";
         }
      }

      return header;
   }

   // Prints a line of dashes of the appropriate length
   private void printDashedLine() {
      String dashes = "";
      int numTabs = this.header.length() - this.header.replace("\t","").length();
      for (int i = 0; i < this.header.length() + (numTabs * 6); i++) {
         dashes += "-";
      }

      System.out.println(dashes);
   }

   // Execute the LinkState routing process
   public void run() {
      // Initialize the Network File Reader
      this.initialize();

      // Parse the Network File to create the Network of Nodes
      this.parseNetworkFile();

      // Perform the Dijkstra's Shortest-Path Algorithm on the specified source node
      // Note: this is hard coded to Node 1 based on assignment specifications
      this.route(1);
   }

   // Used to drive the LinkState program
   public static void main(String... args) {
      if (args.length != 1) {
         System.out.println("Usage:\njava LinkState [networkFile]");
         System.exit(0);
      }

      // Create a LinkState instance with the specified network file
      LinkState linkState = new LinkState(args[0]);
      
      // Run the LinkState algorithm
      linkState.run();

   }
   
}
