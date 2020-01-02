package us.bojie.libnetwork.cache;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import us.bojie.libcommon.AppGlobals;

@Database(entities = {Cache.class}, version = 1)
public abstract class CacheDatabase extends RoomDatabase {
    private static final CacheDatabase database;

    static {
        // just in memory, if process is killed, data may lost
//        Room.inMemoryDatabaseBuilder()
        database = Room.databaseBuilder(AppGlobals.getApplication(), CacheDatabase.class, "svm_cache")
                .allowMainThreadQueries()
//              .addCallback()
//              .setQueryExecutor()
//              .openHelperFactory()
//              .setJournalMode()
//              .fallbackToDestructiveMigration()
//              .fallbackToDestructiveMigrationFrom()
//              .addMigrations(sMigration)
                .build();
    }

    public abstract CacheDao getCache();

    public static CacheDatabase get() {
        return database;
    }

//    static Migration sMigration = new Migration(1, 3) {
//        @Override
//        public void migrate(@NonNull SupportSQLiteDatabase database) {
//            database.execSQL("alter table teacher rename to student");
//        }
//    };
}
