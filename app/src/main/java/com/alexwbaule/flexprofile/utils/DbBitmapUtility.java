package com.alexwbaule.flexprofile.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.alexwbaule.flexprofile.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by alex on 28/02/17.
 */

public class DbBitmapUtility {

    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(Context context, byte[] image) {
        if (image == null)
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_header_photo);

        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public static void savebitmap(Context context, Bitmap bmp, long fname) throws IOException {
        if (bmp != null) {
            String filename = String.valueOf(fname);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 60, bytes);
            File f = new File(context.getFilesDir(), filename);
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            fo.close();
        }
    }

    public static Bitmap readbitmap(Context context, String name) {
        File f = new File(context.getFilesDir(), name);
        FileInputStream input = null;
        try {
            input = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_header_photo);
        }
        return BitmapFactory.decodeStream(input);
    }
}