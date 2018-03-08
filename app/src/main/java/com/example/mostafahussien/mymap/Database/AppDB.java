package com.example.mostafahussien.mymap.Database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

import com.example.mostafahussien.mymap.model.MyPlace;

@Database(entities = { MyPlace.class},version = 5)                    // version of our database (which will be incremented every time we change something in the database schema).
public abstract class AppDB extends RoomDatabase {
    private static final String DB_NAME = "appDatabase.db";
    private static volatile AppDB instance;
    private static Context con;

    public static synchronized AppDB getInstance(Context context){
        con=context;
        if(instance==null){
            instance=create(context);
        }
        return instance;
    }
    static final Migration MIGRATION_1_2 = new Migration(2,5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
          //  database.execSQL("DROP TABLE place");
            database.execSQL("CREATE TABLE myPlaces ( address TEXT NOT NULL PRIMARY KEY  ,\n" +
                    " latitude REAL NOT NULL ,\n" +
                    " longitude REAL NOT NULL )" );
        }
    };

    private static AppDB create(Context context){
        return Room.databaseBuilder(context,AppDB.class,DB_NAME)
                .addMigrations(MIGRATION_1_2)
                .build();
    }
    public abstract FavoriteDAO getFavoriteDAO();
}
