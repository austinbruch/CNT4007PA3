/**
 * @author Austin Bruch
 * CNT4007C Spring 2015
 * Programming Assignment 3
 * StepPrinter Class
**/

import java.util.Collections;

public class StepPrinter {

   // Reference to the related LinkState instance
   private LinkState linkState;

   private String header;

   private int sourceNodeIndex;

   public StepPrinter(LinkState linkState, int sourceNodeIndex) {
      this.linkState = linkState;
      this.header = null;
      this.sourceNodeIndex = sourceNodeIndex;
      this.initialize();
   }

   private void initialize() {
      this.buildHeader();
   }

   // Print the header for the Dijkstra's Algorithm output
   // Specify which source node we are using to not print columns for that Node
   private void buildHeader() {
      String header = "";

      header += "Step\t";

      int numTabsNeeded = this.linkState.getNodes().size() / 6;
      if (this.linkState.getNodes().size() % 6 != 0) {
         numTabsNeeded++;
      }

      header += "N\'";

      for (int i = 0; i <= numTabsNeeded; i++) {
         header += "\t";
      }

      for (int i = 1; i <= this.linkState.getNodes().size(); i++) {
         if (i != this.sourceNodeIndex) {
            header += "D(" + i + "),p(" + i + ")\t";
         }
      }

      this.header = header;
   }

   public void printHeader(int sourceNodeIndex) {
      System.out.println(this.header);
   } 

   public void printStatusLine(int step) {
      String statusLine = "";

      statusLine += step + "\t";

      int numTabsNeeded = this.linkState.getNodes().size()  / 6;
      if (this.linkState.getNodes().size() % 6 != 0) {
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

      

      for (int i = 0; i < this.linkState.getNodes().size(); i++) {
         int j = i + 1;
         if (j != this.sourceNodeIndex) {
            if (!this.linkState.getNSet().contains(j)) {
               int distance = this.linkState.getDistances().get(i);
               int pValue = this.linkState.getPValues().get(i);

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

      Collections.sort(this.linkState.getNSet());

      for (Integer i : this.linkState.getNSet()) {
         toReturn += i.toString() + ",";
      }

      toReturn = toReturn.substring(0, toReturn.length()-1);

      return toReturn;
   }

   // Returns the maximum length of the N' set printed to the console
   private int maximumNSetStringLength() {
      int numCommas = this.linkState.getNodes().size() - 1;

      int numbers = 0;
      for (int i = 1; i <= this.linkState.getNodes().size(); i++) {
         numbers += String.valueOf(i).length();
      }

      return numCommas + numbers;
   }

   // Prints a line of dashes of the appropriate length
   public void printDashedLine() {
      String dashes = "";
      int numTabs = this.header.length() - this.header.replace("\t","").length();
      for (int i = 0; i < this.header.length() + (numTabs * 6); i++) {
         dashes += "-";
      }

      System.out.println(dashes);
   }
   
}