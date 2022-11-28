package ittimfn.performance.poi.controller;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import java.lang.RuntimeException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ColumnIndexController {

    private Logger logger = LogManager.getLogger();

    private List<Integer> usedColumnIndex = new ArrayList<Integer>();
    private Random random = new Random();

    private int columnLength;

    public ColumnIndexController(int columnLength) {
        this.columnLength = columnLength;
    }

    public int get() throws RuntimeException {
        if(this.usedColumnIndex.size() == columnLength) {
            throw new RuntimeException("Index is all used.");
        }
        
        int columnIndex;
        do {
            columnIndex = random.nextInt(this.columnLength);
        } while(usedColumnIndex.contains(columnIndex));
        usedColumnIndex.add(columnIndex);

        logger.debug("column index : {}", columnIndex);

        return columnIndex;
    }
    
    public void clear() {
        logger.debug("column index cleared.");
        this.usedColumnIndex.clear();
    }

}
