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
import java.util.Collections;
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
   // Represents the D(node) values
   private ArrayList<Integer> distances;

   // Represents the set N'
   private ArrayList<Integer> nSet;

   // P Values for each Node 
   // Represents the p(node) values
   private ArrayList<Integer> pValues;

   private String header;

   // Current step of the routing algorithm
   private int step;

   public LinkState(String networkFile) {
      this.networkFile = networkFile;
      this.numNodes = 0;
      this.nodes = new ArrayList<Node>();
      this.header = null;
      this.distances = new ArrayList<Integer>();
      this.pValues = new ArrayList<Integer>();
      this.nSet = new ArrayList<Integer>();
      this.step = 0;
   }

   public ArrayList<Integer> getDistances() {
      return this.distances;
   }

   public ArrayList<Integer> getPValues() {
      return this.pValues;
   }

   public ArrayList<Integer> getNSet() {
      return this.nSet;
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
               // System.out.println(temp);

               Node node = new Node(currentNode);
               node.parseCosts(temp);
               this.nodes.add(node);
               this.distances.add(-1);
               this.pValues.add(0);
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

      // System.out.println("Number of Nodes in this Network: " + this.numNodes);

   }

   // Perform the Dijkstra's Algorithm routing
   private void route(int sourceNodeIndex) {
      this.header = buildHeader(sourceNodeIndex);
      this.printDashedLine();
      System.out.println(header);
      this.printDashedLine();

      // Initialize N'
      this.nSet.add(new Integer(sourceNodeIndex));

      Node sourceNode = this.nodes.get(sourceNodeIndex-1);

      for (Node n : this.nodes) {
         if (!n.equals(sourceNode)) { // not dealing with the source node
            // if neighbor
            if (sourceNode.getDistanceToNode(n) != -1){
               this.distances.set(n.getNodeIndex()-1, sourceNode.getDistanceToNode(n));
               this.pValues.set(n.getNodeIndex()-1, sourceNode.getNodeIndex());
            } else { // Not a direct neighbor --> infinity
               this.distances.set(n.getNodeIndex()-1, -1);
            }
         } else {
            this.distances.set(n.getNodeIndex()-1, 0);
            this.pValues.set(n.getNodeIndex()-1, n.getNodeIndex());
         }
      }

      this.printStatusLine(sourceNodeIndex);
      this.printDashedLine();

      // Looping stage

      while (this.nSet.size() != this.numNodes) {
         this.step++;
         int min = -1;
         int toAdd = sourceNodeIndex - 1;
         
         // Iterate through the distance ArrayList, looking for a minimum Distance value for a node that is not in N'
         // Iterate backwards because in the case of a tie in the distance, we want to take the node with the smaller node number, as per assignment instructions
         for (int i = this.distances.size()-1; i >= 0; i--) {
            int oneBasedIndex = i + 1; 
            if (!this.nSet.contains(oneBasedIndex)) {
               if (min == -1) { // If min is infinity, add the next node because it's either smaller or the same
                  min = this.distances.get(i);
                  toAdd = i;
               } else { // If min is not infinity
                  if (min >= this.distances.get(i)) { // If the current iterating distance is less than or equal to the current min, replace min with this distance value
                     min = this.distances.get(i);
                     toAdd = i;
                  }
               }
            }
         }

         // At this point, we know the index (0 based) of the Node to add to N'
         this.nSet.add(toAdd + 1);

         // Update the distances ArrayList (D()) for each node
         // The new distance value is either the old value, or the least cost value to the node that was just added to N', PLUS the cost from that new node to each node

         Node addedNode = this.nodes.get(toAdd);
         for (Node n : this.nodes) {
            if (!this.nSet.contains(n.getNodeIndex())) { // the iterating node isn't in N'
               // if neighbor
               if (addedNode.getDistanceToNode(n) != -1){

                  // If the new path is shorter than the old path, update it to show 
                  if (this.distances.get(n.getNodeIndex()-1) != -1) {
                     if (this.distances.get(n.getNodeIndex()-1) > this.distances.get(addedNode.getNodeIndex()-1) + addedNode.getDistanceToNode(n)) {
                        this.distances.set(n.getNodeIndex()-1, this.distances.get(addedNode.getNodeIndex()-1) + addedNode.getDistanceToNode(n));
                        this.pValues.set(n.getNodeIndex()-1, addedNode.getNodeIndex());
                     }
                  } else {
                     this.distances.set(n.getNodeIndex()-1, this.distances.get(addedNode.getNodeIndex()-1) + addedNode.getDistanceToNode(n));
                     this.pValues.set(n.getNodeIndex()-1, addedNode.getNodeIndex());
                  }
                  
               } else { // Not a direct neighbor --> infinity
                  // Do nothing here, don't update the values in either D() or p()
               }
            }
         }

         this.printStatusLine(sourceNodeIndex);
         this.printDashedLine();
      }
      

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

      for (int i = 0; i <= numTabsNeeded; i++) {
         header += "\t";
      }

      for (int i = 1; i <= this.numNodes; i++) {
         if (i != sourceNodeIndex) {
            header += "D(" + i + "),p(" + i + ")\t";
         }
      }

      return header;
   }

   private void printStatusLine(int sourceNodeIndex) {
      String statusLine = "";

      statusLine += this.step + "\t";

      int numTabsNeeded = this.numNodes  / 6;
      if (this.numNodes % 6 != 0) {
         numTabsNeeded++;
      }

      String nSetContents = this.getContentsOfNSet();
      int numTabsToSubtract = nSetContents.length() / 6;
      if (nSetContents.length() % 6 != 0 && nSetContents.length() / 6 != 0) {
         numTabsToSubtract++;
      }

      numTabsNeeded = numTabsNeeded - numTabsToSubtract;
      
      statusLine += nSetContents;

      for (int i = 0; i <= numTabsNeeded; i++) {
         statusLine += "\t";   
      }

      

      for (int i = 0; i < this.nodes.size(); i++) {
         int j = i + 1;
         if (j != sourceNodeIndex) {
            if (!this.nSet.contains(j)) {
            // if (true) { // Debug purposes only
               int distance = this.distances.get(i);
               int pValue = this.pValues.get(i);

               if (distance == -1) {
                  statusLine += "N\t\t";
               } else {
                  statusLine += distance + "," + pValue + "\t\t"; 
               }
            } else {
               statusLine += "\t\t";
              
         }
            }
      }
      
      System.out.println(statusLine);
   }

   private String getContentsOfNSet() {
      String toReturn = "";

      Collections.sort(this.nSet);

      for (Integer i : nSet) {
         toReturn += i.toString() + ",";
      }

      toReturn = toReturn.substring(0, toReturn.length()-1);

      return toReturn;
   }

   // Returns the maximum length of the N' set printed to the console
   private int maximumNSetStringLength() {
      int numCommas = this.numNodes - 1;

      int numbers = 0;
      for (int i = 1; i <= this.numNodes; i++) {
         numbers += String.valueOf(i).length();
      }

      return numCommas + numbers;
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
