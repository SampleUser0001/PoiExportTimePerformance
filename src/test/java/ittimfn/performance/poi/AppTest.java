package ittimfn.performance.poi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import ittimfn.performance.poi.enums.MemoryKeyEnum;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

// @RunWith(PowerMockRunner.class)
public class AppTest {

    private Logger logger = LogManager.getLogger();

    private App app;

    @Before
    public void setUp() {
        this.app = new App();
    }


}
