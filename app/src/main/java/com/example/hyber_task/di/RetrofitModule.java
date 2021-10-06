package com.example.hyber_task.di;

import com.example.hyber_task.interfaces.ItemsApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class RetrofitModule {
    @Provides
    @Singleton
    public static ItemsApi provideItemApi() {

        return new Retrofit
                .Builder()
                .baseUrl("https://elsayedmustafa.github.io/HyperoneWebservice/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
                .create(ItemsApi.class);
    }

}
