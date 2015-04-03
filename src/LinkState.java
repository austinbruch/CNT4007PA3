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

   private ArrayList<Node> nodes;

   public LinkState(String networkFile) {
      this.networkFile = networkFile;
      this.numNodes = 0;
      this.nodes = new ArrayList<Node>();
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

   private void parseNetworkFile() {

      int currentNode = 0;
      String temp = null;
      try {
         while( (temp = this.networkFileBufferedReader.readLine()) != null ) {
            // input += temp;
            if (!temp.equals("EOF.")) {
               this.numNodes++;
               currentNode++;
               System.out.println(temp);

               Node node = new Node(currentNode);
               node.parseCosts(temp);

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

   // Execute the LinkState routing process
   public void run() {
      // Initialize the Network File Reader
      this.initialize();

      // Parse the Network File to create the Network
      this.parseNetworkFile();
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
