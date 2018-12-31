package ui.viewmodel.openloop;

import closedloop.ClosedLoopCorrection;
import closedloop.ClosedLoopCorrectionManager;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import openloop.OpenLoopCorrection;
import openloop.OpenLoopCorrectionManager;
import preferences.closedloop.ClosedLoopLogFilterPreferences;
import preferences.openloop.OpenLoopLogFilterPreferences;
import ui.viewmodel.MlhfmViewModel;
import ui.viewmodel.closedloop.ClosedLoopMe7LogViewModel;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class OpenLoopCorrectionViewModel {

    private static OpenLoopCorrectionViewModel instance;

    private Map<String, List<Double>> mlhfmMap;
    private Map<String, List<Double>> me7LogMap;
    private Map<String, List<Double>> afrLogMap;

    private PublishSubject<OpenLoopCorrection> publishSubject;

    public static OpenLoopCorrectionViewModel getInstance() {
        if(instance == null) {
            instance = new OpenLoopCorrectionViewModel();
        }

        return instance;
    }

    private OpenLoopCorrectionViewModel() {
        publishSubject = PublishSubject.create();

        OpenLoopMe7LogViewModel.getInstance().getPublishSubject().subscribe(new Observer<Map<String, List<Double>>>() {
            @Override
            public void onNext(Map<String, List<Double>> me7LogMap) {
                OpenLoopCorrectionViewModel.this.me7LogMap = me7LogMap;
            }

            @Override
            public void onSubscribe(Disposable disposable) {}

            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onComplete() {}
        });

        OpenLoopAfrLogViewModel.getInstance().getPublishSubject().subscribe(new Observer<Map<String, List<Double>>>() {
            @Override
            public void onNext(Map<String, List<Double>> afrLogMap) {
                OpenLoopCorrectionViewModel.this.afrLogMap = afrLogMap;
            }

            @Override
            public void onSubscribe(Disposable disposable) {}

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {

            }
        });

        MlhfmViewModel mlhfmViewModel = MlhfmViewModel.getInstance();
        mlhfmViewModel.getMlhfmPublishSubject().subscribe(new Observer<Map<String, List<Double>>>() {
            @Override
            public void onNext(Map<String, List<Double>> mlhfmMap) {
                OpenLoopCorrectionViewModel.this.mlhfmMap = mlhfmMap;
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
        if(me7LogMap != null && afrLogMap != null && mlhfmMap != null) {
            CompletableFuture.runAsync(() -> {
                OpenLoopCorrectionManager openLoopCorrectionManager = new OpenLoopCorrectionManager(OpenLoopLogFilterPreferences.getMinThrottleAnglePreference(), OpenLoopLogFilterPreferences.getMinRpmPreference(), OpenLoopLogFilterPreferences.getMinMe7PointsPreference(), OpenLoopLogFilterPreferences.getMinAfrPointsPreference(), OpenLoopLogFilterPreferences.getMaxAfrPreference());
                openLoopCorrectionManager.correct(me7LogMap, afrLogMap, mlhfmMap);
                OpenLoopCorrection openLoopCorrection = openLoopCorrectionManager.getOpenLoopCorrection();

                if(openLoopCorrection != null) {
                    publishSubject.onNext(openLoopCorrection);
                }
            });
        }
    }

    public PublishSubject<OpenLoopCorrection> getPublishSubject() {
        return publishSubject;
    }
}
