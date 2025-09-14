/*
 * Copyright (c) 2024 ADWISE Lab, Florida International University (FIU), AndrewQuijano
 * Licensed under the MIT License. See LICENSE file in the project root for details.
 */
package edu.fiu.adwise.drone_collision_avoidance;

import edu.fiu.adwise.drone_collision_avoidance.structs.BigIntPoint;
import edu.fiu.adwise.homomorphic_encryption.dgk.DGKOperations;
import edu.fiu.adwise.homomorphic_encryption.misc.HomomorphicException;
import edu.fiu.adwise.homomorphic_encryption.paillier.PaillierCipher;
import edu.fiu.adwise.homomorphic_encryption.socialistmillionaire.alice;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Provides methods for encrypted path comparison using homomorphic encryption.
 * Includes functionality for finding intersections and performing geometric operations
 * on encrypted data.
 */
public class EncryptedPathsComparison {

    /** Logger for logging messages and errors. */
    private static final Logger logger = LogManager.getLogger(EncryptedPathsComparison.class);

    /** The Alice instance used for encrypted operations. */
    private final alice myself;

    /**
     * Constructs an EncryptedPathsComparison instance with the specified Alice instance.
     *
     * @param myself the Alice instance for encrypted operations
     */
    public EncryptedPathsComparison(alice myself) {
        this.myself = myself;
    }

    /**
     * Finds the minimum of two encrypted BigInteger values using Protocol2.
     *
     * @param a the first encrypted BigInteger
     * @param b the second encrypted BigInteger
     * @return the minimum encrypted BigInteger
     * @throws IOException if an I/O error occurs
     * @throws HomomorphicException if a homomorphic encryption error occurs
     * @throws ClassNotFoundException if a class is not found during deserialization
     */
    public BigInteger encryptedMaxBigInt(BigInteger a, BigInteger b)
            throws IOException, HomomorphicException, ClassNotFoundException {

        myself.writeInt(2);
        boolean z = myself.Protocol2(a, b);

        if (z) {
            return a;
        }
        else {
            return b;
        }
    }
    /**
     * Finds the minimum of two encrypted BigInteger values using Protocol2.
     * This method runs Protocol2 to find Min value.
     * I could've just used Max, but it reads easier this way later on.
     *
     * @param a the first encrypted BigInteger
     * @param b the second encrypted BigInteger
     * @return the minimum encrypted BigInteger
     * @throws IOException if an I/O error occurs
     * @throws HomomorphicException if a homomorphic encryption error occurs
     * @throws ClassNotFoundException if a class is not found during deserialization
     */
    public BigInteger encryptedMinBigInt(BigInteger a, BigInteger b)
            throws IOException, HomomorphicException, ClassNotFoundException {

        myself.writeInt(2);
        boolean z = myself.Protocol2(a, b);

        if (z) {
            return b;
        }
        else {
            return a;
        }
    }

    /**
     * Determines the orientation of three encrypted points, used in the doIntersect method.
     * Used to check if the points are collinear, clockwise, or counterclockwise.
     *
     * @param p the first point
     * @param q the second point
     * @param r the third point
     * @return 0 if collinear, 1 if clockwise, 2 if counterclockwise
     */
    public int encryptedOrientation(BigIntPoint p, BigIntPoint q, BigIntPoint r) {
        BigInteger temp1;
        BigInteger temp2;
        BigInteger temp3;
        BigInteger temp4;
        BigInteger val;
        BigInteger encrypted_zero;

        try {
            if (myself.isDGK()) {
                encrypted_zero = myself.getDGKPublicKey().ZERO();
                // From this line BigInteger temp1 = q.y.subtract(p.y);
                temp1 = DGKOperations.subtract(q.y, p.y, myself.getDGKPublicKey());
                // From this line BigInteger temp2 = r.x.subtract(q.x);
                temp2 = DGKOperations.subtract(r.x, q.x, myself.getDGKPublicKey());
                // From this line BigInteger temp3 = q.x.subtract(p.x);
                temp3 = DGKOperations.subtract(q.x, p.x, myself.getDGKPublicKey());
                //From this line BigInteger temp4 = r.y.subtract(q.y);
                temp4 = DGKOperations.subtract(r.y, q.y, myself.getDGKPublicKey());
            }
            else {
                encrypted_zero = myself.getPaillierPublicKey().ZERO();
                // From this line BigInteger temp1 = q.y.subtract(p.y);
                temp1 = PaillierCipher.subtract(q.y, p.y, myself.getPaillierPublicKey());
                // From this line BigInteger temp2 = r.x.subtract(q.x);
                temp2 = PaillierCipher.subtract(r.x, q.x, myself.getPaillierPublicKey());
                // From this line BigInteger temp3 = q.x.subtract(p.x);
                temp3 = PaillierCipher.subtract(q.x, p.x, myself.getPaillierPublicKey());
                //From this line BigInteger temp4 = r.y.subtract(q.y);
                temp4 = PaillierCipher.subtract(r.y, q.y, myself.getPaillierPublicKey());
            }
            //From this line BigInteger temp5 = temp1.multiply(temp2);
            myself.writeInt(1);
            BigInteger temp5 = myself.multiplication(temp1, temp2);
            //From this line BigInteger temp6 = temp3.multiply(temp4);
            myself.writeInt(1);
            BigInteger temp6 = myself.multiplication(temp3, temp4);
            //From this line BigInteger val = temp5.subtract(temp6);
            if (myself.isDGK()) {
                val = DGKOperations.subtract(temp5, temp6, myself.getDGKPublicKey());
            }
            else {
                val = PaillierCipher.subtract(temp5, temp6, myself.getPaillierPublicKey());
            }

            // Protocol2 returns boolean param1 >= param2.
            myself.writeInt(2);
            boolean testcase1 = myself.Protocol2(val, encrypted_zero);
            myself.writeInt(2);
            boolean testcase2 = myself.Protocol2(encrypted_zero, val);
            //if val = 0
            if (testcase1 && testcase2) {
                return 0;
            // if val > 0
            }
            else if (testcase1) {
                return 1;
            // if neither, then val < 0
            }
            else {
                return 2;
            }

        }
        catch (HomomorphicException | IOException | ClassNotFoundException a) {
            logger.fatal(a);
            throw new RuntimeException(a);
        }
    }

