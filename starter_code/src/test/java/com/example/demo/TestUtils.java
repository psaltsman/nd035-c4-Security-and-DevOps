package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class TestUtils {

    public static final Logger log = LoggerFactory.getLogger(TestUtils.class);

    public static void injectObjects(Object target, String fieldName, Object toInject) {

        boolean wasPrivate = false;

        try {
            Field f = target.getClass().getDeclaredField(fieldName);

            if (!f.isAccessible()) {

                f.setAccessible(true);
                wasPrivate = true;
            }

            f.set(target, toInject);

            if (wasPrivate) {

                f.setAccessible(false);
            }

         } catch (NoSuchFieldException e) {

            log.error(e.getClass().getName() + ": " + e.getMessage());

            e.printStackTrace();

        } catch (IllegalAccessException e) {

            log.error(e.getClass().getName() + ": " + e.getMessage());

            e.printStackTrace();
        }
    }
}
