package com.example.moneylognotificationlogapplication.db

import android.app.Notification
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AppNotificationDao {
    // 앱 추가 (새 앱 삽입 후 자동 생성된 id 반환)
    @Insert
    fun insertApp(app: AppEntity):Long

    // 알림 추가 (새 알림 삽입 후 자동 생성된 id 반환)
    @Insert
    fun insertNotification(notification: NotificationEntity):Long

    // 앱 목록 전체 조회
    @Query("SELECT * FROM apps ORDER BY id DESC")
    fun getAllApps(): LiveData<List<AppEntity>>

    // 특정 앱(appId)에 해당하는 알림 목록 조회
    @Query("""
        SELECT * 
        FROM notifications n
        INNER JOIN apps a ON n.appId = a.id 
        WHERE appId = :appId
        ORDER BY n.id DESC""")
    fun getNotificationsByAppId(appId: Long): LiveData<List<NotificationEntity>>

    // 앱 이름으로 앱 조회 (없으면 null 반환)
    @Query("SELECT * FROM apps WHERE appName = :appName LIMIT 1")
    fun getAppByName(appName: String): AppEntity?
}