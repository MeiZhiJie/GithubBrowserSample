package com.android.example.github.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.android.example.github.vo.Contributor;
import com.android.example.github.vo.Repo;
import com.android.example.github.vo.RepoSearchResult;
import com.android.example.github.vo.User;

/**
 * Main database description.
 */
@Database(
        entities = {
                User.class,
                Repo.class,
                Contributor.class,
                RepoSearchResult.class},
        version = 3,
        exportSchema = false
)
public abstract class GithubDb extends RoomDatabase {
    public abstract UserDao userDao();

    public abstract RepoDao repoDao();
}
