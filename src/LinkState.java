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

   public ArrayList<Node> getNodes() {
      return this.nodes;
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

   // Initialize the Reader for the Network File
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
      String costs = null;
      try {
         while( (costs = this.networkFileBufferedReader.readLine()) != null ) {
            if (!costs.equals("EOF.")) {
               this.numNodes++;
               currentNode++;

               Node node = new Node(currentNode);
               node.parseCosts(costs);    // Figure out the costs to other Nodes from the new Node
               this.nodes.add(node);      // Add the new Node to the list of Nodes
               this.distances.add(-1);    // Initialize the distances ArrayList
               this.pValues.add(0);       // Initialize the p-values ArrayList
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

   }

   // Perform the Dijkstra's Algorithm routing
   private void route(int sourceNodeIndex) {
      StepPrinter printer = new StepPrinter(this, sourceNodeIndex);

      printer.printDashedLine();
      printer.printHeader(sourceNodeIndex);
      printer.printDashedLine();

      // Initialize N'
      this.nSet.add(new Integer(sourceNodeIndex));

      Node sourceNode = this.nodes.get(sourceNodeIndex-1);

      for (Node n : this.nodes) {
         if (!n.equals(sourceNode)) { // not dealing with the source node
            // if neighbor
            if (sourceNode.getDistanceToNode(n) != -1) {
               this.updateDistance(n, sourceNode.getDistanceToNode(n));
               this.updatePValue(n, sourceNode);
            } else { // Not a direct neighbor --> infinity
               this.updateDistance(n, -1);
            }
         } else { // Dealing with the source node here
            this.updateDistance(n, 0); 
            this.updatePValue(n, n);
         }
      }

      printer.printStatusLine(this.step);
      printer.printDashedLine();

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
                  if (this.distances.get(i) != -1) { // Need to check for infinity here because of how infinity is being represented (-1)
                     if (min >= this.distances.get(i)) { // If the current iterating distance is less than or equal to the current min, replace min with this distance value
                        min = this.distances.get(i);
                        toAdd = i;
                     }
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
               if (addedNode.getDistanceToNode(n) != -1) {

                  // If the new path is shorter than the old path, update it to show 
                  if (this.getDistanceFromNode(n) != -1) {
                     if (this.getDistanceFromNode(n) > this.getDistanceFromNode(addedNode) + addedNode.getDistanceToNode(n)) {
                        this.updateDistance(n, this.getDistanceFromNode(addedNode) + addedNode.getDistanceToNode(n));
                        this.updatePValue(n, addedNode);
                     }
                  } else {
                     this.updateDistance(n, this.getDistanceFromNode(addedNode) + addedNode.getDistanceToNode(n));
                     this.updatePValue(n, addedNode);
                  }
                  
               } else { // Not a direct neighbor --> infinity
                  // Do nothing here, don't update the values in either D() or p()
               }
            }
         }

         printer.printStatusLine(this.step);
         printer.printDashedLine();

      }
      
   }

   // Wrapping function to get the distance for a certain Node
   private int getDistanceFromNode(Node node) {
      return this.distances.get(node.getNodeIndex()-1);
   }

   // Wrapping function to avoid confusion with the indexing differences between ArrayList containers and Node Indexing
   private void updateDistance(Node nodeToUpdate, int distance) {
      this.distances.set(nodeToUpdate.getNodeIndex()-1, distance);
   }

   // Wrapping function to avoid confusion with the indexing differences between ArrayList containers and Node Indexing
   private void updatePValue(Node nodeToUpdate, Node newPValue) {
      this.pValues.set(nodeToUpdate.getNodeIndex()-1, newPValue.getNodeIndex());
   }

   // Execute the LinkState routing process
   public void run(int sourceNodeIndex) {
      // Initialize the Network File Reader
      this.initialize();

      // Parse the Network File to create the Network of Nodes
      this.parseNetworkFile();

      // Find all routes from the specified source Node
      this.route(sourceNodeIndex);
   }

   // Used to drive the LinkState program
   public static void main(String... args) {
      if (args.length != 1) {
         System.out.println("Usage:\njava LinkState [networkFile]");
         System.exit(0);
      }

      // Create a LinkState instance with the specified network file
      LinkState linkState = new LinkState(args[0]);

      // Always route from Node 1, based on project specifications
      int sourceNodeIndex = 1;
      
      // Run the LinkState algorithm
      linkState.run(sourceNodeIndex);

   }
   
}
