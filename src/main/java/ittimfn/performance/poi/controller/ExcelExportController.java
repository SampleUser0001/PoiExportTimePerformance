package ittimfn.performance.poi.controller;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fasterxml.jackson.databind.ObjectMapper;

import ittimfn.performance.poi.App;
import ittimfn.performance.poi.model.ExportInfoModel;
import lombok.Data;
import lombok.Getter;
import ittimfn.performance.poi.task.*;
import ittimfn.performance.poi.util.Util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Data
public class ExcelExportController {

    private Logger logger = LogManager.getLogger();

    /** Excelファイルパス。Excelファイル自体はなんでもいいので、実行のたびに上書きされる。 */
    private static final String EXPORT_FILE_PATH = Paths.get(App.EXPORT_HOME, "export.xlsx").toString();

    private Path exportDir;

    private ExportInfoModel exportInfo;

    private Workbook workbook;
    private Sheet sheet;

    private ExecutorService exec;

    public ExcelExportController(int column, int line) {
        this.exportInfo = new ExportInfoModel();
        this.exportInfo.setColumn(column);
        this.exportInfo.setLine(line);

        this.exportDir = Paths.get(App.EXPORT_HOME, Util.getLocalDateTime());

        this.exportInfo.setExportCsvPath(Paths.get(exportDir.toString(), "memory.csv").toString());

        this.workbook = new XSSFWorkbook();
        this.sheet = workbook.createSheet();
    }

    public void export() throws IOException {
        this.startMemoryMonitoring(new LoggingTask(this.exportInfo));

        long time = this.excelExport();
        this.exportInfo.setTime(time);

        this.finishMemoryMonitoring();

        this.exportInfo();
    }

    private long excelExport() throws FileNotFoundException, IOException {
        long start = System.currentTimeMillis();
        try(FileOutputStream fos = new FileOutputStream(EXPORT_FILE_PATH);) {
            for(; this.exportInfo.currentLine < this.exportInfo.getLine() ; this.exportInfo.currentLine++ ){
                Row row = sheet.createRow(this.exportInfo.currentLine);
                for(int j = 0; j < this.exportInfo.getColumn() ; j++ ) {
                    Cell cell = row.createCell(j);

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

        long time = finish - start;
        logger.info("time : {} ms", time);
        return time;
    }

    private void startMemoryMonitoring(Runnable task) {
        this.exec = Executors.newSingleThreadExecutor();
        this.exec.submit(task);
    }
    private void finishMemoryMonitoring() {
        this.exec.shutdown();
    
        try {
            if( !this.exec.awaitTermination(10, TimeUnit.SECONDS) ){
                this.exec.shutdownNow();
                if(!this.exec.awaitTermination(10, TimeUnit.SECONDS)){
                    logger.error("Cannot shutdown.");
                }
            }
        } catch (InterruptedException e){
            this.exec.shutdownNow();
            Thread.currentThread().interrupt();
        }        
    }

    private void exportInfo() throws FileNotFoundException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        try(BufferedWriter writer = Files.newBufferedWriter(
            Paths.get(this.exportDir.toString(), "info.json"),
            Charset.forName("UTF-8"),
            StandardOpenOption.CREATE)) {

            writer.write(mapper.writeValueAsString(this.exportInfo));
        }
    }
}
