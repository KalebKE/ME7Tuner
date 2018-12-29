package ui.viewmodel;

import io.reactivex.subjects.PublishSubject;
import parser.mlhfm.MlhfmParser;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MlhfmViewModel {

    private MlhfmParser mlhfmParser;
    private PublishSubject<Map<String, List<Double>>> publishSubject;

    private static MlhfmViewModel instance;

    public static MlhfmViewModel getInstance() {
        if(instance == null) {
            instance = new MlhfmViewModel();
        }

        return instance;
    }

    private MlhfmViewModel() {
        mlhfmParser = new MlhfmParser();
        publishSubject = PublishSubject.create();
    }

    public PublishSubject<Map<String, List<Double>>> getPublishSubject() {
        return publishSubject;
    }

    public void loadFile(File file) {
        CompletableFuture.runAsync(() -> {
            Map<String, List<Double>> mlhfmMap = mlhfmParser.parse(file);

            if(mlhfmMap != null) {
                publishSubject.onNext(mlhfmMap);
            }
        });
    }
}
