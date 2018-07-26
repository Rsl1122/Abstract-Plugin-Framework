package com.djrapitops.plugin.logging.error;

import com.djrapitops.plugin.logging.FolderTimeStampFileLogger;
import com.djrapitops.plugin.logging.L;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FolderTimeStampErrorFileLogger extends FolderTimeStampFileLogger implements ErrorHandler {

    public FolderTimeStampErrorFileLogger(File logFolder, ErrorHandler errorHandler) {
        super("Errors", logFolder, () -> errorHandler);
    }

    @Override
    public void log(L level, Class caughtBy, Throwable throwable) {
        log(caughtBy.getName() + " caught " + throwable.getClass().getSimpleName());

        List<String> stackTrace = getStackTrace(throwable);
        for (String line : stackTrace) {
            log(line);
        }
    }

    private List<String> getStackTrace(Throwable e) {
        List<String> trace = new ArrayList<>();
        trace.add(e.toString());
        for (StackTraceElement element : e.getStackTrace()) {
            trace.add("   " + element);
        }
        Throwable cause = e.getCause();
        if (cause != null) {
            trace.add("Caused by:");
            trace.addAll(getStackTrace(cause));
        }
        return trace;
    }
}