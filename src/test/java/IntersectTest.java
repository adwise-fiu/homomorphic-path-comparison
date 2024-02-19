import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class IntersectTest {

    @Before
    public void read_properties() {

    }

    @Test
    public void test_intersections() throws Exception {
        String answers_path = new File("data/testroutine.csv").toString();
        //Parse CSV file
        try (BufferedReader br = new BufferedReader(new FileReader(answers_path))){
            String assertstring;
            String line;
            while ((line = br.readLine()) != null){
                String [] values = line.split(",");
                String ownroute = values[0];
                String cryptroute = values[1];
                String expected_result = values[2];
                System.out.println(ownroute);
                //Parsing routes
                List<BigIntPoint> ownroute_list = HomomorphicPaths.read_all_paths(ownroute);
                List<BigIntPoint> cryptroute_list = HomomorphicPaths.read_all_paths(cryptroute);
                //Testing for intersection
                boolean output = HomomorphicPaths.pathIntersection(ownroute_list, cryptroute_list);
                if (output == true) {
                    assertstring = "true";
                } else if (output == false) {
                     assertstring = "false";
                } else {
                     assertstring = "error";
                }
                assertEquals(expected_result, assertstring);
            }
        }
    }

    @Test
    public void test_drone_collision() {
        Drone kemal = new Drone(2048);
        Drone allan = new Drone(2048);

        // I need the drones to have each other's public key...

        // I need to figure out how to use Homomorphic Encryption Library here...
        kemal.willCollide(allan);
    }
}