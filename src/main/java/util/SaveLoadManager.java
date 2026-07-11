package com.silicontycoon.util;

import com.silicontycoon.exception.CorruptedSaveException;
import com.silicontycoon.model.GameState;

import java.io.*;
import java.util.function.Consumer;

/**
 * Handles persisting and restoring a {@link GameState} using plain Java
 * object serialization. Save/Load run on a background thread so the
 * JavaFX Application Thread is never blocked while touching disk.
 */
public final class SaveLoadManager {

    private SaveLoadManager() {}

    /** Synchronous save; call only from a background thread. */
    public static void save(GameState state, File file) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
            out.writeObject(state);
        }
    }

    /** Synchronous load; call only from a background thread. */
    public static GameState load(File file) throws CorruptedSaveException {
        if (!file.exists() || file.length() == 0) {
            throw new CorruptedSaveException("فایل ذخیره یافت نشد یا خالی است: " + file.getName());
        }
        try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)))) {
            Object obj = in.readObject();
            if (!(obj instanceof GameState)) {
                throw new CorruptedSaveException("محتوای فایل با فرمت بازی سازگار نیست.");
            }
            return (GameState) obj;
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            throw new CorruptedSaveException("فایل ذخیره خراب یا ناقص است.", e);
        }
    }

    /** Asynchronous save on a dedicated thread; callbacks fire with the result. */
    public static void saveAsync(GameState state, File file, Runnable onSuccess, Consumer<Exception> onError) {
        Thread worker = new Thread(() -> {
            try {
                save(state, file);
                if (onSuccess != null) onSuccess.run();
            } catch (Exception e) {
                if (onError != null) onError.accept(e);
            }
        }, "save-thread");
        worker.setDaemon(true);
        worker.start();
    }

    /** Asynchronous load on a dedicated thread; callbacks fire with the result. */
    public static void loadAsync(File file, Consumer<GameState> onSuccess, Consumer<Exception> onError) {
        Thread worker = new Thread(() -> {
            try {
                GameState loaded = load(file);
                if (onSuccess != null) onSuccess.accept(loaded);
            } catch (Exception e) {
                if (onError != null) onError.accept(e);
            }
        }, "load-thread");
        worker.setDaemon(true);
        worker.start();
    }
}
