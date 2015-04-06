/**
 * @author Austin Bruch
 * CNT4007C Spring 2015
 * Programming Assignment 3
 * StepPrinter Class
**/

import java.util.Collections;

public class StepPrinter {

   // Constant that represents the length of a tab character in spaces for a monospaced font
   public static int TAB_LENGTH = 8;

   // Reference to the related Router instance
   private Router router;

   // The header for the Algorithm printout
   private String header;

   // The index of the Source node for the given Dijkstra's Algorithm execution
   private int sourceNodeIndex;

   // Integer array used to maintain the index into the step status line each column needs to begin at, for columns N', D(2),p(2), ..., D(N),p(N)
   private int[] columnIndices;

   // Constructor that takes in a reference to the owning Router and the index of the source node for the routing algorithm
   public StepPrinter(Router router, int sourceNodeIndex) {
      this.router = router;
      this.header = null;
      this.sourceNodeIndex = sourceNodeIndex;
      this.columnIndices = new int[this.router.getNodes().size()];   // A new array that requires exactly as many entries as there are Nodes in the network
      this.initialize();   // Initialize this StepPrinter
   }

   // Initialize this StepPrinter
   private void initialize() {
      this.buildHeader();  // Build the header for this StepPrinter
   }

   // Static helper method that builds a String based on repeating one String a certain amount of times
   private static String buildRepeatingString(String toRepeat, int howManyTimes) {
      String toReturn = "";
      
      for (int i = 0; i < howManyTimes; i++) {
         toReturn += toRepeat;
      }
      
      return toReturn;
   }

   // Print the header for the Dijkstra's Algorithm output
   private void buildHeader() {
      String header = "";

      // This block is the initial work for handling cases where the maximum step number, when represented as a String, would be longer than 7 characters
      // At this point, it is assumed that this scenario will not happen as that would require in the millions of Nodes for the given network

      // if (String.valueOf(this.numNodes).length() >= 8) { // The Step field will be longer than 8 spaces
      //    int quotient = String.valueOf(this.numNodes).length() / 8);
      //    int remainder = String.valueOf(this.numNodes).length() % 8;

      // } else { // The Step field will be less than 8 spaces
      //    header += "Step    ";
      // }

      // Start the header off with the word Step, followed by however many spaces are left until a full tab's length is met
      header += "Step" + StepPrinter.buildRepeatingString(" ", TAB_LENGTH - "Step".length());

      // Set the column index that specifies in which index the N' column will begin
      this.columnIndices[0] = header.length();

      // Determine how long the N' field needs to be at its most
      int nSetLength = this.maximumNSetStringLength();

      // Determine how many tabs the maximum N' length will be
      int quotient = nSetLength / TAB_LENGTH;
      int remainder = nSetLength % TAB_LENGTH;

      // If there would only be 1 space between the end of the N' column and the next column, go ahead and add another tab's length of spaces in there
      if (remainder != TAB_LENGTH - 1) {
         this.columnIndices[1] = TAB_LENGTH * (quotient + 1) + this.columnIndices[0]; // This is where D(2),p(2) starts   
      } else {
         this.columnIndices[1] = TAB_LENGTH * (quotient + 2) + this.columnIndices[0]; // This is where D(2),p(2) starts   
      }
      
      // Set the indices within the header string for where each successive column will start
      // This is assuming that the entries in each column will not exceed 16 characters in length
      for (int i = 2; i < this.columnIndices.length; i++) {
         this.columnIndices[i] = this.columnIndices[i-1] + 2 * TAB_LENGTH;
      }

      // Append the N' column, along with the required amount of space padding afterwards
      header += "N\'";
      header += StepPrinter.buildRepeatingString(" ",this.columnIndices[1] - header.length());

      // Append each successive column header, along with the necessary spaces
      // Notice that we are starting with 1 instead of 0 because Nodes used 1-based indexing
      for (int i = 1; i <= this.router.getNodes().size(); i++) {
         if (i != this.sourceNodeIndex) { // Don't need to print out anything for the source node
            if (i < this.router.getNodes().size()) {        // Handles every column but the final column 
               String temp = "D(" + i + "),p(" + i + ")";   // Generate the next column header
               String spaces = StepPrinter.buildRepeatingString(" ", this.columnIndices[i] - this.columnIndices[i-1] - temp.length()); // Generate the necessary amount of spaces
               header += temp + spaces;                     // Append the header and corresponding spaces
            } else { // Handles the final column
               String temp = "D(" + i + "),p(" + i + ")";   // Generate the final column header
               String spaces = StepPrinter.buildRepeatingString(" ", TAB_LENGTH * 2 - temp.length()); // Generate the necessary spaces
               header += temp + spaces;                     // Append the header and corresponding spaces
            }
         }
      }

      // Update the header instance variable
      this.header = header;
   }

