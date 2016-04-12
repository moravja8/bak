package services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by cloudera on 3/28/16.
 */
public class ShellCommandsExecutorService {

    Logger log = LoggerFactory.getLogger(ShellCommandsExecutorService.class);

    protected ShellCommandsExecutorService() {
    }

    public ArrayList<String> executeCommand(String command) {

        log.info("Executing command: "+ command +"");

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            ArrayList<String> output = new ArrayList<String>();

            String line;
            while ((line = reader.readLine())!= null) {
                log.trace(line);
                output.add(line);
            }
            log.info("Command execution terminated "+ command);
            return output;

        } catch (Exception e) {
            log.error("Error occured during command execution", e);
            return null;
        }
    }

    @Deprecated // TODO: 4/11/16 smazat po zalohovani na git
    public String callProcess(String... command) {
        StringBuilder assembleCommand = new StringBuilder();
        for (String commandPart: command) {
            assembleCommand.append(commandPart);
            assembleCommand.append(" ");
        }

        log.info("Executing command: "+ assembleCommand.toString());

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(new File(System.getProperty("user.home")));

        Process process = null;
        try {
            process = processBuilder.start();
        } catch (IOException e) {
            log.error("An error occurred while creating process.", e);
        }
        String output = ServiceFactory.getFileSystemService().readInputStream(process.getInputStream());
        String error = ServiceFactory.getFileSystemService().readInputStream(process.getErrorStream());
        log.info("Proccess ended with message: "+ error);
        return output;
    }
    
}
