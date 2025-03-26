package com.example.moneylognotificationlogapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import android.content.Intent
import android.provider.Settings
import androidx.lifecycle.lifecycleScope
import com.example.moneylognotificationlogapplication.databinding.ActivityMainBinding
import com.example.moneylognotificationlogapplication.db.AppEntity
import com.example.moneylognotificationlogapplication.db.NotificationDatabase
import kotlinx.coroutines.launch



class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: AppListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)

        // 알림 접근 확인 후 설정 페이지로 이동하는 코드
        if (!isNotificationServiceEnabled()) {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }

        // 뷰 바인딩을 통해 레이아웃 인플레이트
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 다운로드 글자에 밑줄 추가


        // 어댑터 초기화 (빈 리스트로 생성)
        adapter = AppListAdapter(emptyList())
        // 바인딩의 ListView ID가 실제 XML과 일치하는지 확인 (예: app_list_view)
        binding.appListView.adapter = adapter


        // 앱 목록 아이템 클릭 시 NotificationListActivity로 이동
        binding.appListView.setOnItemClickListener { _, _, position, _ ->
            val app = adapter.getItem(position) as AppEntity
            val intent = Intent(this, NotificationListActivity::class.java).apply {
                putExtra("APP_ID", app.id)
                putExtra("APP_NAME", app.appName)
            }
            startActivity(intent)
        }


        // 데이터 로딩 함수 호출
        lifecycleScope.launch {
            loadAppList()
        }
    }

    private fun isNotificationServiceEnabled(): Boolean {
        // 현재 활성화된 알림 접근 서비스 목록에서 자신의 앱 서비스가 포함되어 있는지 확인합니다.
        val pkgName = packageName
        val flat = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        return flat?.contains(pkgName) ?: false
    }

    private fun loadAppList() {
        // NotificationDatabase의 싱글턴 인스턴스 사용
        val db = NotificationDatabase.getInstance(applicationContext)
        // 앱 목록을 관찰하여 데이터가 변경되면 어댑터 업데이트
        db.notificationDao().getAllApps().observe(this) { appList ->
            adapter.updateApp(appList)
        }
    }
}
