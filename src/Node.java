/**
 * @author Austin Bruch
 * CNT4007C Spring 2015
 * Programming Assignment 3
 * Node Class
**/

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Node {

   // Instance variable representing the identifying number of this Node instance
   private int nodeIndex;

   // The cost from this Node to any other node in the network of Nodes
   private ArrayList<Integer> costs;

   // Constructor that takes the index of this Node instance
   public Node(int index) {
      this.nodeIndex = index;
      this.costs = new ArrayList<Integer>();
   }

   // Expects a comma-separated list of integers (and N to represent infinity)
   // Will populate the costs ArrayList with appropriate costs to each node in the containing network of Nodes
   public void parseCosts(String costs) {
      StringTokenizer costTokenizer = new StringTokenizer(costs, ",");     // Each cost is separated by a comma in the list of costs

      String token = null;
      int cost = -1;
      while(costTokenizer.hasMoreTokens()) {
         token = costTokenizer.nextToken();     // Get the next cost value
         token = token.replace(".","");         // If it's the last cost, which will have a period next to it, remove the period
         token = token.trim();                  // Trim any extra white space, just in case there is any
         
         if (token.equals("N")) {
            token = "-1";                       // use negative 1 to represent infinity in this instance
         }

         try {
            cost = Integer.parseInt(token);     // Attempt to parse the cost as an Integer
         } catch (NumberFormatException e) {
            System.out.println("Cost \"" + token + "\" associated with Node " + this.nodeIndex + " cannot be parsed to an Integer.");
            System.exit(0);
         }

         this.costs.add(new Integer(cost));     // If all went well with the parsing, add the new cost to the ArrayList of costs
      }

   }

   // Helper method that returns the cost from this Node to the specified Node
   // Note: This is for direct paths only (will return N representing Infinity if there is not a direct path from this Node to the specified Node)
   // Implemented to eliminate replication of potential off-by-one errors with indexing into an zero-based ArrayList
   public int getDistanceToNode(Node otherNode) {
      return this.costs.get(otherNode.getNodeIndex()-1);
   }

   // Returns the index of this Node instance
   public int getNodeIndex() {
      return this.nodeIndex;
   }
   
}