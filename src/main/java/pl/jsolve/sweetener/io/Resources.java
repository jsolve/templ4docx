package pl.jsolve.sweetener.io;

import java.io.Closeable;
import java.io.IOException;

import pl.jsolve.sweetener.exception.ResourceException;

public final class Resources {

    public static void closeStream(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            throw new ResourceException(e);
        }
    }
}