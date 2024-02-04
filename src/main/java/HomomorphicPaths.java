import java.io.IOException;
import java.lang.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.file.Paths;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;

public class HomomorphicPaths {

     public static class BigIntPoint
     {
          BigInteger x;
          BigInteger y;

          public BigIntPoint(BigInteger x, BigInteger y)
          {
               this.x = x;
               this.y = y;
          }
     }

     public static BigInteger maxBigInt(BigInteger a, BigInteger b)
     {
          if(a.compareTo(b) == -1) return b;
          else return a;

     }
     public static BigInteger minBigInt(BigInteger a, BigInteger b)
     {
          if(a.compareTo(b) == -1) return a;
          else return b;

     }

     public static boolean geBigInt(BigInteger a, BigInteger b) {
          if (a.compareTo(b) == -1) return false;
          else return true;

     }
     public static boolean leBigInt(BigInteger a, BigInteger b){
          if(a.compareTo(b) == 1) return true;
          else return false;
     }
     static boolean onSegment(BigIntPoint p, BigIntPoint q, BigIntPoint r)
     {
          if (leBigInt(q.x,(maxBigInt(p.x, r.x))) && geBigInt(q.x,(minBigInt(p.x,r.x)))
                  && leBigInt(q.y,(maxBigInt(p.y,r.y))) && leBigInt(q.y,(minBigInt(p.y,r.y))))
               return true;

          return false;
     }

     static int orientation(BigIntPoint p, BigIntPoint q, BigIntPoint r)
     {
          BigInteger temp1 = q.y.subtract(p.y);
          BigInteger temp2 = r.x.subtract(q.x);
          BigInteger temp3 = q.x.subtract(p.x);
          BigInteger temp4 = r.y.subtract(q.y);
          BigInteger temp5 = temp1.multiply(temp2);
          BigInteger temp6 = temp3.multiply(temp4);
          BigInteger val = temp5.subtract(temp6);

          int ans = val.compareTo(BigInteger.ZERO);

          if (ans==0) return 0;

          else if (ans == 1) return 1;

          else return 2;
     }

     static boolean doIntersect(BigIntPoint p1, BigIntPoint q1, BigIntPoint p2, BigIntPoint q2)
     {
          int o1 = orientation(p1,q1,p2);
          int o2 = orientation(p1,q1,q2);
          int o3 = orientation(p2,q2,p1);
          int o4 = orientation(p2,q2,q1);

          if (o1 != o2 && o3 != o4)
               return true;

          if (o1 == 0 && onSegment(p1,p2,q1)) return true;
          if (o2 == 0 && onSegment(p1,q2,q1)) return true;
          if (o3 == 0 && onSegment(p2,p1,q2)) return true;
          if (o4 == 0 && onSegment(p2,q1,q2)) return true;

          return false;
     }

     // Can you get this to work with multiple lines on each text file?
     public static List<BigIntPoint> read_all_paths(String file_path) {
          Scanner scanner;
          String ownrouteinput = null;

          // TODO: To get good coding practice, look into using the Java equivalent of try with
          try {
               scanner = new Scanner(Paths.get(file_path), StandardCharsets.UTF_8);
               ownrouteinput = scanner.useDelimiter("\\A").next();
               scanner.close();
          } catch (IOException e) {
               System.err.println("An IOException occurred: " + e.getMessage());
               e.printStackTrace();
          }
         return parse_line(ownrouteinput);
     }

     // TODO: Is this for one line?
     public static List<BigIntPoint> parse_line(String input) {
          List<BigIntPoint> result = new ArrayList<>();

          // Define a regex pattern for extracting pairs of numbers within parentheses
          Pattern pattern = Pattern.compile("\\((\\d+),(\\d+)\\)");

          // Use a Matcher to find matches in the input string
          Matcher matcher = pattern.matcher(input);

          // Iterate through the matches and extract BigInteger values
          while (matcher.find()) {
               String group1 = matcher.group(1);
               String group2 = matcher.group(2);
               BigIntPoint pair = new BigIntPoint(new BigInteger(group1), new BigInteger(group2));
               result.add(pair);
          }
          return result;
     }

     public static void main (String[] args) {
          List<BigIntPoint> ownroute;
          List<BigIntPoint> cryptroute;

          // When this is migarated, we can set a standard file path name.
          String ownroutefile = "ownroutefile.txt";
          String cryptroutefile = "cryptroutefile.txt";

          ownroute = read_all_paths(ownroutefile);
          cryptroute = read_all_paths(cryptroutefile);

          // TODO: Rewrite this to a Boolean, and place this on IntersectTest, on test_intersections
          // Test for intersection
          for (int i = 0; i < (ownroute.size()-1); i++){
               for (int j = 0; j < (cryptroute.size()-1); j++) {
                    if(doIntersect(ownroute.get(i),ownroute.get(i+1),cryptroute.get(j),cryptroute.get(j+1)))
                         System.out.println("Yes");
               }
          }

     }
}