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

public class LinkState {

   // Name of the specified Network File
   private String networkFile;
   private BufferedReader networkFileBufferedReader;

   // List of all Nodes in this Network, in order from 1 to N
   private ArrayList<Node> nodes;

   // Router instance variable
   private Router router;

   public LinkState(String networkFile) {
      this.networkFile = networkFile;
      this.nodes = new ArrayList<Node>();
   }

   public ArrayList<Node> getNodes() {
      return this.nodes;
   }

   // Only needed for testing purposes
   public ArrayList<Integer> getDistances() {
      return this.router.getDistances();
   }

   // Only needed for testing purposes
   public ArrayList<Integer> getPValues() {
      return this.router.getPValues();
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
               currentNode++;

               Node node = new Node(currentNode);
               node.parseCosts(costs);    // Figure out the costs to other Nodes from the new Node
               this.nodes.add(node);      // Add the new Node to the list of Nodes
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

   // Execute the LinkState routing process
   public void run(int sourceNodeIndex) {
      // Initialize the Network File Reader
      this.initialize();

      // Parse the Network File to create the Network of Nodes
      this.parseNetworkFile();

      // Find all routes from the specified source Node
      this.router = new Router(this);
      this.router.route(sourceNodeIndex);
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