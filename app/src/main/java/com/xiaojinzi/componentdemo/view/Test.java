package com.xiaojinzi.componentdemo.view;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class Test {

    public static void main(String[] args) {

        List<Integer> list = Observable.range(1, 10)
                .flatMapSingle(new Function<Integer, SingleSource<Integer>>() {
                    @Override
                    public SingleSource<Integer> apply(Integer integer) throws Exception {
                        return Single.just(integer)
                                .subscribeOn(Schedulers.newThread());
                    }
                })
                .observeOn(Schedulers.io())
                .toList()
                .blockingGet();

        System.out.println(list);

    }

}
