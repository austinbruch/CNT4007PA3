/**
 * @author Austin Bruch
 * CNT4007C Spring 2015
 * Programming Assignment 3
 * Router Class
**/

import java.util.ArrayList;

public class Router {

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

   // Constructor that takes in a reference to the owning LinkState instance, in order to obtain the ArrayList of Nodes for this network
   public Router(LinkState linkState) {
      this.nodes = linkState.getNodes();           // Get the list of Nodes
      this.distances = new ArrayList<Integer>();   // New Distance ArrayList
      this.pValues = new ArrayList<Integer>();     // New P-Values ArrayList
      this.nSet = new ArrayList<Integer>();        // New N' set
      this.initialize();                           // Initialize this Router
   }

   // Accessor for the list of Nodes
   public ArrayList<Node> getNodes() {
      return this.nodes;
   }

   // Accessor for the Distance vector
   public ArrayList<Integer> getDistances() {
      return this.distances;
   }

   // Accessor for the P Vector
   public ArrayList<Integer> getPValues() {
      return this.pValues;
   }

   // Accessor for N'
   public ArrayList<Integer> getNSet() {
      return this.nSet;
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

   // Populate the Distance and P Vectors for initial starting values
   private void initialize() {
      for (Node n : this.nodes) {
         this.distances.add(-1);
         this.pValues.add(0);
      }
   }

   // Perform the Dijkstra's Algorithm routing, based on the specified source Node
   public void route(int sourceNodeIndex) {
      int step = 0;  // Current step of the algorithm

      StepPrinter printer = new StepPrinter(this, sourceNodeIndex);  // Utility object that prints out the progress of the algorithm

      printer.printDashedLine();             // Start by printing a dashed line
      printer.printHeader(sourceNodeIndex);  // Print the header for this algorithm instance
      printer.printDashedLine();             // Print another dashed line

      // Initialize N'
      this.getNSet().add(new Integer(sourceNodeIndex));           // N' starts off with the source Node

      Node sourceNode = this.getNodes().get(sourceNodeIndex-1);   // Get a reference to the source Node

      // For each node, get the initial cost and path values from the source node
      for (Node n : this.getNodes()) {
         if (!n.equals(sourceNode)) { // not dealing with the source node
            // if neighbor
            if (sourceNode.getDistanceToNode(n) != -1) {
               this.updateDistance(n, sourceNode.getDistanceToNode(n));
               this.updatePValue(n, sourceNode);
            } else { // Not a direct neighbor --> infinity
               this.updateDistance(n, -1);
            }
         } else { // Dealing with the source node here
            this.updateDistance(n, 0);    // These values aren't ever printed as algorithm progress, but it's nice to have for completeness
            this.updatePValue(n, n);
         }
      }

      printer.printStatusLine(step);   // Initialization is complete, print the status of the algorithm
      printer.printDashedLine();       // Print another dashed line

      // Looping stage

      while (this.getNSet().size() != this.getNodes().size()) {   // Loop until N' is full
         step++;                             // We are now on the next step of the algorithm
         int min = -1;                       // Minimum value for the next part of the algorith; starts at infinity (-1)
         int toAdd = sourceNodeIndex - 1;    // Reference to which Node will be added to N' this iteration
         
         // Iterate through the distance ArrayList, looking for a minimum Distance value for a node that is not in N'
         // Iterate backwards because in the case of a tie in the distance, we want to take the node with the smaller node number, as per assignment instructions
         for (int i = this.getDistances().size()-1; i >= 0; i--) {
            int oneBasedIndex = i + 1; 
            if (!this.getNSet().contains(oneBasedIndex)) {  // Only consider this Node if it's not already in N'
               if (min == -1) { // If min is infinity, add the next node because it's either smaller or the same
                  min = this.getDistances().get(i);   // Update the minimum
                  toAdd = i;                          // This Node is currently the Node we want to add to N'
               } else { // If min is not infinity
                  if (this.getDistances().get(i) != -1) { // Need to check for infinity here because of how infinity is being represented (-1)
                     if (min >= this.getDistances().get(i)) {  // If the current iterating distance is less than or equal to the current min, replace min with this distance value
                        min = this.getDistances().get(i);      // Update the minimum
                        toAdd = i;                             // This Node is currently the Node we want to add to N'
                     }
                  }
               }
            }
         }

         // At this point, we know the index (0 based) of the Node we want to add to N'
         this.getNSet().add(toAdd + 1);

         // Update the distances ArrayList (D()) for each node
         // The new distance value is either the old value, or the least cost value to the node that was just added to N', PLUS the cost from that new node to each node

         Node addedNode = this.getNodes().get(toAdd);    // Get a reference to the Node that was just added to N'
         for (Node n : this.getNodes()) {
            if (!this.getNSet().contains(n.getNodeIndex())) { // the iterating Node isn't in N'
               // if neighbor
               if (addedNode.getDistanceToNode(n) != -1) {
                  if (this.getDistanceFromNode(n) != -1) {  // If the old Distance Vector value for this Node wasn't Infinity
                     // If the new path is shorter than the old path, update it to show 
                     if (this.getDistanceFromNode(n) > this.getDistanceFromNode(addedNode) + addedNode.getDistanceToNode(n)) {
                        this.updateDistance(n, this.getDistanceFromNode(addedNode) + addedNode.getDistanceToNode(n));
                        this.updatePValue(n, addedNode);
                     }
                  } else {
                     // The old Distance vector value for this Node was Infinity, so we will always update it in this case
                     this.updateDistance(n, this.getDistanceFromNode(addedNode) + addedNode.getDistanceToNode(n));
                     this.updatePValue(n, addedNode);
                  }
                  
               } else { // Not a direct neighbor --> infinity
                  // Do nothing here, don't update the values in either D() or p()
               }
            }
         }

         printer.printStatusLine(step);   // Print the status of the algorithm
         printer.printDashedLine();       // Print another dashed line

      }
   }
   
}