package edu.fiu.adwise;

import edu.fiu.adwise.structs.BigIntPoint;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


// Starting with this:
// https://www.geeksforgeeks.org/check-if-two-given-line-segments-intersect/
public class CleartextPathsComparison {

     // Returns the larger of two BigIntegers
     public static BigInteger maxBigInt(BigInteger a, BigInteger b) {
          if (a.compareTo(b) < 0) {
               return b;
          }
          else {
               return a;
          }

     }

     // Returns the lesser of two BigIntegers
     public static BigInteger minBigInt(BigInteger a, BigInteger b) {
          if (a.compareTo(b) < 0) {
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
                  && leBigInt(q.y, (maxBigInt(p.y, r.y))) && geBigInt(q.y, (minBigInt(p.y, r.y)));
     }

     //Tests for clockwise or counterclockwise rotation or collinearity between the 3 points, used in doIntersect
     static int orientation(BigIntPoint p, BigIntPoint q, BigIntPoint r) {
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
     static boolean doIntersect(BigIntPoint p1, BigIntPoint q1, BigIntPoint p2, BigIntPoint q2) {
          int o1 = orientation(p1, q1, p2);
          int o2 = orientation(p1, q1, q2);
          int o3 = orientation(p2, q2, p1);
          int o4 = orientation(p2, q2, q1);

          if (o1 != o2 && o3 != o4) {
               return true;
          }

          if (o1 == 0 && onSegment(p1, p2, q1)) {
               return true;
          }
          if (o2 == 0 && onSegment(p1, q2, q1)) {
               return true;
          }
          if (o3 == 0 && onSegment(p2, p1, q2)) {
               return true;
          }
          if (o4 == 0 && onSegment(p2, q1, q2)) {
               return true;
          }

          return false;
     }

     //Boolean function that iterates over two paths looking for intersections
     public static boolean pathIntersection(List<BigIntPoint> mine, List<BigIntPoint> theirs) {
          for (int i = 0; i < (mine.size() - 1); i++) {
               for (int j = 0; j < (theirs.size() - 1); j++) {
                    if (doIntersect(mine.get(i), mine.get(i + 1), theirs.get(j), theirs.get(j + 1))) {
                         return true;
                    }
               }
          }
          return false;
     }

     // Returns the two endpoints of the line segment of the unencrypted path where an intersection occurs,
     // otherwise returns (0,0) (0,0)
     public static List<BigIntPoint> whereIntersection(List<BigIntPoint> mine, List<BigIntPoint> theirs) {
          List<BigIntPoint> segments = new ArrayList<>();

          for (int j = 0; j < (theirs.size() - 1); j++) {
               for (int i = 0; i < (mine.size() - 1); i++) {
                    if (doIntersect(mine.get(i), mine.get(i + 1), theirs.get(j), theirs.get(j + 1))) {
                         segments.add(mine.get(i));
                         segments.add(mine.get(i + 1));
                    }
               }
          }
          return segments;
     }
}