import java.lang.*;
import java.math.*;
import java.util.*;

public class Main {

     static class BigIntPoint
     {
          BigInteger x;
          BigInteger y;

               public BigIntPoint(BigInteger x, BigInteger y)
               {
                    this.x = x;
                    this.y = y;
               }
     }

     public static BigInteger maxBigInt(BigInteger x, BigInteger y)
     {
          if(x.compareTo(y) == -1){
               return y;
          }
          else{
               return x;
          }
     }
     public static BigInteger minBigInt(BigInteger x, BigInteger y)
     {
          if(x.compareTo(y) == -1){
               return x;
          }
          else{
               return y;
          }
     }

     public static boolean geBigInt(BigInteger x, BigInteger y) {
          if (x.compareTo(y) == -1)
          {
               return false;
          }
          else
          {
               return true;
          }
     }
     public static boolean leBigInt(BigInteger x, BigInteger y){
          if(x.compareTo(y) == 1)
          {
               return true;
          }
          else
          {
               return false;
          }
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
          BigInteger val = ((q.y.subtract(p.y)).multiply(r.x.subtract(q.x)).subtract(q.x.subtract(p.x)).multiply(r.y.subtract(q.y)));

          int ans = val.compareTo(BigInteger.ZERO);

          if (ans==0) return 0;

          else if (ans == 1) return 1;

          else return 2;

     }
    public static void main (String[] args) {
         ArrayList<BigIntPoint> ownroute = new ArrayList<BigIntPoint>();
         ArrayList<BigIntPoint> cryptroute = new ArrayList<BigIntPoint>();

         // Clumsy way to build drones routes, probably update to allow user input
         BigInteger xone = BigInteger.valueOf(12);
         BigInteger yone = BigInteger.valueOf(25);
         BigInteger xtwo = BigInteger.valueOf(-235);
         BigInteger ytwo = BigInteger.valueOf(30);
         BigIntPoint firstpoint = new BigIntPoint(xone,yone);
         BigIntPoint secondpoint = new BigIntPoint(xtwo,ytwo);

         //Clumsily building the cryptroute
         BigInteger cxone = BigInteger.valueOf(0);
         BigInteger cyone = BigInteger.valueOf(0);
         BigInteger cxtwo = BigInteger.valueOf(10);
         BigInteger cytwo = BigInteger.valueOf(50);
         BigIntPoint cfirstpoint = new BigIntPoint(cxone,cyone);
         BigIntPoint csecondpoint = new BigIntPoint(cxtwo,cytwo);

         //Assemble BigInt "points" into ArrayList
         ownroute.add(firstpoint);
         ownroute.add(secondpoint);

         cryptroute.add(cfirstpoint);
         cryptroute.add(csecondpoint);


         //Test for intersection







         }


}