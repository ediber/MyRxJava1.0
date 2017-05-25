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
import rx.functions.Func1;
import rx.subjects.PublishSubject;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "myTag";
    @BindView(R.id.button1)
    View button1;

    @BindView(R.id.button2)
    View button2;

    @BindView(R.id.txt1)
    TextView txt1;

    @BindView(R.id.txt2)
    TextView txt2;

    @BindView(R.id.editText1)
    TextView editText1;

    private Observable<Integer> observable;
    PublishSubject<Integer> publishSubject;
    private Subscription subscription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        button1.setOnClickListener(new Button1Listener());

//        observable = createObservable();
//        observable.subscribe()

        Observer<Integer> observerForTxt1 = createObserverForTxt1();

        // PublishSubject extends observabale, next can be invoked after creation
        publishSubject = PublishSubject.create();
        publishSubject.map((new Func1<Integer, Integer>() {
            @Override
            public Integer call(Integer number) {
                return number + 1;
            }
        })).subscribe(observerForTxt1);


        Observer<Integer> observerForTxt2 = createObserverForTxt2();
        subscription = publishSubject.subscribe(observerForTxt2);


        button2.setOnClickListener(new Button2Listener());
    }

    private Observer<Integer> createObserverForTxt1() {
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
                txt1.setText(num + "");
            }
        };

        return observer;
    }

    private Observer<Integer> createObserverForTxt2() {
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
            publishSubject.onNext(Integer.parseInt(editText1.getText().toString()));
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
