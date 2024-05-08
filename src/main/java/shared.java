import org.apache.commons.io.serialization.ValidatingObjectInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import security.dgk.DGKOperations;
import security.dgk.DGKPublicKey;
import security.misc.HomomorphicException;
import security.paillier.PaillierCipher;
import security.paillier.PaillierPublicKey;

import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class shared {

    private static final Logger logger = LogManager.getLogger(shared.class);

    public static ValidatingObjectInputStream get_ois(Socket socket) throws IOException {
        ValidatingObjectInputStream ois = new ValidatingObjectInputStream(socket.getInputStream());
        ois.accept(
                java.util.List.class,
                BigIntPoint.class,
                java.lang.Number.class,
                java.math.BigInteger.class
        );
        ois.accept("[B");
        ois.accept("[L*");
        return ois;
    }

    public static List<BigIntPoint> read_all_paths(String file_path) {
        String route = null;
        try {
            route = Files.readString(Path.of(file_path), StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            logger.fatal(e);
        }
        return parse_line(route);
    }

    public static List<BigIntPoint> parse_line(String input) {
        List<BigIntPoint> result = new ArrayList<>();

        //Define a regex pattern for extracting pairs of numbers within parentheses
        Pattern pattern = Pattern.compile("\\((\\d+),(\\d+)\\)");

        //Use a Matcher to find matches in the input string
        Matcher matcher = pattern.matcher(input);

        //Iterate through the matches and extract BigInteger values
        while (matcher.find()) {
            String group1 = matcher.group(1);
            String group2 = matcher.group(2);
            BigIntPoint pair = new BigIntPoint(new BigInteger(group1), new BigInteger(group2));
            result.add(pair);
        }
        return result;
    }

    public static List<BigIntPoint> encrypt_paillier(List<BigIntPoint> input_path,
                                                     PaillierPublicKey paillier_public_key)
            throws HomomorphicException {

        List<BigIntPoint> encrypted_path = new ArrayList<>();
        for (BigIntPoint bigIntPoint : input_path) {
            BigInteger their_x = PaillierCipher.encrypt(bigIntPoint.x.longValue(), paillier_public_key);
            BigInteger their_y = PaillierCipher.encrypt(bigIntPoint.y.longValue(), paillier_public_key);

            BigIntPoint theirs = new BigIntPoint(their_x, their_y);
            encrypted_path.add(theirs);
        }
        return encrypted_path;
    }

    public static List<BigIntPoint> encrypt_dgk(List<BigIntPoint> input_path,
                                                     DGKPublicKey dgk_public_key) {
        List<BigIntPoint> encrypted_path = new ArrayList<>();
        for (BigIntPoint bigIntPoint : input_path) {
            BigInteger their_x = DGKOperations.encrypt(bigIntPoint.x.longValue(), dgk_public_key);
            BigInteger their_y = DGKOperations.encrypt(bigIntPoint.y.longValue(), dgk_public_key);

            BigIntPoint theirs = new BigIntPoint(their_x, their_y);
            encrypted_path.add(theirs);
        }
        return encrypted_path;
    }
}