    /**
     * Checks if a point lies on a line segment defined by two other encrypted points.
     *
     * @param p the first endpoint of the segment
     * @param q the point to check
     * @param r the second endpoint of the segment
     * @return true if q lies on the segment (p, r), false otherwise
     */
    public boolean encryptedOnSegment (BigIntPoint p, BigIntPoint q, BigIntPoint r) {
        boolean z = false;
        try {
            // I had to pull these out of a comparison statement to work with BobThread
            BigInteger u = encryptedMaxBigInt(p.x, r.x);
            BigInteger v = encryptedMinBigInt(p.x, r.x);
            BigInteger w = encryptedMaxBigInt(p.y, r.y);
            BigInteger x = encryptedMinBigInt(p.y, r.y);

            myself.writeInt(2);
            boolean a = myself.Protocol2(u, q.x);
            myself.writeInt(2);
            boolean b = myself.Protocol2(q. x,v);
            myself.writeInt(2);
            boolean c = myself.Protocol2(w,q.y);
            myself.writeInt(2);
            boolean d = myself.Protocol2(q.y, x);
            z =  a && b && c && d;
        }
        catch (HomomorphicException | IOException | ClassNotFoundException e){
            logger.fatal("Exception in OnSegment {}", e.getMessage());
        }
        return z;
    }

    /**
     * Checks if two encrypted line segments intersect.
     *
     * @param p1 the first endpoint of the first segment
     * @param q1 the second endpoint of the first segment
     * @param p2 the first endpoint of the second segment
     * @param q2 the second endpoint of the second segment
     * @return true if the segments intersect, false otherwise
     */
    public boolean encryptedDoIntersect(BigIntPoint p1, BigIntPoint q1,
                                        BigIntPoint p2, BigIntPoint q2) {
        int o1 = encryptedOrientation(p1, q1, p2);
        int o2 = encryptedOrientation(p1, q1, q2);
        int o3 = encryptedOrientation(p2, q2, p1);
        int o4 = encryptedOrientation(p2, q2, q1);

        if (o1 != o2 && o3 != o4) {
            return true;
        }
        if (o1 == 0 && encryptedOnSegment(p1, p2, q1)) {
            return true;
        }
        if (o2 == 0 && encryptedOnSegment(p1, q2, q1)) {
            return true;
        }
        if (o3 == 0 && encryptedOnSegment(p2, p1, q2)) {
            return true;
        }
        if (o4 == 0 && encryptedOnSegment(p2, q1, q2)) {
            return true;
        }

        return false;
    }

    /**
     * Finds the indices of the line segments in the encrypted paths where intersections occur.
     *
     * @param mine the first path as a list of encrypted BigIntPoint objects
     * @param theirs the second path as a list of encrypted BigIntPoint objects
     * @return a list of indices representing the intersecting segments
     * @throws IOException if an I/O error occurs
     * @throws HomomorphicException if a homomorphic encryption error occurs
     */
    public List<Integer> encryptedWhereIntersection(List<BigIntPoint> mine, List<BigIntPoint> theirs)
            throws IOException, HomomorphicException {
        List<Integer> index = new ArrayList<>();
        long start_wait = System.nanoTime();

        // This is where I was picturing the threading going, so that the calls from these for loops can be run separately,
        // My concern is that Bob might need to be able to multi-thread his part of the protocols to see proper speed gains
        for (int j = 0; j < (theirs.size() - 1); j++) {
            for (int i = 0; i < (mine.size() - 1); i++) {
                if(encryptedDoIntersect(mine.get(i), mine.get(i + 1), theirs.get(j), theirs.get(j + 1))) {
                    index.add(i);
                    index.add(i+1);
                }
            }
        }
        // This will terminate bobThread, see line 38
        myself.writeInt(0);

        long end_wait = System.nanoTime();
        double wait_time = (double) (end_wait - start_wait);
        wait_time = wait_time/1000000;
        logger.info(String.format("[Alice] completed intersection checking %f ms", wait_time));
        return index;
    }
}