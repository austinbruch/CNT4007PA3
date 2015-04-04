/**
 * @author Austin Bruch
 * CNT4007C Spring 2015
 * Programming Assignment 3
 * Router Class
**/

public class Router {

   // Reference to the LinkState object this Router is working with
   private LinkState linkState;

   public Router(LinkState linkState) {
      this.linkState = linkState;
   }

   // Perform the Dijkstra's Algorithm routing
   public void route(int sourceNodeIndex) {
      int step = 0;

      StepPrinter printer = new StepPrinter(this.linkState, sourceNodeIndex);

      printer.printDashedLine();
      printer.printHeader(sourceNodeIndex);
      printer.printDashedLine();

      // Initialize N'
      this.linkState.getNSet().add(new Integer(sourceNodeIndex));

      Node sourceNode = this.linkState.getNodes().get(sourceNodeIndex-1);

      for (Node n : this.linkState.getNodes()) {
         if (!n.equals(sourceNode)) { // not dealing with the source node
            // if neighbor
            if (sourceNode.getDistanceToNode(n) != -1) {
               this.linkState.updateDistance(n, sourceNode.getDistanceToNode(n));
               this.linkState.updatePValue(n, sourceNode);
            } else { // Not a direct neighbor --> infinity
               this.linkState.updateDistance(n, -1);
            }
         } else { // Dealing with the source node here
            this.linkState.updateDistance(n, 0); 
            this.linkState.updatePValue(n, n);
         }
      }

      printer.printStatusLine(step);
      printer.printDashedLine();

      // Looping stage

      while (this.linkState.getNSet().size() != this.linkState.getNodes().size()) {
         step++;
         int min = -1;
         int toAdd = sourceNodeIndex - 1;
         
         // Iterate through the distance ArrayList, looking for a minimum Distance value for a node that is not in N'
         // Iterate backwards because in the case of a tie in the distance, we want to take the node with the smaller node number, as per assignment instructions
         for (int i = this.linkState.getDistances().size()-1; i >= 0; i--) {
            int oneBasedIndex = i + 1; 
            if (!this.linkState.getNSet().contains(oneBasedIndex)) {
               if (min == -1) { // If min is infinity, add the next node because it's either smaller or the same
                  min = this.linkState.getDistances().get(i);
                  toAdd = i;
               } else { // If min is not infinity
                  if (this.linkState.getDistances().get(i) != -1) { // Need to check for infinity here because of how infinity is being represented (-1)
                     if (min >= this.linkState.getDistances().get(i)) { // If the current iterating distance is less than or equal to the current min, replace min with this distance value
                        min = this.linkState.getDistances().get(i);
                        toAdd = i;
                     }
                  }
               }
            }
         }

         // At this point, we know the index (0 based) of the Node to add to N'
         this.linkState.getNSet().add(toAdd + 1);

         // Update the distances ArrayList (D()) for each node
         // The new distance value is either the old value, or the least cost value to the node that was just added to N', PLUS the cost from that new node to each node

         Node addedNode = this.linkState.getNodes().get(toAdd);
         for (Node n : this.linkState.getNodes()) {
            if (!this.linkState.getNSet().contains(n.getNodeIndex())) { // the iterating node isn't in N'
               // if neighbor
               if (addedNode.getDistanceToNode(n) != -1) {

                  // If the new path is shorter than the old path, update it to show 
                  if (this.linkState.getDistanceFromNode(n) != -1) {
                     if (this.linkState.getDistanceFromNode(n) > this.linkState.getDistanceFromNode(addedNode) + addedNode.getDistanceToNode(n)) {
                        this.linkState.updateDistance(n, this.linkState.getDistanceFromNode(addedNode) + addedNode.getDistanceToNode(n));
                        this.linkState.updatePValue(n, addedNode);
                     }
                  } else {
                     this.linkState.updateDistance(n, this.linkState.getDistanceFromNode(addedNode) + addedNode.getDistanceToNode(n));
                     this.linkState.updatePValue(n, addedNode);
                  }
                  
               } else { // Not a direct neighbor --> infinity
                  // Do nothing here, don't update the values in either D() or p()
               }
            }
         }

         printer.printStatusLine(step);
         printer.printDashedLine();

      }
   }
   
}