package com.yahya.dailyflow.di

import com.yahya.dailyflow.data.repository.MongoRepository
import com.yahya.dailyflow.data.repository.MongoRepositoryImpl
import com.yahya.dailyflow.model.Diary
import com.yahya.dailyflow.util.Constants.APP_ID
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.User
import io.realm.kotlin.mongodb.sync.SyncConfiguration

@Module
@InstallIn(SingletonComponent::class)
object RealmModule {

    @Provides
    fun provideRealmApp(): App {
        return App.create(APP_ID)
    }

    @Provides
    fun provideRealmUser(app: App): User? {
        return app.currentUser
    }

    @Provides
    fun provideRealm(user: User?): Realm? {
        if (user == null) {
            return null
        }
        val config =
            SyncConfiguration.Builder(user, setOf(Diary::class)).initialSubscriptions { sub ->
                add(
                    query = sub.query<Diary>("owner_id == $0", user.id)
                )
            }.build()
        return Realm.open(config)
    }

    @Provides
    fun provideMongoRepository(user: User?, realm: Realm?): MongoRepository {
        return MongoRepositoryImpl(user = user, realm = realm)
    }

}