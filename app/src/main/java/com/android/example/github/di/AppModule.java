package com.android.example.github.di;

import android.app.Application;

import androidx.room.Room;

import com.android.example.github.api.GithubService;
import com.android.example.github.db.GithubDb;
import com.android.example.github.db.RepoDao;
import com.android.example.github.db.UserDao;
import com.android.example.github.util.LiveDataCallAdapterFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = ViewModelModule.class)
public class AppModule {
    @Singleton
    @Provides
    public GithubService provideGithubService() {
        return new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build()
                .create(GithubService.class);
    }

    @Singleton
    @Provides
    public GithubDb provideDb(Application app) {
        return Room
                .databaseBuilder(app, GithubDb.class, "github.db")
                .fallbackToDestructiveMigration()
                .build();
    }

    @Singleton
    @Provides
    public UserDao provideUserDao(GithubDb db) {
        return db.userDao();
    }

    @Singleton
    @Provides
    public RepoDao provideRepoDao(GithubDb db) {
        return db.repoDao();
    }
}
