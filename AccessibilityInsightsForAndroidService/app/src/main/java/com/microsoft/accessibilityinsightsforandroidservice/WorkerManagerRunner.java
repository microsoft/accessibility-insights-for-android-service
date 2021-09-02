package com.microsoft.accessibilityinsightsforandroidservice;

import android.content.Context;

import androidx.work.Configuration;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class WorkerManagerRunner {
    public Configuration configuration;
    public Context context;

    public WorkerManagerRunner(Context context) {
        this.context = context;
        configuration = initializeConfiguration();
        startWorkerManager();
    }

    //This methot was created to be able to mock the configuration in the unit test
    public static Configuration initializeConfiguration(){
        return new Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.INFO)
                .build();
    }

    public static OneTimeWorkRequest createTask(){
        return new OneTimeWorkRequest.Builder(CleanWorker.class)
                .setInitialDelay(5, TimeUnit.MINUTES)
                .build();
    }

    public WorkManager startWorkerManager(){
        WorkManager.initialize(context, configuration);
        return WorkManager.getInstance(context);
    }

    public void enqueueTask(String cacheDirectory){
        Configuration myConfig = new Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build();
        WorkManager.initialize(context, myConfig);


        Data inputData = new Data.Builder().putString("cacheDirectoryPath", cacheDirectory).build();
        OneTimeWorkRequest cleanFilesWorker = new OneTimeWorkRequest.Builder(CleanWorker.class)
            .setInitialDelay(5, TimeUnit.MINUTES)
            .setInputData(inputData)
            .build();
        WorkManager.getInstance(context).enqueue(cleanFilesWorker);

        WorkerManagerRunner workerManagerRunner = new WorkerManagerRunner(context);
        workerManagerRunner.startWorkerManager().enqueue(workerManagerRunner.createTask());
    }
}
