package ui.viewmodel.primaryfueling;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import model.airflow.AirflowEstimation;
import model.primaryfueling.PrimaryFuelingCorrection;
import model.primaryfueling.PrimaryFuelingCorrectionManager;
import preferences.primaryfueling.PrimaryFuelingPreferences;
import ui.viewmodel.MlhfmViewModel;
import ui.viewmodel.openloopfueling.OpenLoopFuelingAirflowViewModel;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class PrimaryFuelingCorrectionViewModel {

    private static PrimaryFuelingCorrectionViewModel instance;

    private Map<String, List<Double>> mlhfmMap;
    private AirflowEstimation airflowEstimation;

    private PublishSubject<PrimaryFuelingCorrection> publishSubject;

    public static PrimaryFuelingCorrectionViewModel getInstance() {
        if (instance == null) {
            instance = new PrimaryFuelingCorrectionViewModel();
        }

        return instance;
    }

    private PrimaryFuelingCorrectionViewModel() {
        publishSubject = PublishSubject.create();

        OpenLoopFuelingAirflowViewModel.getInstance().getPublishSubject().subscribe(new Observer<AirflowEstimation>() {
            @Override
            public void onNext(AirflowEstimation airflowEstimation) {
                PrimaryFuelingCorrectionViewModel.this.airflowEstimation = airflowEstimation;
                generateCorrection();
            }

            @Override
            public void onSubscribe(Disposable disposable) {
            }

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
                PrimaryFuelingCorrectionViewModel.this.mlhfmMap = mlhfmMap;
                generateCorrection();
            }

            @Override
            public void onSubscribe(Disposable disposable) {
            }

            @Override
            public void onError(Throwable throwable) {
            }

            @Override
            public void onComplete() {
            }
        });
    }

    private void generateCorrection() {
        if (airflowEstimation != null && mlhfmMap != null) {
            CompletableFuture.runAsync(() -> {
                PrimaryFuelingCorrectionManager primaryFuelingCorrectionManager = new PrimaryFuelingCorrectionManager();
                primaryFuelingCorrectionManager.correct(PrimaryFuelingPreferences.getKrktePreference(), mlhfmMap, airflowEstimation);
                PrimaryFuelingCorrection primaryFuelingCorrection = primaryFuelingCorrectionManager.getPrimaryFuelingCorrection();

                if (primaryFuelingCorrection != null) {
                    publishSubject.onNext(primaryFuelingCorrection);
                }
            });
        }
    }

    public PublishSubject<PrimaryFuelingCorrection> getPublishSubject() {
        return publishSubject;
    }
}
