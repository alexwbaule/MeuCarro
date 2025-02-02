package com.alexwbaule.flexprofile.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by alex on 04/06/16.
 */
public class CustomGenerateViewId {
    private static final AtomicInteger nextGeneratedId = new AtomicInteger(1);

    public static int customGenerateViewId() {
        for (; ; ) {
            final int result = nextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) {
                newValue = 1; // Roll over to 1, not 0.
            }
            if (nextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }
}