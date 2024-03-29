import security.misc.HomomorphicException;
import security.paillier.PaillierCipher;
import security.paillier.PaillierPublicKey;
import security.socialistmillionaire.alice_joye;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


public class EncryptedPathsComparison {

    public EncryptedPathsComparison(alice_joye myself, PaillierPublicKey public_key) {
        this.myself = myself;
    }
    private final alice_joye myself;

    // This method uses Protocol2 to find Max value.
    public BigInteger encryptedMaxBigInt(BigInteger a, BigInteger b){
       boolean z = false;
        try {
            myself.writeInt(2);
            z = myself.Protocol2(a,b);
       } catch (HomomorphicException | IOException | ClassNotFoundException e) {
           System.err.println("Exception in Max: " + e.getMessage());
       }
       if (z){
           return a;
       }else {
           return b;
       }
    }

    //This method runs Protocol2 to find Min value. I could've just used Max but it reads easier this way later on.
    public BigInteger encryptedMinBigInt(BigInteger a, BigInteger b){
        boolean z = false;
        try {
            myself.writeInt(2);
            z = myself.Protocol2(a,b);
        } catch (HomomorphicException | IOException | ClassNotFoundException e) {
            System.err.println("Exception in Min: " + e.getMessage());
        }
        if (z){
            return b;
        }else {
            return a;
        }
    }

    //Checks orientation of 3 points, for use in doIntersect
    public int encryptedOrientation(BigIntPoint p, BigIntPoint q, BigIntPoint r, PaillierPublicKey public_key, BigInteger encryptedzero) {

        try {
            // From this line BigInteger temp1 = q.y.subtract(p.y);
            BigInteger temp1 = PaillierCipher.subtract(q.y, p.y, public_key);
            // From this line BigInteger temp2 = r.x.subtract(q.x);
            BigInteger temp2 = PaillierCipher.subtract(r.x, q.x, public_key);
            // From this line BigInteger temp3 = q.x.subtract(p.x);
            BigInteger temp3 = PaillierCipher.subtract(q.x, p.x, public_key);
            //From this line BigInteger temp4 = r.y.subtract(q.y);
            BigInteger temp4 = PaillierCipher.subtract(r.y, q.y, public_key);
            //From this line BigInteger temp5 = temp1.multiply(temp2);
            myself.writeInt(1);
            BigInteger temp5 = myself.multiplication(temp1,temp2);
            //From this line BigInteger temp6 = temp3.multiply(temp4);
            myself.writeInt(1);
            BigInteger temp6 = myself.multiplication(temp3,temp4);
            //From this line BigInteger val = temp5.subtract(temp6);
            BigInteger val = PaillierCipher.subtract(temp5,temp6, public_key);

            //Protocol2 returns boolean param1 >= param2.

            myself.writeInt(2);
            boolean testcase1 = (myself.Protocol2(val,encryptedzero));
            myself.writeInt(2);
            boolean testcase2 = (myself.Protocol2(encryptedzero,val));
            //if val = 0
            if (testcase1 && testcase2) {
                return 0;
            // if val > 0
            } else if (testcase1 && !testcase2) {
                return 1;
            // if neither, then val < 0
            } else return 2;

        } catch (HomomorphicException | IOException | ClassNotFoundException a) {
            System.err.println("Exception in encryptedOrientation: " + a.getMessage());
        }
        return 3;
    }

    public boolean encryptedOnSegment (BigIntPoint p, BigIntPoint q, BigIntPoint r) {
        boolean z = false;

        try {
            // I had to pull these out of comparison statement to work with BobThread
            BigInteger u = encryptedMaxBigInt(p.x,r.x);
            BigInteger v = encryptedMinBigInt(p.x,r.x);
            BigInteger w = encryptedMaxBigInt(p.y,r.y);
            BigInteger x = encryptedMinBigInt(p.y,r.y);

            /*I have to run these one at a time to work with BobThread. Maybe passing 4 and calling
            Protocol2 4 times in BobThread would speed things up.
             */
            myself.writeInt(2);
            boolean a = myself.Protocol2(u,q.x);
            myself.writeInt(2);
            boolean b = myself.Protocol2(q.x,v);
            myself.writeInt(2);
            boolean c = myself.Protocol2(w,q.y);
            myself.writeInt(2);
            boolean d = myself.Protocol2(q.y,x);

            z =  a && b && c && d;

        } catch (HomomorphicException | IOException | ClassNotFoundException e){
            System.err.println("Exception in OnSegment" + e.getMessage());
        }

        return z;
    }

    public boolean encryptedDoIntersect(BigIntPoint p1, BigIntPoint q1, BigIntPoint p2, BigIntPoint q2, PaillierPublicKey public_key, BigInteger encryptedzero)
    {
        int o1 = encryptedOrientation(p1,q1,p2,public_key,encryptedzero);
        int o2 = encryptedOrientation(p1,q1,q2,public_key,encryptedzero);
        int o3 = encryptedOrientation(p2,q2,p1,public_key,encryptedzero);
        int o4 = encryptedOrientation(p2,q2,q1,public_key,encryptedzero);


        if (o1 != o2 && o3 != o4) {
            return true;
        }


        if (o1 == 0 && encryptedOnSegment(p1,p2,q1)) {
            return true;
        }
        if (o2 == 0 && encryptedOnSegment(p1,q2,q1)) {
            return true;
        }
        if (o3 == 0 && encryptedOnSegment(p2,p1,q2)) {
            return true;
        }
        if (o4 == 0 && encryptedOnSegment(p2,q1,q2)) {
            return true;
        }

        return false;
    }

    public boolean encryptedPathIntersection(List<BigIntPoint> mine, List<BigIntPoint> theirs, PaillierPublicKey public_key, BigInteger encryptedzero) {
        for (int i = 0; i < (mine.size() - 1); i++) {
            for (int j = 0; j < (theirs.size() - 1); j++) {
                if (encryptedDoIntersect(mine.get(i), mine.get(i + 1), theirs.get(j), theirs.get(j + 1), public_key, encryptedzero)) {
                    System.out.println("Intersection!");
                    return true;
                    }
                }
            }
        System.out.println("No intersections!");
        return false;
    }

    public List<BigIntPoint> encryptedWhereIntersection(List<BigIntPoint> mine, List<BigIntPoint> theirs, PaillierPublicKey public_key, BigInteger encryptedzero)
    {
        List<BigIntPoint> segments = new ArrayList<>();
        List index = new ArrayList<>();

        for (int j = 0; j < (theirs.size()-1); j++) {
            for (int i = 0; i < (mine.size()-1); i++) {
                if(encryptedDoIntersect(mine.get(i),mine.get(i+1),theirs.get(j),theirs.get(j+1), public_key, encryptedzero)) {

                    index.add(i);
                    index.add(i+1);


                }
            }
        }
        try {
            myself.writeInt(0);
        }catch(IOException e){
            e.printStackTrace();
        }
        return index;
    }
}