package sk.tuke.zadanie_szabados;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Category.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CategoryDao categoryDao();
}