package com.edi.myrxjava10;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Func1;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "myTag";
    @BindView(R.id.button)
    View button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        button.setOnClickListener(new View.OnClickListener() {
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
                        .map(new Func1<Integer, Integer>(){
                            @Override
                            public Integer call(Integer number) {
                                return number + 1;
                            }
                        })
                        ;


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
                } ;

//Create our subscription//
                observable.subscribe(observer);
            }
        });
    }
}
