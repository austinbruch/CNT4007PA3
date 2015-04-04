import java.util.ArrayList;

public class Test {

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

   private static void test(String value, ArrayList<Integer> expected, ArrayList<Integer> actual) {
      System.out.print(value + ": ");
      boolean result = compareTwoArrayLists(expected, actual);

      if (result) {
         System.out.print("[PASS]\n");
      } else {
         System.out.print("[FAIL]\n");
         System.out.println("Expected " + value + ": " +  "\t" + expected);
         System.out.println("Actual " + value + ": " + "\t" + actual);
      }
   }

   public static void main(String... args) {

      LinkState ls = new LinkState("network.txt");
      ls.run();

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

      test("Distances", testDistances, ls.getDistances());
      test("P Values", testPValues, ls.getPValues());
      
   }

}