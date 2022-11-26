package ittimfn.performance.poi.enums;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public enum PropertiesEnum {
    MEMORY_USEDRATE_THRESHOLD("memory.usedrate.threshold");

    private static Properties properties;
    
    private final String key;

    private PropertiesEnum(String key) {
        this.key = key;
    }
    
    public static void load(Path propertiesPath) throws IOException {
        properties = new Properties();
        properties.load(
            Files.newBufferedReader(propertiesPath, StandardCharsets.UTF_8)
        );
    }
    
    public String getPropertiesValue() {
        return properties.getProperty(this.key);
    }
    
        
}
