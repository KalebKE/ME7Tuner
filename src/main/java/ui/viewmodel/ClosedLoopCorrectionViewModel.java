package ui.viewmodel;

import closedloop.ClosedLoopCorrection;
import closedloop.ClosedLoopCorrectionManager;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import preferences.ClosedLoopLogFilterPreferences;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ClosedLoopCorrectionViewModel {

    private static ClosedLoopCorrectionViewModel instance;

    private Map<String, List<Double>> mlhfmMap;
    private Map<String, List<Double>> me7LogMap;

    private PublishSubject<ClosedLoopCorrection> publishSubject;

    public static ClosedLoopCorrectionViewModel getInstance() {
        if(instance == null) {
            instance = new ClosedLoopCorrectionViewModel();
        }

        return instance;
    }

    private ClosedLoopCorrectionViewModel() {
        publishSubject = PublishSubject.create();

        ClosedLoopMe7LogViewModel closedLoopViewModel = ClosedLoopMe7LogViewModel.getInstance();
        closedLoopViewModel.getPublishSubject().subscribe(new Observer<Map<String, List<Double>>>() {
            @Override
            public void onNext(Map<String, List<Double>> me7LogMap) {
                ClosedLoopCorrectionViewModel.this.me7LogMap = me7LogMap;
            }

            @Override
            public void onSubscribe(Disposable disposable) {}

            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onComplete() {}
        });

        MlhfmViewModel mlhfmViewModel = MlhfmViewModel.getInstance();
        mlhfmViewModel.getPublishSubject().subscribe(new Observer<Map<String, List<Double>>>() {
            @Override
            public void onNext(Map<String, List<Double>> mlhfmMap) {
                ClosedLoopCorrectionViewModel.this.mlhfmMap = mlhfmMap;
            }

            @Override
            public void onSubscribe(Disposable disposable) {}

            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onComplete() {}
        });
    }

    public void generateCorrection() {
        if(me7LogMap != null && mlhfmMap != null) {
            CompletableFuture.runAsync(() -> {
                ClosedLoopCorrectionManager closedLoopCorrectionManager = new ClosedLoopCorrectionManager(ClosedLoopLogFilterPreferences.getMinThrottleAnglePreference(), ClosedLoopLogFilterPreferences.getMinRpmPreference(), ClosedLoopLogFilterPreferences.getMaxStdDevPreference());
                closedLoopCorrectionManager.correct(me7LogMap, mlhfmMap);
                ClosedLoopCorrection closedLoopCorrection = closedLoopCorrectionManager.getClosedLoopCorrection();

                if (closedLoopCorrection != null) {
                    publishSubject.onNext(closedLoopCorrection);
                }
            });
        }
    }

    public PublishSubject<ClosedLoopCorrection> getPublishSubject() {
        return publishSubject;
    }
}
