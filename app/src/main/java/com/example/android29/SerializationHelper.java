package com.example.android29;

import android.content.Context;
import java.io.*;
import java.nio.file.Files;

public class SerializationHelper {

    public static boolean fileExists(Context context, String fileName) {
        File file = new File(getStorageDirectory(context), fileName);
        return file.exists();
    }

    public static void serialize(Context context, Object object, String fileName) {
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(new File(getStorageDirectory(context), fileName).toPath()))) {
            oos.writeObject(object);
        } catch (IOException e) {
            System.err.println("Error occurred during serialization: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Object deserialize(Context context, String fileName) {
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(new File(getStorageDirectory(context), fileName).toPath()))) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static File getStorageDirectory(Context context) {
        // Use either internal or external storage directory based on your requirements
        // Example using internal storage:
        return context.getFilesDir();

        // Example using external storage:
        // return context.getExternalFilesDir(null);
    }
}
