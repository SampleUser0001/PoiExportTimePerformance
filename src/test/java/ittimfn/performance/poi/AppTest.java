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

    @Test
    public void isOverTenth_true() throws NoSuchMethodException, SecurityException  {

        Method method = App.class.getDeclaredMethod("isOneTenth", int.class, int.class);
        method.setAccessible(true);

        try {
            assertThat(method.invoke(this.app, 500, 5000), is(true));
        } catch (Throwable e) {
            logger.error(e);
            fail();
        }
    }

    @Test
    public void isOverTenth_false() throws NoSuchMethodException, SecurityException  {

        Method method = App.class.getDeclaredMethod("isOneTenth", int.class, int.class);
        method.setAccessible(true);

        try {
            assertThat(method.invoke(this.app, 499, 5000), is(false));
            assertThat(method.invoke(this.app, 501, 5000), is(false));
        } catch (Throwable e) {
            logger.error(e);
            fail();
        }
    }
 
    @Test
    public void isMemoryUsedPer90Over_true_equal() throws NoSuchMethodException, SecurityException {
        Method method = App.class.getDeclaredMethod("isMemoryUsedPer90Over");
        method.setAccessible(true);

        Map<MemoryKeyEnum, Double> memory = new HashMap<MemoryKeyEnum, Double>();
        memory.put(MemoryKeyEnum.USED_RATE, 0.9);
        this.app.setMemory(memory);

        try {
            assertThat(method.invoke(this.app), is(true));
        } catch (Throwable e) {
            logger.error(e);
            fail();
        }

    }

    @Test
    public void isMemoryUsedPer90Over_true_over() throws NoSuchMethodException, SecurityException {
        Method method = App.class.getDeclaredMethod("isMemoryUsedPer90Over");
        method.setAccessible(true);

        Map<MemoryKeyEnum, Double> memory = new HashMap<MemoryKeyEnum, Double>();
        memory.put(MemoryKeyEnum.USED_RATE, 0.91);
        this.app.setMemory(memory);

        try {
            assertThat(method.invoke(this.app), is(true));
        } catch (Throwable e) {
            logger.error(e);
            fail();
        }

    }

    @Test
    public void isMemoryUsedPer90Over_false() throws NoSuchMethodException, SecurityException {
        Method method = App.class.getDeclaredMethod("isMemoryUsedPer90Over");
        method.setAccessible(true);

        Map<MemoryKeyEnum, Double> memory = new HashMap<MemoryKeyEnum, Double>();
        memory.put(MemoryKeyEnum.USED_RATE, 0.89);
        this.app.setMemory(memory);

        try {
            assertThat(method.invoke(this.app), is(false));
        } catch (Throwable e) {
            logger.error(e);
            fail();
        }

    }

}
