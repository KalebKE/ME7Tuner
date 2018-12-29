package ui.viewmodel;

import closedloop.ClosedLoopCorrection;
import closedloop.ClosedLoopCorrectionManager;
import io.reactivex.subjects.PublishSubject;
import parser.me7log.Me7LogParser;
import preferences.ClosedLoopLogFilterPreferences;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ClosedLoopMe7LogViewModel {

    private Me7LogParser me7LogParser;
    private PublishSubject<Map<String, List<Double>>> publishSubject;

    private static ClosedLoopMe7LogViewModel instance;

    public static ClosedLoopMe7LogViewModel getInstance() {
        if(instance == null) {
            instance = new ClosedLoopMe7LogViewModel();
        }

        return instance;
    }

    private ClosedLoopMe7LogViewModel() {
        me7LogParser = new Me7LogParser();
        publishSubject = PublishSubject.create();
    }

    public PublishSubject<Map<String, List<Double>>> getPublishSubject() {
        return publishSubject;
    }

    public void loadFile(File directory) {
        CompletableFuture.runAsync(() -> {
            if(directory.isDirectory()) {
                CompletableFuture.runAsync(() -> {
                    Map<String, List<Double>> me7LogMap = me7LogParser.parseLogFile(Me7LogParser.LogType.CLOSED_LOOP, directory);

                    if (me7LogMap != null) {
                        publishSubject.onNext(me7LogMap);
                    }
                });
            } else {
                publishSubject.onError(new Throwable("Not a directory!"));
            }
        });
    }
}
