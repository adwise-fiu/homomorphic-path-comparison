package edu.fiu.adwise;

import edu.fiu.adwise.structs.BigIntPoint;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides utility methods for comparing paths represented as lists of BigIntPoint objects.
 * Includes methods for determining intersections between line segments and paths.
 * Based on the algorithm from GeeksforGeeks:
 * <a href="https://www.geeksforgeeks.org/check-if-two-given-line-segments-intersect/">Link here</a>
 */
public class CleartextPathsComparison {

     /**
      * Returns the larger of two BigInteger values.
      *
      * @param a the first BigInteger
      * @param b the second BigInteger
      * @return the larger BigInteger
      */
     public static BigInteger maxBigInt(BigInteger a, BigInteger b) {
          if (a.compareTo(b) < 0) {
               return b;
          } else {
               return a;
          }
     }

     /**
      * Returns the smaller of two BigInteger values.
      *
      * @param a the first BigInteger
      * @param b the second BigInteger
      * @return the smaller BigInteger
      */
     public static BigInteger minBigInt(BigInteger a, BigInteger b) {
          if (a.compareTo(b) < 0) {
               return a;
          } else {
               return b;
          }
     }

     /**
      * Checks if the first BigInteger is greater than or equal to the second.
      *
      * @param a the first BigInteger
      * @param b the second BigInteger
      * @return true if a is greater than or equal to b, false otherwise
      */
     public static boolean geBigInt(BigInteger a, BigInteger b) {
          return a.compareTo(b) >= 0;
     }

     /**
      * Checks if the first BigInteger is less than or equal to the second.
      *
      * @param a the first BigInteger
      * @param b the second BigInteger
      * @return true if a is less than or equal to b, false otherwise
      */
     public static boolean leBigInt(BigInteger a, BigInteger b) {
          return a.compareTo(b) > 0;
     }

     /**
      * Determines if a point lies on a line segment defined by two other points.
      * Used in intersection checks to handle collinearity.
      *
      * @param p the first endpoint of the segment
      * @param q the point to check
      * @param r the second endpoint of the segment
      * @return true if q lies on the segment (p, r), false otherwise
      */
     static boolean onSegment(BigIntPoint p, BigIntPoint q, BigIntPoint r) {
          return leBigInt(q.x, (maxBigInt(p.x, r.x))) && geBigInt(q.x, (minBigInt(p.x, r.x)))
                  && leBigInt(q.y, (maxBigInt(p.y, r.y))) && geBigInt(q.y, (minBigInt(p.y, r.y)));
     }

     /**
      * Determines the orientation of three points (p, q, r).
      * Used to check if the points are collinear, clockwise, or counterclockwise.
      *
      * @param p the first point
      * @param q the second point
      * @param r the third point
      * @return 0 if collinear, 1 if clockwise, 2 if counterclockwise
      */
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
          } else if (ans > 0) {
               return 1;
          } else {
               return 2;
          }
     }

     /**
      * Checks if two line segments intersect.
      *
      * @param p1 the first endpoint of the first segment
      * @param q1 the second endpoint of the first segment
      * @param p2 the first endpoint of the second segment
      * @param q2 the second endpoint of the second segment
      * @return true if the segments intersect, false otherwise
      */
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

     /**
      * Checks if two paths intersect by iterating over their line segments.
      *
      * @param mine the first path as a list of BigIntPoint objects
      * @param theirs the second path as a list of BigIntPoint objects
      * @return true if any segments of the paths intersect, false otherwise
      */
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

     /**
      * Finds the endpoints of the line segment in the unencrypted path where an intersection occurs.
      * If no intersection is found, returns an empty list.
      *
      * @param mine the first path as a list of BigIntPoint objects
      * @param theirs the second path as a list of BigIntPoint objects
      * @return a list of BigIntPoint objects representing the intersecting segment, or an empty list
      */
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