import java.util.ArrayList;

public class Test {

   private static String output = "";

   private static boolean compareTwoArrayLists(ArrayList<Integer> one, ArrayList<Integer> two) {
      boolean equal = true;
      for (int i = 0; i < one.size(); i++) {
         if (!one.get(i).equals(two.get(i))) {
            equal = false;
            break;
         }
      }
      return equal;
   }

   private static boolean test(String value, ArrayList<Integer> expected, ArrayList<Integer> actual) {
      output += value + ": ";

      boolean result = compareTwoArrayLists(expected, actual);

      if (result) {
         output += "[PASS]\n";
      } else {
         output += "[FAIL]\n";
         output += "Expected " + value + ": " +  "\t" + expected + "\n";
         output += "Actual " + value + ": " + "\t" + actual + "\n";

      }

      return result;
   }

   public static void main(String... args) {

      int sourceNodeIndex = 1;

      LinkState ls = new LinkState("network.txt");
      ls.run(sourceNodeIndex);

      ArrayList<Integer> testDistances = new ArrayList<Integer>();
      testDistances.add(0);
      testDistances.add(2);
      testDistances.add(3);
      testDistances.add(1);
      testDistances.add(2);
      testDistances.add(4);

      ArrayList<Integer> testPValues = new ArrayList<Integer>();
      testPValues.add(1);
      testPValues.add(1);
      testPValues.add(5);
      testPValues.add(1);
      testPValues.add(4);
      testPValues.add(5);

      boolean overall;
      overall = test("Distances", testDistances, ls.getDistances());
      overall = test("P Values", testPValues, ls.getPValues()) & overall;

      System.out.println("Overall result: " + (overall ? "[PASS]" : "[FAIL]"));
      System.out.println("-------------------------");
      System.out.println(output);

//*******************************************************************************
      ls = new LinkState("network2.txt");
      ls.run(sourceNodeIndex);

      testDistances = new ArrayList<Integer>();
      testDistances.add(0);
      testDistances.add(10);
      testDistances.add(12);
      testDistances.add(5);
      testDistances.add(9);
      testDistances.add(10);
      testDistances.add(7);
      testDistances.add(6);


      testPValues = new ArrayList<Integer>();
      testPValues.add(1);
      testPValues.add(5);
      testPValues.add(5);
      testPValues.add(1);
      testPValues.add(4);
      testPValues.add(7);
      testPValues.add(4);
      testPValues.add(4);

      output = "";
      overall = test("Distances", testDistances, ls.getDistances());
      overall = test("P Values", testPValues, ls.getPValues()) & overall;

      System.out.println("Overall result: " + (overall ? "[PASS]" : "[FAIL]"));
      System.out.println("-------------------------");
      System.out.println(output);

      
   }

}