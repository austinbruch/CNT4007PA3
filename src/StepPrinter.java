/**
 * @author Austin Bruch
 * CNT4007C Spring 2015
 * Programming Assignment 3
 * StepPrinter Class
**/

import java.util.Collections;

public class StepPrinter {

   public static int TAB_LENGTH = 8;

   // Reference to the related Router instance
   private Router router;

   // The header for the Algorithm printout
   private String header;

   // The index of the Source node for the given Dijkstra's Algorithm execution
   private int sourceNodeIndex;

   // Integer array used to maintain the index into the step status line each column needs to begin at, for columns N', D(2),p(2), ..., D(N),p(N)
   private int[] columnIndices;

   private int numNodes;

   public StepPrinter(Router router, int sourceNodeIndex) {
      this.router = router;
      this.header = null;
      this.sourceNodeIndex = sourceNodeIndex;
      this.numNodes = this.router.getNodes().size();
      this.columnIndices = new int[this.numNodes];
      this.initialize();
   }

   // Initialize this StepPrinter
   private void initialize() {
      this.buildHeader();
   }

   private static String buildRepeatingString(String toRepeat, int howManyTimes) {
      String toReturn = "";
      
      for (int i = 0; i < howManyTimes; i++) {
         toReturn += toRepeat;
      }
      
      return toReturn;
   }

   // Print the header for the Dijkstra's Algorithm output
   // Specify which source node we are using to not print columns for that Node
   private void buildHeader() {
      String header = "";

      // if (String.valueOf(this.numNodes).length() >= 8) { // The Step field will be longer than 8 spaces
      //    int quotient = String.valueOf(this.numNodes).length() / 8);
      //    int remainder = String.valueOf(this.numNodes).length() % 8;

      // } else { // The Step field will be less than 8 spaces
      //    header += "Step    ";
      // }

      header += "Step" + StepPrinter.buildRepeatingString(" ", TAB_LENGTH - "Step".length());

      this.columnIndices[0] = ("Step" + StepPrinter.buildRepeatingString(" ", TAB_LENGTH - "Step".length())).length();

      int nSetLength = this.maximumNSetStringLength();

      int quotient = nSetLength / TAB_LENGTH;
      int remainder = nSetLength % TAB_LENGTH;

      if (remainder != TAB_LENGTH - 1) {
         this.columnIndices[1] = TAB_LENGTH * (quotient + 1) + this.columnIndices[0]; // This is where D(2),p(2) starts   
      } else {
         this.columnIndices[1] = TAB_LENGTH * (quotient + 2) + this.columnIndices[0]; // This is where D(2),p(2) starts   
      }
      
      for (int i = 2; i < this.columnIndices.length; i++) {
         this.columnIndices[i] = this.columnIndices[i-1] + 2 * TAB_LENGTH;
      }

      header += "N\'" + StepPrinter.buildRepeatingString(" ",this.columnIndices[1] - this.columnIndices[0] - "N\'".length());

      for (int i = 1; i <= this.router.getNodes().size(); i++) {
         if (i != this.sourceNodeIndex) {
            if (i < this.router.getNodes().size()) {
               String temp = "D(" + i + "),p(" + i + ")";
               String spaces = StepPrinter.buildRepeatingString(" ", this.columnIndices[i] - this.columnIndices[i-1] - temp.length());
               header += temp + spaces;
            } else {
               String temp = "D(" + i + "),p(" + i + ")";
               String spaces = StepPrinter.buildRepeatingString(" ", TAB_LENGTH * 2 - temp.length());
               header += temp + spaces;
            }
            
         }
      }

      this.header = header;
   }

   // Prints the Header for the Algorithm's output
   public void printHeader(int sourceNodeIndex) {
      System.out.println(this.header);
   } 

   // Prints the status line of the algorithm, indicating the current status of the Distance Vector and P vector
   public void printStatusLine(int step) {
      String statusLine = "";

      statusLine += step;
      statusLine += StepPrinter.buildRepeatingString(" ", this.columnIndices[0] - String.valueOf(statusLine).length());

      statusLine += this.getContentsOfNSet();
      statusLine += StepPrinter.buildRepeatingString(" ", this.columnIndices[1] - String.valueOf(statusLine).length());

      for (int i = 0; i < this.router.getNodes().size(); i++) {
         int j = i + 1;
         if (j != this.sourceNodeIndex) {
            if (!this.router.getNSet().contains(j)) {
               int distance = this.router.getDistances().get(i);
               int pValue = this.router.getPValues().get(i);

               if (distance == -1) {
                  statusLine += "N";
                  if (i+1 != this.router.getNodes().size()) {
                     statusLine += StepPrinter.buildRepeatingString(" ", this.columnIndices[i+1] - String.valueOf(statusLine).length());
                  } else {
                     statusLine += StepPrinter.buildRepeatingString(" ", TAB_LENGTH * 2 - "N".length());
                  }
               } else {
                  statusLine += Integer.toString(distance) + "," + Integer.toString(pValue); 
                  if (i+1 != this.router.getNodes().size()) {
                     statusLine += StepPrinter.buildRepeatingString(" ", this.columnIndices[i+1] - String.valueOf(statusLine).length());
                  } else {
                     statusLine += StepPrinter.buildRepeatingString(" ", TAB_LENGTH * 2 - (Integer.toString(distance) + "," + Integer.toString(pValue)).length());
                  }
               }
            } else {
               if (i+1 != this.router.getNodes().size()) {
                  statusLine += StepPrinter.buildRepeatingString(" ", this.columnIndices[i+1] - String.valueOf(statusLine).length());
               } else {
                  statusLine += StepPrinter.buildRepeatingString(" ", TAB_LENGTH * 2);
               }
         }
            }
      }

      System.out.println(statusLine);
   }

   // Returns a String representation of the sorted version of the N' set of Nodes
   private String getContentsOfNSet() {
      String toReturn = "";

      Collections.sort(this.router.getNSet());

      for (Integer i : this.router.getNSet()) {
         toReturn += i.toString() + ",";
      }

      toReturn = toReturn.substring(0, toReturn.length()-1);

      return toReturn;
   }

   // Returns the maximum length of the N' set printed to the console
   private int maximumNSetStringLength() {
      int numCommas = this.router.getNodes().size() - 1;

      int numbers = 0;
      for (int i = 1; i <= this.router.getNodes().size(); i++) {
         numbers += String.valueOf(i).length();
      }

      return numCommas + numbers;
   }

   // Prints a line of dashes of the appropriate length
   public void printDashedLine() {
      String dashes = "";
      
      for (int i = 0; i < this.header.length(); i++) {
         dashes += "-";
      }

      System.out.println(dashes);
   }
   
}