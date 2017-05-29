package com.edi.myrxjava10;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "myTag";
    @BindView(R.id.button1)
    View button1;

    @BindView(R.id.btnMap)
    View btnMap;

    @BindView(R.id.btnFlatMap)
    View btnFlatMap;

    @BindView(R.id.txt1)
    TextView txt1;

    @BindView(R.id.txt2)
    TextView txt2;

    @BindView(R.id.txtCounter)
    TextView txtCounter;

    @BindView(R.id.editText1)
    TextView editText1;

    private Observable<Integer> observable;
    private PublishSubject<Integer> publishSubjectMap;
    private Subscription subscription;
    private PublishSubject<Integer> subjectFlatMap;
    private int counter = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        button1.setOnClickListener(new Button1Listener());

        //  map example ***********************************************************
        Observer<Integer> observerForTxt1 = createObserverForTxt1();

        // PublishSubject extends observabale, next can be invoked after creation
        publishSubjectMap = PublishSubject.create();
        publishSubjectMap.map(new Func1<Integer, Integer>() {
            @Override
            public Integer call(Integer number) {
                return number + 1;
            }
        })
                .observeOn(AndroidSchedulers.mainThread())   // everything above this works on **Main** Thread
                .map(new Func1<Integer, Integer>() {
                    @Override
                    public Integer call(Integer number) {
                        try {
                            Thread.sleep(4000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return number + 7;
                    }
                })
                .observeOn(Schedulers.newThread())    // everything above this works on **new** Thread
//                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(observerForTxt1);

        Observer<Integer> observerForTxt2 = createObserverForTxt2();
//        subscription = publishSubjectMap.subscribe(observerForTxt2);

        btnMap.setOnClickListener(new Button2Listener());


        // flat map example ***********************************************************
        subjectFlatMap = PublishSubject.create();
        subjectFlatMap.flatMap(new Func1<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> call(Integer num) {
                Integer[] lst = new Integer[2];
                lst[0] = num * 2;
                lst[1] = num * 3;
                return Observable.from(lst);
            }
        })
                .subscribe(observerForTxt1)
        ;

        subjectFlatMap.subscribe(observerForTxt2);

        btnFlatMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subjectFlatMap.onNext(Integer.parseInt(editText1.getText().toString()));
            }
        });
    }

    private Observer<Integer> createObserverForTxt1() {
        Observer<Integer> observer = new Observer<Integer>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onComplete 1: All Done!");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError 1: ", e);
            }

            @Override
            public void onNext(Integer num) {
                Log.e(TAG, "onNext 1: " + num);
                txt1.setText(num + "");
                counter++;
                txtCounter.setText(counter + "");
            }
        };

        return observer;
    }

    private Observer<Integer> createObserverForTxt2() {
        Observer<Integer> observer = new Observer<Integer>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onComplete 2: All Done!");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError 2: ");
            }

            @Override
            public void onNext(Integer num) {
                Log.e(TAG, "onNext 2: " + num);
                txt2.setText(num + "");
            }
        };

        return observer;
    }

/*    private Observable<Integer> createObservable() {
        Observable<Integer> observable = Observable.create(new Observable.OnSubscribe<Integer>() {

            @Override
            public void call(Subscriber<? super Integer> subscriber) {

            }
        });

        return observable;
    }*/


    private class Button2Listener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            publishSubjectMap.onNext(Integer.parseInt(editText1.getText().toString()));
        }
    }


    private class Button1Listener implements View.OnClickListener {

        @Override
        public void onClick(View v) {


            Observable<Integer> observable = Observable.create(new Observable.OnSubscribe<Integer>() {
                                                                   @Override
                                                                   public void call(Subscriber<? super Integer> e) {
                                                                       //Use onNext to emit each item in the stream//
                                                                       e.onNext(1);
                                                                       e.onNext(2);
                                                                       e.onNext(3);
                                                                       e.onNext(4);

                                                                       //Once the Observable has emitted all items in the sequence, call onComplete//
                                                                       e.onCompleted();
                                                                   }
                                                               }
            )
                    .map(new Func1<Integer, Integer>() {
                        @Override
                        public Integer call(Integer number) {
                            return number + 1;
                        }
                    });


            Observer<Integer> observer = new Observer<Integer>() {
                @Override
                public void onCompleted() {
                    Log.e(TAG, "onComplete: All Done!");
                }

                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, "onError: ");
                }

                @Override
                public void onNext(Integer num) {
                    Log.e(TAG, "onNext: " + num);
                }
            };

//Create our subscription//
            observable.subscribe(observer);
        }
    }
}
