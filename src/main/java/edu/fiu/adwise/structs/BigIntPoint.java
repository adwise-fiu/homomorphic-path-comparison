/*
 * Copyright (c) 2024 ADWISE Lab, Florida International University (FIU), AndrewQuijano
 * Licensed under the MIT License. See LICENSE file in the project root for details.
 */
package edu.fiu.adwise.structs;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Represents a point in a 2D space with coordinates as BigInteger values.
 * This class is serializable and can be used in cryptographic computations
 * or other applications requiring large integer precision.
 */
public class BigIntPoint implements Serializable {

    /** The x-coordinate of the point. */
    public BigInteger x;

    /** The y-coordinate of the point */
    public BigInteger y;

    /**
     * Constructs a BigIntPoint with the specified x and y coordinates.
     *
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     */
    public BigIntPoint(BigInteger x, BigInteger y) {
        this.x = x;
        this.y = y;
    }
}