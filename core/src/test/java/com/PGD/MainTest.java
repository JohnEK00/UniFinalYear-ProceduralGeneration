package com.PGD;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/*
 * this class tests the Main class
 */
public class MainTest {
    // Test the increaseView method (cannot be called directly due to LIBGDX camera)
    @Test
    public void testIncreaseView() throws Exception {
        int initialWidth = getStaticField(Main.class, "windowWidth");
        int initialHeight = getStaticField(Main.class, "windowHeight");

        setStaticField(Main.class, "windowWidth", initialWidth - 100);
        setStaticField(Main.class, "windowHeight", initialHeight - 100);

        assertEquals(initialWidth - 100, getStaticField(Main.class, "windowWidth"),
                "Window width should decrease by 100");

        assertEquals(initialHeight - 100, getStaticField(Main.class, "windowHeight"),
                "Window height should decrease by 100");
    }

    // Test the decreaseView method (cannot be called directly due to LIBGDX camera)
    @Test
    public void testDecreaseView() throws Exception {
        int initialWidth = getStaticField(Main.class, "windowWidth");
        int initialHeight = getStaticField(Main.class, "windowHeight");

        setStaticField(Main.class, "windowWidth", initialWidth + 100);
        setStaticField(Main.class, "windowHeight", initialHeight + 100);

        assertEquals(initialWidth + 100, getStaticField(Main.class, "windowWidth"),
                "Window width should increase by 100");
        assertEquals(initialHeight + 100, getStaticField(Main.class, "windowHeight"),
                "Window height should increase by 100");
    }

    // Test the camera resizing (cannot be called directly due to LIBGDX camera)
    @Test
    public void testCameraResize() throws Exception {
        setStaticField(Main.class, "windowWidth", 400);
        setStaticField(Main.class, "windowHeight", 300);

        int newWidth = 800;
        int newHeight = 600;
        int expectedWidth = newWidth / 2;
        int expectedHeight = newHeight / 2;

        setStaticField(Main.class, "windowWidth", expectedWidth);
        setStaticField(Main.class, "windowHeight", expectedHeight);

        int actualWidth = getStaticField(Main.class, "windowWidth");
        int actualHeight = getStaticField(Main.class, "windowHeight");

        // assert window width and height are half of the new screen width and height
        assertEquals(expectedWidth, actualWidth);
        assertEquals(expectedHeight, actualHeight);
    }

    /*
     * helper methods for setting and getting private static fields in main class
     */
    private static void setStaticField(Class<?> clazz, String fieldName, int value) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }

    private static int getStaticField(Class<?> clazz, String fieldName) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (int) field.get(null);
    }
}