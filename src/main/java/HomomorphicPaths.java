import java.io.IOException;
import java.lang.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.charset.StandardCharsets;

// Starting with this:
// https://www.geeksforgeeks.org/check-if-two-given-line-segments-intersect/

public class HomomorphicPaths {

     // Returns the larger of two BigIntegers
     public static BigInteger maxBigInt(BigInteger a, BigInteger b)
     {
          if(a.compareTo(b) < 0) {
               return b;
          }
          else {
               return a;
          }

     }
     // Returns the lesser of two BigIntegers
     public static BigInteger minBigInt(BigInteger a, BigInteger b)
     {
          if(a.compareTo(b) < 0) {
               return a;
          }
          else {
               return b;
          }
     }

     //Returns true if a is greater than or equal to b
     public static boolean geBigInt(BigInteger a, BigInteger b) {
         return a.compareTo(b) >= 0;

     }
     //Returns true if a is lesser than or equal to b
     public static boolean leBigInt(BigInteger a, BigInteger b) {
         return a.compareTo(b) > 0;
     }
     //Used in doIntersect to avoid issues with collinearity
     static boolean onSegment(BigIntPoint p, BigIntPoint q, BigIntPoint r) {
         return leBigInt(q.x, (maxBigInt(p.x, r.x))) && geBigInt(q.x, (minBigInt(p.x, r.x)))
                 && leBigInt(q.y, (maxBigInt(p.y, r.y))) && leBigInt(q.y, (minBigInt(p.y, r.y)));
     }

     //Tests for clockwise or counterclockwise rotation or collinearity between the 3 points, used in doIntersect
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

          if (ans == 0) {
               return 0;
          }

          else if (ans > 0) {
               return 1;
          }

          else {
               return 2;
          }
     }
     // Tests if line segment (p1,q1) and (p2,q2) intersect
     static boolean doIntersect(BigIntPoint p1, BigIntPoint q1, BigIntPoint p2, BigIntPoint q2)
     {
          int o1 = orientation(p1,q1,p2);
          int o2 = orientation(p1,q1,q2);
          int o3 = orientation(p2,q2,p1);
          int o4 = orientation(p2,q2,q1);

          if (o1 != o2 && o3 != o4) {
               return true;
          }

          if (o1 == 0 && onSegment(p1,p2,q1)) {
               return true;
          }
          if (o2 == 0 && onSegment(p1,q2,q1)) {
               return true;
          }
          if (o3 == 0 && onSegment(p2,p1,q2)) {
               return true;
          }
          if (o4 == 0 && onSegment(p2,q1,q2)) {
               return true;
          }

          return false;
     }

     public static List<BigIntPoint> read_all_paths(String file_path) {
          String route = null;
          try {
               route = Files.readString(Path.of(file_path), StandardCharsets.UTF_8);
          } catch (IOException e) {
               System.err.println("An IOException occured in read_all_paths" + e.getMessage());
          }
          return parse_line(route);
     }

     // TODO: Is this for one line?
     public static List<BigIntPoint> parse_line(String input) {
          List<BigIntPoint> result = new ArrayList<>();

           //Define a regex pattern for extracting pairs of numbers within parentheses
          Pattern pattern = Pattern.compile("\\((\\d+),(\\d+)\\)");

           //Use a Matcher to find matches in the input string
          Matcher matcher = pattern.matcher(input);

           //Iterate through the matches and extract BigInteger values
          while (matcher.find()) {
               String group1 = matcher.group(1);
               String group2 = matcher.group(2);
               BigIntPoint pair = new BigIntPoint(new BigInteger(group1), new BigInteger(group2));
               result.add(pair);
          }
          return result;
     }

     //Boolean function that iterates over two paths looking for intersections
     public static Boolean pathIntersection(List<BigIntPoint> mine, List<BigIntPoint> theirs) {
          for (int i = 0; i < (mine.size() - 1); i++) {
               for (int j = 0; j < (theirs.size() - 1); j++) {
                    if (doIntersect(mine.get(i), mine.get(i + 1), theirs.get(j), theirs.get(j + 1))) {
                         return Boolean.TRUE;
                    }
               }
          }
          return Boolean.FALSE;
     }
     //Returns the two endpoints of the line segment of the unencrypted path where an intersection occurs, otherwise returns (0,0) (0,0)
     public static BigIntPoint [] whereIntersection(List<BigIntPoint> mine, List<BigIntPoint> theirs)
     {
          BigIntPoint[] segment = new BigIntPoint[2];
          BigIntPoint empty = new BigIntPoint(BigInteger.ZERO,BigInteger.ZERO);

          for (int i = 0; i < (mine.size()-1); i++) {
               for (int j = 0; j < (theirs.size()-1); j++) {
                    if(doIntersect(mine.get(i),mine.get(i+1),theirs.get(j),theirs.get(j+1))) {

                         segment[0] = mine.get(i);
                         segment[1] = mine.get(i + 1);

                         return segment;
                    }
                    else {
                           segment[0] = empty;
                           segment[1] = empty;
                           return segment;
                    }
               }
          }
          segment [0] = empty;
          segment [1] = empty;
          return segment;
     }

     public static void main (String[] args) {
          List<BigIntPoint> ownroute;
          List<BigIntPoint> cryptroute;
          BigIntPoint [] location;

          // When this is migarated, we can set a standard file path name.
          String ownroutefile = "ownroutefile.txt";
          String cryptroutefile = "cryptroutefile.txt";

          ownroute = read_all_paths(ownroutefile);
          cryptroute = read_all_paths(cryptroutefile);

          location = whereIntersection(ownroute, cryptroute);

          System.out.printf("Intersection between (%d,%d) and (%d,%d)%n", location[0].x.intValue(), location[0].y.intValue(), location[1].x.intValue(), location[1].y.intValue());
     }
}