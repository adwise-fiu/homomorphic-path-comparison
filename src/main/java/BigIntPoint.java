import java.io.Serializable;
import java.math.BigInteger;

public class BigIntPoint implements Serializable
{
    BigInteger x;
    BigInteger y;

    public BigIntPoint(BigInteger x, BigInteger y)
    {
        this.x = x;
        this.y = y;
    }
}