package services;

/**
 * not thread safe
 * Created by cloudera on 3/29/16.
 */
public class ServiceFactory {
    private static PropertiesLoaderService propertiesLoaderService = null;
    private static ShellCommandsExecutorService shellCommandsExecutorService = null;
    private static XMLXpathService xmlXpathService = null;
    private static FileSystemService fileSystemService = null;
    private static KnimeLogSettingsService knimeLogSettingsService = null;

    public static PropertiesLoaderService getPropertiesLoaderService() {
        if(propertiesLoaderService == null){
            propertiesLoaderService = new PropertiesLoaderService();
        }
        return propertiesLoaderService;
    }

    public static ShellCommandsExecutorService getShellCommandsExecutorService() {
        if(shellCommandsExecutorService == null){
            shellCommandsExecutorService = new ShellCommandsExecutorService();
        }
        return shellCommandsExecutorService;
    }

    public static XMLXpathService getXmlXpathService() {
        if(xmlXpathService == null){
            xmlXpathService = new XMLXpathService();
        }
        return xmlXpathService;
    }

    public static FileSystemService getFileSystemService(){
        if(fileSystemService == null){
            fileSystemService = new FileSystemService();
        }
        return fileSystemService;
    }

    public static KnimeLogSettingsService getKnimeLogSettingsService(){
        if(knimeLogSettingsService == null){
            knimeLogSettingsService = new KnimeLogSettingsService();
        }
        return knimeLogSettingsService;
    }
}
