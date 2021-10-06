package com.example.hyber_task.view_models;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.hyber_task.models.Item;
import com.example.hyber_task.repository.Repository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;

@HiltViewModel
public class ItemViewModel extends ViewModel {
    private static final String TAG = "ItemViewModel";
    private final MutableLiveData<ArrayList<Item>> itemsList = new MutableLiveData<>();
    private final Repository repository;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Context context;

    @Inject
    public ItemViewModel(Repository repository) {
        this.repository = repository;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public MutableLiveData<ArrayList<Item>> getItemsList() {
        return itemsList;
    }

    public void getItemsFromRepository() {
        Disposable disposable = repository.getItems()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                            itemsList.setValue((ArrayList<Item>) result);
                            //compositeDisposable.dispose();

                        }
                        , error -> {
                            Log.e(TAG, "getItemsFromRepository: ", error);

                        });

        compositeDisposable.add(disposable);
        // compositeDisposable.dispose();

    }

    public void downloadFileFromRepository(int position) {
        @NonNull Observable<ResponseBody> disposable = repository.getFile(itemsList.getValue().get(position).getUrl())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());


        Observer<ResponseBody> observer = new Observer<ResponseBody>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
            }

            @Override
            public void onNext(@NonNull ResponseBody responseBody) {

                if (responseBody.contentLength() == -1) {
                    Toast.makeText(context, "cannot download ,file length =-1", Toast.LENGTH_LONG).show();
                    return;
                }
                Thread thread = new Thread(() -> {
                    try {
                        String[] name = itemsList.getValue().get(position).getUrl().split("/");
                        writeResponseBodyToDisk(responseBody, name[name.length - 1], position);
                        itemsList.getValue().get(position).setDownloaded(true);
                        itemsList.getValue().get(position).setDownloading(false);
                        itemsList.postValue(itemsList.getValue());
                        Log.d(TAG, "run: ok");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                thread.start();


            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: ");
            }
        };
        disposable.subscribe(observer);
        // compositeDisposable.dispose();

    }


    boolean writeResponseBodyToDisk(ResponseBody body, String name, int position) {

        try {
            // todo change the file location/name according to your needs
            String s = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + name;
            Log.d(TAG, "writeResponseBodyToDisk: path :" + s);
            File futureStudioIconFile = new File(s);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                //    Log.d(TAG, "writeResponseBodyToDisk: content lenght->"+ fileSize);

                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        Log.i(TAG, "writeResponseBodyToDisk: read==-1 :" + (read == -1));
                        break;
                    }

                    outputStream.write(fileReader, 0, read);
                    //
                    Long filTemp = fileSizeDownloaded;
                    fileSizeDownloaded += read;
                    if (fileSizeDownloaded < filTemp)
                        Log.d(TAG, "writeResponseBodyToDisk: error");
                    Log.d(TAG, "writeResponseBodyToDisk er: " + fileSizeDownloaded + " original " + fileSize);
                    final Long percentage = (fileSizeDownloaded * 100) / fileSize;

                    itemsList.getValue().get(position).setDownloadPercentage((percentage.intValue()));
                    itemsList.getValue().get(position).setDownloading(true);
                    itemsList.postValue(itemsList.getValue());
//                    Log.d(TAG, "writeResponseBodyToDisk: "+position);
//                    Log.d(TAG, "writeResponseBodyToDisk: "+percentage +"%");

                    // Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);


                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                Log.d(TAG, "writeResponseBodyToDisk: " + e.getMessage());
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            Log.d(TAG, "writeResponseBodyToDisk2: " + e.getMessage());

        }
        return false;
    }
    // Most of it is just regular Java I/O boilerplate. You might need to adjust the first line on where and with what name your file is being saved. When you have done

}