   // Prints the Header for the Algorithm's output
   public void printHeader(int sourceNodeIndex) {
      System.out.println(this.header);
   } 

   // Prints the status line of the algorithm, indicating the current status of the Distance Vector and P vector
   public void printStatusLine(int step) {
      String statusLine = "";

      // Append the step number and the corresponding amount of spaces
      statusLine += Integer.valueOf(step);
      statusLine += StepPrinter.buildRepeatingString(" ", this.columnIndices[0] - statusLine.length());

      // Append N' and the corresponding amount of spaces
      statusLine += this.getContentsOfNSet();
      statusLine += StepPrinter.buildRepeatingString(" ", this.columnIndices[1] - statusLine.length());

      // For every Node, append it's status at the current state of the algorithm
      for (int i = 0; i < this.router.getNodes().size(); i++) {
         int oneBasedIndex = i + 1;
         if (oneBasedIndex != this.sourceNodeIndex) {                // Don't append the status of the source Node
            if (!this.router.getNSet().contains(oneBasedIndex)) {    // Don't append the status of any node that is in N'
               int distance = this.router.getDistances().get(i);     // Get the current distance value to this Node
               int pValue = this.router.getPValues().get(i);         // Get the current p value to this node

               if (distance == -1) {   // If the distance is infinity (must have a special case for this as infinity is represented as -1 which isn't really infinity)
                  statusLine += "N";   // Append N as N represents infinity
                  if (oneBasedIndex != this.router.getNodes().size()) { // If this isn't the final Node
                     statusLine += StepPrinter.buildRepeatingString(" ", this.columnIndices[i+1] - statusLine.length());  // Append spaces
                  } else { // This is the final Node
                     statusLine += StepPrinter.buildRepeatingString(" ", TAB_LENGTH * 2 - "N".length()); // Append spaces
                  }
               } else {
                  statusLine += Integer.toString(distance) + "," + Integer.toString(pValue);    // Append the distance and p values
                  if (oneBasedIndex != this.router.getNodes().size()) { // If this isn't the final Node
                     statusLine += StepPrinter.buildRepeatingString(" ", this.columnIndices[i+1] - statusLine.length());   // Append spaces
                  } else { // This is the final Node
                     statusLine += StepPrinter.buildRepeatingString(" ", TAB_LENGTH * 2 - (Integer.toString(distance) + "," + Integer.toString(pValue)).length());   // Append spaces
                  }
               }
            } else { // This node is in N', print only spaces
               if (oneBasedIndex != this.router.getNodes().size()) { // If this isn't the final Node
                  statusLine += StepPrinter.buildRepeatingString(" ", this.columnIndices[i+1] - statusLine.length());   // Append spaces
               } else { // This is the final Node
                  statusLine += StepPrinter.buildRepeatingString(" ", TAB_LENGTH * 2); // Append spaces
               }
            }
         }
      }

      // Print out the status line
      System.out.println(statusLine);
   }

   // Returns a String representation of the sorted version of the N' set of Nodes
   private String getContentsOfNSet() {
      String toReturn = "";

      Collections.sort(this.router.getNSet());                    // Sort N'

      for (Integer i : this.router.getNSet()) {
         toReturn += i.toString() + ",";                          // Build the String representation of N', as a comma separated list of integers
      }

      toReturn = toReturn.substring(0, toReturn.length()-1);      // Remove the trailing comma

      return toReturn;
   }

   // Returns the maximum length of the N' set printed to the console
   // Used to determine the length of the Header
   private int maximumNSetStringLength() {
      int numCommas = this.router.getNodes().size() - 1; // There will be a comma in between every number listed in N'

      int numbers = 0;
      for (int i = 1; i <= this.router.getNodes().size(); i++) {
         numbers += String.valueOf(i).length(); // Determine how long each number is when printed to a string, and sum those lengths
      }

      return numCommas + numbers;
   }

   // Prints a line of dashes of the appropriate length
   public void printDashedLine() {
      String dashes = StepPrinter.buildRepeatingString("-", this.header.length());

      System.out.println(dashes);
   }
   
}