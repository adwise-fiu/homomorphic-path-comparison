import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IntersectTest {
    @Before
    public void read_properties() {

    }

    @Test
    public void test_intersections() {
        // TODO: Allan please fill this out of reading the text files. Essentially do the same as main here
        assertEquals("hello", "hello");
    }

    @Test
    public void test_drone_collision() {
        Drone kemal = new Drone(1024);
        Drone allan = new Drone(1024);

        // I need the drones to have each other's public key...

        // I need to figure out how to use Homomorphic Encryption Library here...
        kemal.willCollide(allan);
    }
}