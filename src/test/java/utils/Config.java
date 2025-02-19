package utils;

import java.io.IOException;
import java.io.FileInputStream;
import java.util.Properties;

public class Config {

    private static final String CONFIG_PATH="src/test/resources/config.properties";

    public static String getToken(){
        Properties properties = new Properties();
        try (FileInputStream file = new FileInputStream(CONFIG_PATH)) {
            properties.load(file);
            return "Bearer "+properties.getProperty("GOREST_TOKEN");
        }catch (IOException e){
            throw new RuntimeException("Error al configurar el token");
        }

    }
}
