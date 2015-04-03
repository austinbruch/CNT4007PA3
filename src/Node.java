/**
 * @author = Austin Bruch
 * CNT4007C Spring 2015
 * Programming Assignment 3
 * Node Class
**/

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Node {

   private int nodeIndex;

   private ArrayList<Integer> costs;

   public Node(int index) {
      this.nodeIndex = index;
      this.costs = new ArrayList<Integer>();
   }

   // Expects a comma-separated list of integers (and N to represent infinity)
   // Will populate the costs ArrayList with appropriate costs to each node in the containing Network
   public void parseCosts(String costs) {
      StringTokenizer costTokenizer = new StringTokenizer(costs, ",");

      String token = null;
      int cost = -1;
      while(costTokenizer.hasMoreTokens()) {
         token = costTokenizer.nextToken();
         token = token.replace(".","");
         if (token.equals("N")) {
            token = "-1"; // use negative 1 to indicate infinity in this instance
         }

         try {
            cost = Integer.parseInt(token);
         } catch (NumberFormatException e) {
            System.out.println("Cost \"" + token + "\" associated with Node " + this.nodeIndex + " cannot be parsed to an Integer.");
            System.exit(0);
         }

         this.costs.add(new Integer(cost));
      }

      // System.out.println("Node: " + this.nodeIndex + " has links: " + this.costs);
   }

   public int getDistanceToNode(int destinationIndex) {
      return this.costs.get(destinationIndex-1);
   }

   public int getNodeIndex() {
      return this.nodeIndex;
   }
   


   
}