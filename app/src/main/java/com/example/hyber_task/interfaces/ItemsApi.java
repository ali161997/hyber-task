package com.example.hyber_task.interfaces;

import com.example.hyber_task.models.Item;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ItemsApi {
    @GET("getListOfFilesResponse.json")
    Observable<List<Item>> getResponse();


    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Url String fileUrl);
}
