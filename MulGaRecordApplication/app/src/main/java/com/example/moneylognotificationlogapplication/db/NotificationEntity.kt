package com.example.moneylognotificationlogapplication.db
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index


// 앱 정보를 저장하는 테이블
@Entity(
    tableName = "apps",
    indices = [Index(value = ["appName"], unique = true)]
)
data class AppEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val appName: String
)

// 알림 정보를 저장하는 테이블. 외래키를 통해 appId로 AppEntity와 연결합니다.
@Entity(
    tableName = "notifications",
    foreignKeys = [ForeignKey(
        entity = AppEntity::class,
        parentColumns = ["id"],
        childColumns = ["appId"],
        onDelete = ForeignKey.CASCADE // 앱이 삭제되면 관련 알림도 함께 삭제합니다.
    )],
    indices = [Index(value = ["appId"])]
)
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val appId: Long,
    val title: String?,
    val content: String?,
    val wholeContent: String?
)