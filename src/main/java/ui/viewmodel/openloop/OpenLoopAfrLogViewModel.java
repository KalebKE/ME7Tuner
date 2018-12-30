package ui.viewmodel.openloop;

import io.reactivex.subjects.PublishSubject;
import parser.afrLog.AfrLogParser;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class OpenLoopAfrLogViewModel {

    private AfrLogParser afrLogParser;
    private PublishSubject<Map<String, List<Double>>> publishSubject;

    private static OpenLoopAfrLogViewModel instance;

    public static OpenLoopAfrLogViewModel getInstance() {
        if (instance == null) {
            instance = new OpenLoopAfrLogViewModel();
        }

        return instance;
    }

    private OpenLoopAfrLogViewModel() {
        afrLogParser = new AfrLogParser();
        publishSubject = PublishSubject.create();
    }

    public PublishSubject<Map<String, List<Double>>> getPublishSubject() {
        return publishSubject;
    }

    public void loadFile(File file) {
        CompletableFuture.runAsync(() -> {
            CompletableFuture.runAsync(() -> {
                Map<String, List<Double>> afrLogMap = afrLogParser.parse(file);

                if (afrLogMap != null) {
                    publishSubject.onNext(afrLogMap);
                }
            });
        });
    }
}
