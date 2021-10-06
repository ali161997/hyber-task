package com.example.hyber_task.repository;

import com.example.hyber_task.interfaces.ItemsApi;
import com.example.hyber_task.models.Item;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class Repository {
    private final ItemsApi itemsApi;


    @Inject
    public Repository(ItemsApi itemsApi) {
        this.itemsApi = itemsApi;
    }

    public Observable<List<Item>> getItems() {
        return itemsApi.getResponse();
    }
    public Observable<ResponseBody>getFile(String url){
        return itemsApi.downloadFile(url);
    }



}
