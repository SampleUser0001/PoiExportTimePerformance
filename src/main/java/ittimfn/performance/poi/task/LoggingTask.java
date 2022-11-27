package ittimfn.performance.poi.task;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ittimfn.performance.poi.App;
import ittimfn.performance.poi.enums.MemoryKeyEnum;
import ittimfn.performance.poi.enums.PropertiesEnum;
import ittimfn.performance.poi.model.ExportInfoModel;

public class LoggingTask implements Runnable {

    private Logger logger = LogManager.getLogger();

    private static final String CSV_HEADER = "Line,Total(MB),Used(MB),Used rate(%)";

    private Map<MemoryKeyEnum, Double> memory;

    private ExportInfoModel info;

    public LoggingTask(ExportInfoModel info) throws IOException {
        this.info = info;

        this.memory = new HashMap<MemoryKeyEnum, Double>();
        this.mkdir();
    }

    @Override
    public void run() {
        try(BufferedWriter writer = Files.newBufferedWriter(
            Paths.get(this.info.getExportCsvPath()),
            Charset.forName("UTF-8"),
            StandardOpenOption.CREATE)) {

            writer.write(CSV_HEADER);
            writer.write(System.lineSeparator());
            while(this.info.getCurrentLine() < this.info.getLine()) {
                this.setMemory();
                writer.write(this.memoryToString(this.info.getCurrentLine()));
                writer.write(System.lineSeparator());

                logger.debug(this.memoryToString(this.info.getCurrentLine()));

                Thread.sleep(Long.valueOf(PropertiesEnum.MEMORY_EXPORT_INTERVAL.getPropertiesValue()));
            }

        } catch (Exception e) {
            logger.error(e);
        }
    }

    private void setMemory() {
        double totalMemory = Runtime.getRuntime().totalMemory();
        double usedMemory = totalMemory - Runtime.getRuntime().freeMemory();
        double usedRate = (double)usedMemory / (double)totalMemory;

        this.memory.put(MemoryKeyEnum.TOTAL_MEMORY, totalMemory);
        this.memory.put(MemoryKeyEnum.USED_MEMORY, usedMemory);
        this.memory.put(MemoryKeyEnum.USED_RATE, usedRate);
    }

    private String memoryToString(int line) {
        return String.format("%d,%d,%d,%f",
            line,
            (int)(this.memory.get(MemoryKeyEnum.TOTAL_MEMORY) / ( 1024 * 1024 )),
            (int)(this.memory.get(MemoryKeyEnum.USED_MEMORY) / ( 1024 * 1024 )),
            this.memory.get(MemoryKeyEnum.USED_RATE) * 100);
    }

    private void mkdir() throws IOException {
        Files.createDirectories(Paths.get(this.info.getExportCsvPath()).getParent());
    }
}
