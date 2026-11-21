package pe.cajamapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pe.cajamapp.data.model.Destination

@Database(entities = [Destination::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun destinationDao(): DestinationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cajamapp_database.db"
                ).addCallback(AppDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.destinationDao())
                }
            }
        }

        suspend fun populateDatabase(dao: DestinationDao) {
            if (dao.getCount() == 0) {
                dao.insertDestinations(CajamarcaData.getInitialDestinations())
            }
        }
    }
}
