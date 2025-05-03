package edu.fiu.adwise.structs;

import java.io.Serializable;
import java.math.BigInteger;

public class BigIntPoint implements Serializable
{
    public BigInteger x;
    public BigInteger y;

    public BigIntPoint(BigInteger x, BigInteger y) {
        this.x = x;
        this.y = y;
    }
}