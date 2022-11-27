package ittimfn.performance.poi;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import ittimfn.performance.poi.enums.PropertiesEnum;
import ittimfn.performance.poi.task.LoggingTask;
import lombok.Getter;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Excelエクスポートの時間計測を行う。
 */
public class App {
    
    private Logger logger = LogManager.getLogger();

    public static final String EXPORT_FILE_PATH = Paths.get(System.getProperty("user.dir"), "export", "export.xlsx").toString();

    private ExecutorService exec;

    @Getter
    private static int currentLine = 0;

    public void exec( String[] args) throws IOException {
        logger.info("start");
        int argsIndex = 0;
        int column = Integer.valueOf(args[argsIndex++]);
        int line = Integer.valueOf(args[argsIndex++]);

        logger.info("column : {}, line : {}", column, line);

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();

        this.startMemoryMonitoring(new LoggingTask(line));

        long start = System.currentTimeMillis();
        try(FileOutputStream fos = new FileOutputStream(EXPORT_FILE_PATH);) {
            for(; currentLine < line ; currentLine++ ){
                XSSFRow row = sheet.createRow(currentLine);
                for(int j = 0; j < column ; j++ ) {
                    XSSFCell cell = row.createCell(j);

                    // 適当な値を生成する。
                    cell.setCellValue(RandomStringUtils.randomAlphabetic(20));
                }

            }

            workbook.write(fos);
            fos.close();
        }
        workbook.close();


        long finish = System.currentTimeMillis();
        logger.info("Excel export finish.");
        logger.info("time : {} ms", finish - start);

        this.finishMemoryMonitoring();

        logger.info("finish.");
    }

    private void startMemoryMonitoring(Runnable task) {
        this.exec = Executors.newSingleThreadExecutor();
        this.exec.submit(task);
    }

    private void finishMemoryMonitoring() {
        this.exec.shutdown();
    
        try {
            if( !this.exec.awaitTermination(60, TimeUnit.SECONDS) ){
                this.exec.shutdownNow();
                if(!this.exec.awaitTermination(60, TimeUnit.SECONDS)){
                    logger.error("Cannot shutdown.");
                }
            }
        } catch (InterruptedException e){
            this.exec.shutdownNow();
            Thread.currentThread().interrupt();
        }        
    }

    public App() {
    }

    public static void main( String[] args ) throws FileNotFoundException, IOException {
        PropertiesEnum.load(Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "application.properties"));
        new App().exec(args);
    }
}
