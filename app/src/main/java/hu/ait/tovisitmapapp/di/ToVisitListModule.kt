package hu.ait.tovisitmapapp.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import hu.ait.tovisitmapapp.data.ToVisitListAppDatabase
import hu.ait.tovisitmapapp.data.ToVisitListDAO
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Provides
    fun provideToVisitListDao(appDatabase: ToVisitListAppDatabase): ToVisitListDAO {
        return appDatabase.toVisitListDao()
    }

    @Provides
    @Singleton
    fun provideToVisitListAppDatabase(@ApplicationContext appContext: Context): ToVisitListAppDatabase {
        return ToVisitListAppDatabase.getDatabase(appContext)
    }
}