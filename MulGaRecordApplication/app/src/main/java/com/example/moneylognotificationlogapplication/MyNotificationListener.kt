package com.example.moneylognotificationlogapplication

import android.content.pm.PackageManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.moneylognotificationlogapplication.db.AppEntity
import com.example.moneylognotificationlogapplication.db.NotificationDatabase
import com.example.moneylognotificationlogapplication.db.NotificationEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyNotificationListener : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        // 알림에서 제목과 내용을 추출
        val notification = sbn.notification
        val extras = notification.extras
        val title = extras.getCharSequence("android.title")?.toString() ?: ""
        val text = extras.getCharSequence("android.text")?.toString() ?: ""

        // PackageManager를 이용해 앱의 실제 이름(라벨)을 가져옴
        val appName = try {
            val pm = packageManager
            val applicationInfo = pm.getApplicationInfo(sbn.packageName, 0)
            pm.getApplicationLabel(applicationInfo).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            sbn.packageName
        }

        // 백그라운드 스레드에서 Room 데이터베이스 작업 수행 (Coroutine 사용)
        CoroutineScope(Dispatchers.IO).launch {
            val db = NotificationDatabase.getInstance(applicationContext)
            val dao = db.notificationDao()

            // 앱 이름으로 해당 앱이 이미 등록되어 있는지 확인
            var appEntity = dao.getAppByName(appName)
            if (appEntity == null) {
                // 등록되어 있지 않으면 새 앱 엔티티를 삽입
                val newAppId = dao.insertApp(AppEntity(0, appName))
                appEntity = AppEntity(newAppId, appName)
            }

            // 현재 시간을 밀리초까지 포맷팅
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
            val formattedTime = dateFormat.format(Date())

            // 알림 정보를 NotificationEntity로 생성 후 삽입
            val notificationEntity = NotificationEntity(
                id = 0,
                appId = appEntity.id,
                title = title,
                content = text,
                wholeContent = "$extras $formattedTime"
            )
            dao.insertNotification(notificationEntity)
        }
    }
}
