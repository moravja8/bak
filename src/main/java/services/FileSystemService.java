package services;

import knimeEntities.KnimeWorkflowManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by cloudera on 4/6/16.
 */
public class FileSystemService {
    private static Logger log = LoggerFactory.getLogger(FileSystemService.class);

    protected FileSystemService() {
    }

    public File createFolder(String folderAbsolutePath){
        File folder = new File(folderAbsolutePath);

        //pokud neexistuje parent složka, rekurzivně se vytvoří
        if(!folder.getParentFile().exists()){
            createFolder(folder.getParentFile().getAbsolutePath());
        }

        //pokusí se 5krát vytvořit složku
        for (int archiveId = 1; archiveId <= 5; archiveId++) {
            if(!folder.exists()){
                folder.mkdir();
                break;
            }else if(folder.exists() && !folder.isDirectory()){
                folder = new File (folderAbsolutePath + archiveId);
            }
        }

        //pokud i tak selže, vrátí null
        if(!(folder.exists() && folder.isDirectory())){
            log.error("Archiv folder could not be created, function returns null");
            return null;
        }

        return folder;
    }

    public void moveFileSilently(File sourceFile, File resultFile) throws IOException {
        try{
            if(sourceFile.renameTo(resultFile)){
                resultFile.createNewFile();
                log.info("File " + sourceFile.getName() + " was moved to " + resultFile.getParentFile().getAbsolutePath());
            }else{
                throw new IOException();
            }
        }catch(IOException e){
            log.error("File " + sourceFile.getName() + " could not be moved to " + resultFile.getParentFile().getAbsolutePath(), e);
            throw new IOException();
        }
    }



    public String readInputStream(InputStream inputStream){
        StringBuilder output = new StringBuilder();
        try {
            int content;
            while ((content = inputStream.read()) != -1) {
                output.append((char) content);
            }
        } catch (IOException e) {
            log.error("Content could not been read.", e);
        }

        return output.toString();
    }
}
