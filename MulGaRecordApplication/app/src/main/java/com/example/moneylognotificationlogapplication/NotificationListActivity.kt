package com.example.moneylognotificationlogapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.moneylognotificationlogapplication.databinding.ActivityNotificationListBinding
import com.example.moneylognotificationlogapplication.db.NotificationDatabase
import kotlinx.coroutines.launch

class NotificationListActivity : ComponentActivity() {
    private lateinit var binding: ActivityNotificationListBinding
    private lateinit var adapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 뷰 바인딩을 통해 레이아웃 인플레이트
        binding = ActivityNotificationListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 인텐트에서 전달받은 앱 id와 이름
        val appId = intent.getLongExtra("APP_ID", -1)
        val appName = intent.getStringExtra("APP_NAME") ?: "알림 목록"
        title = appName

        // toolbar의 title을 appName으로 설정
        binding.toolbar.title = appName

        // 툴바의 로고뷰 클릭 시 뒤로가기 설정
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // 어댑터 초기화 (빈 리스트로 생성)
        adapter = NotificationAdapter(emptyList())
        // 바인딩의 ListView ID가 실제 XML과 일치하는지 확인 (예: app_list_view)
        binding.notificationListView.setAdapter(adapter)

        // 데이터 로딩 함수 호출
        lifecycleScope.launch { 
            loadNotifications(appId)
        }
    }
    
    private fun loadNotifications(appId: Long) {
        // NotificationDatabase의 싱글턴 인스턴스 사용
        val db = NotificationDatabase.getInstance(applicationContext)
        // 알림 목록을 관찰하여 데이터가 변경되면 어댑터 업데이트
        db.notificationDao().getNotificationsByAppId(appId).observe(this) { notifications ->
            adapter.updateNotifications(notifications)
        }
    }
}