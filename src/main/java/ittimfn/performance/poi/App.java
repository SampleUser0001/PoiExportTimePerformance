package ittimfn.performance.poi;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import ittimfn.performance.poi.controller.ExcelExportController;
import ittimfn.performance.poi.enums.PropertiesEnum;
import ittimfn.performance.poi.util.Util;
import lombok.Getter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Excelエクスポートの時間計測を行う。
 */
public class App {
    
    private Logger logger = LogManager.getLogger();

    public static final String EXPORT_HOME = Paths.get(System.getProperty("user.dir"), "export").toString();

    public void exec( String[] args ) throws IOException {
        logger.info("start");
        int argsIndex = 0;
        int column = Integer.valueOf(args[argsIndex++]);
        int line = Integer.valueOf(args[argsIndex++]);
        
        Path csvFilePath = Paths.get(EXPORT_HOME, Util.getLocalDateTime(), "memory.csv");

        logger.info("column : {}, line : {}, csv file path : {}", column, line, csvFilePath);

        ExcelExportController controller = new ExcelExportController(column, line);
        controller.export();

        logger.info("finish.");
    }

    public App() {
    }

    public static void main( String[] args ) throws FileNotFoundException, IOException {
        PropertiesEnum.load(Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "application.properties"));
        new App().exec(args);
    }
}
