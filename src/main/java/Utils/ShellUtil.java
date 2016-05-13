package Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by cloudera on 3/28/16.
 */
public class ShellUtil {
    private static Logger log = LoggerFactory.getLogger(ShellUtil.class);

    public static ArrayList<String> executeCommand(String command) {

        log.info("Executing command: "+ command +"");

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();

            /* FIXME
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            ArrayList<String> output = new ArrayList<String>();

            String line;
            while ((line = reader.readLine())!= null) {
                log.trace(line);
                output.add(line);
            }

            log.info("Command execution terminated "+ command);
            return output; */
            return null;

        } catch (Exception e) {
            log.error("Error occured during command execution", e);
            return null;
        }
    }
}
