package com.example.bibliohub.data

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.bibliohub.data.entities.order.OfflineOrderRepository
import com.example.bibliohub.data.entities.order.OrderDao
import com.example.bibliohub.data.entities.order.OrderRepository
import com.example.bibliohub.data.entities.orderDetails.OfflineOrderDetailsRepository
import com.example.bibliohub.data.entities.orderDetails.OrderDetailsDao
import com.example.bibliohub.data.entities.orderDetails.OrderDetailsRepository
import com.example.bibliohub.data.entities.product.OfflineProductRepository
import com.example.bibliohub.data.entities.product.ProductDao
import com.example.bibliohub.data.entities.product.ProductRepository
import com.example.bibliohub.data.entities.user.OfflineUserRepository
import com.example.bibliohub.data.entities.user.UserDao
import com.example.bibliohub.data.entities.user.UserRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


const val BIBLIO_HUB_PREFERENCE_NAME = "biblioHub_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = BIBLIO_HUB_PREFERENCE_NAME
)

@Module
@InstallIn(SingletonComponent::class)
abstract class AppRepositoryModule {

    @Singleton
    @Binds
    abstract fun bindOrderRepository(orderRepository: OfflineOrderRepository): OrderRepository

    @Singleton
    @Binds
    abstract fun bindOrderDetailsRepository(orderDetailsRepository: OfflineOrderDetailsRepository): OrderDetailsRepository

    @Singleton
    @Binds
    abstract fun bindProductRepository(productRepository: OfflineProductRepository): ProductRepository

    @Singleton
    @Binds
    abstract fun bindUserRepository(userRepository: OfflineUserRepository): UserRepository
}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun provideBiblioHubDatabase(@ApplicationContext context: Context): BiblioHubDatabase {
        return BiblioHubDatabase.getInstance(context)
    }

    @Provides
    fun provideOrderDao(database: BiblioHubDatabase): OrderDao {
        return database.orderDao()
    }

    @Provides
    fun provideOrderDetailsDao(database: BiblioHubDatabase): OrderDetailsDao {
        return database.orderDetailsDao()
    }

    @Provides
    fun provideProductDao(database: BiblioHubDatabase): ProductDao {
        return database.productDao()
    }

    @Provides
    fun provideUserDao(database: BiblioHubDatabase): UserDao {
        return database.userDao()
    }
    @Singleton

    @Provides
    fun providePreferenceRepository(context: Context): BiblioHubPreferencesRepository {
        val biblioHubPreferencesRepository = BiblioHubPreferencesRepository(context.dataStore)
        return biblioHubPreferencesRepository
    }

//    @Singleton
//    @Provides
//    fun provideOrderRepository(orderDao: OrderDao): OrderRepository = OfflineOrderRepository(orderDao)
//
//    @Singleton
//    @Provides
//    fun provideOrderDetailsRepository(orderDetailsDao: OrderDetailsDao): OrderDetailsRepository = OfflineOrderDetailsRepository(orderDetailsDao)
//
//    @Singleton
//    @Provides
//    fun provideProductRepository(productDao: ProductDao): ProductRepository = OfflineProductRepository(productDao)
//
//    @Singleton
//    @Provides
//    fun provideUserRepository(userDao: UserDao): UserRepository = OfflineUserRepository(userDao)
}