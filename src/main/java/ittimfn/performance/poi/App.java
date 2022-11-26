package ittimfn.performance.poi;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import ittimfn.performance.poi.enums.MemoryKeyEnum;
import ittimfn.performance.poi.enums.PropertiesEnum;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Excelエクスポートの時間計測を行う。
 */
public class App {
    
    private Logger logger = LogManager.getLogger();

    public static final String EXPORT_FILE_PATH = Paths.get(System.getProperty("user.dir"), "export", "export.xlsx").toString();

    private double memoryUsedRateThreshold = 100;

    private Map<MemoryKeyEnum, Double> memory;

    public void exec( String[] args) throws IOException {
        logger.info("start");
        int argsIndex = 0;
        int column = Integer.valueOf(args[argsIndex++]);
        int line = Integer.valueOf(args[argsIndex++]);

        logger.info("column : {}, line : {}, memory used rate threshold : {}", column, line, memoryUsedRateThreshold);

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();

        long start = System.currentTimeMillis();
        try(FileOutputStream fos = new FileOutputStream(EXPORT_FILE_PATH);) {
            for(int i = 0 ; i < line ; i++ ){
                XSSFRow row = sheet.createRow(i);
                for(int j = 0; j < column ; j++ ) {
                    XSSFCell cell = row.createCell(j);

                    // 適当な値を生成する。
                    cell.setCellValue(RandomStringUtils.randomAlphabetic(20));
                }

                this.setMemory();
                if(this.isOneTenth(i, line) || this.isMemoryUsedPer90Over()) {
                    this.loggingMemory(i);
                }
            }

            workbook.write(fos);
            fos.close();
        }
        workbook.close();

        long finish = System.currentTimeMillis();
        logger.info("time : {} ms", finish - start);
        logger.info("finish.");
    }

    private void loggingMemory(int line) {
        logger.debug(String.format("line : %d , Mem : total %d MB , used : %d MB, rate : %f %% ",
            line,
            (int)(this.memory.get(MemoryKeyEnum.TOTAL_MEMORY) / ( 1024 * 1024 )),
            (int)(this.memory.get(MemoryKeyEnum.USED_MEMORY) / ( 1024 * 1024 )),
            this.memory.get(MemoryKeyEnum.USED_RATE) * 100));
    }

    private void setMemory() {
        double totalMemory = Runtime.getRuntime().totalMemory();
        double usedMemory = totalMemory - Runtime.getRuntime().freeMemory();
        double usedRate = (double)usedMemory / (double)totalMemory;

        this.memory.put(MemoryKeyEnum.TOTAL_MEMORY, totalMemory);
        this.memory.put(MemoryKeyEnum.USED_MEMORY, usedMemory);
        this.memory.put(MemoryKeyEnum.USED_RATE, usedRate);
    }

    private boolean isOneTenth(int i , int line) {
        return i%(line/10) == 0;
    }

    private boolean isMemoryUsedPer90Over() {
        return this.memory.get(MemoryKeyEnum.USED_RATE) >= this.memoryUsedRateThreshold;
    }

    public App() {
        this.memory = new HashMap<MemoryKeyEnum, Double>();
        this.memoryUsedRateThreshold = Double.valueOf(PropertiesEnum.MEMORY_USEDRATE_THRESHOLD.getPropertiesValue());
    }

    public static void main( String[] args ) throws FileNotFoundException, IOException {
        PropertiesEnum.load(Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "application.properties"));
        new App().exec(args);
    }

    void setMemory(Map<MemoryKeyEnum, Double> memory) {
        this.memory = memory;
    }
}
