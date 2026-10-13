package com.example.myapplication01

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial

class UI2Activity : BaseActivity() {

    private lateinit var rvMainCategories: RecyclerView
    private lateinit var topBarUI2: View
    private lateinit var btnBack: ImageButton
    private lateinit var tvDistanceSetting: TextView
    private lateinit var switchBaichuanAI: SwitchMaterial

    private var currentDistance = 6 // 預設值改為 6 KM
    private var selectedCategoryId: String? = null
    private var selectedCategoryName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_ui2)

        initViews()
        handleWindowInsets()
        setupListeners()
        setupMainRecyclerView()
        
        setupRainbowEngine()
        
        updateDistanceText()
    }

    private fun initViews() {
        rvMainCategories = findViewById(R.id.rvMainCategories)
        topBarUI2 = findViewById(R.id.topBarUI2)
        btnBack = findViewById(R.id.btnBackUI2)
        tvDistanceSetting = findViewById(R.id.tvDistanceSetting)
        switchBaichuanAI = findViewById(R.id.switchBaichuanAI)
    }

    private fun handleWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootLayoutUI2)) { _, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            // 壓縮頂部安全區間距 (從 16dp 調降為 8dp)
            topBarUI2.updatePadding(top = insets.top + (resources.displayMetrics.density * 8).toInt())
            rvMainCategories.updatePadding(bottom = insets.bottom + (resources.displayMetrics.density * 24).toInt())
            windowInsets
        }
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        tvDistanceSetting.setOnClickListener {
            showDistancePickerDialog()
        }

        switchBaichuanAI.setOnCheckedChangeListener { _, _ ->
            // 無論切換結果如何，皆提示功能暫不提供
            Toast.makeText(this, "此功能暫不提供服務", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDistancePickerDialog() {
        val picker = NumberPicker(this).apply {
            minValue = 1
            maxValue = 50 // 最大上限設為 50 KM
            value = currentDistance
        }

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_distance_title))
            .setView(picker)
            .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                currentDistance = picker.value
                updateDistanceText()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun updateDistanceText() {
        tvDistanceSetting.text = "${getString(R.string.distance_setting_prefix)}$currentDistance ${getString(R.string.km_unit)}"
    }

    private fun setupRainbowEngine() {
        val rootLayout = findViewById<FrameLayout>(R.id.rootLayoutUI2)
        val composeView = ComposeView(this).apply {
            isClickable = false
            isFocusable = false
            setContent {
                Box(modifier = Modifier.fillMaxSize()) {
                    LightAppFullscreenAtmosphere()
                    LightAppSuiteBox()
                }
            }
        }
        rootLayout.addView(composeView, 0, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
    }

    private fun setupMainRecyclerView() {
        val mainCategories = listOf(
            getString(R.string.cat_1_food),
            getString(R.string.cat_2_clothing),
            getString(R.string.cat_3_housing),
            getString(R.string.cat_4_transport),
            getString(R.string.cat_5_education),
            getString(R.string.cat_6_entertainment),
            getString(R.string.cat_7_medical),
            getString(R.string.cat_8_secondhand),
            getString(R.string.cat_9_pet),
            getString(R.string.cat_10_service),
            Section11CharityData.SECTION_TITLE
        )
        
        rvMainCategories.layoutManager = LinearLayoutManager(this)
        rvMainCategories.adapter = MainCategoryAdapter(mainCategories) { id, name ->
            selectedCategoryId = id
            selectedCategoryName = name
            
            // 跳轉至 UI3 發佈與清單頁面
            val intent = Intent(this, UI3Activity::class.java).apply {
                putExtra("CATEGORY_ID", id)
                putExtra("CATEGORY_NAME", name)
                putExtra("LIMIT_DISTANCE", currentDistance)
                // 傳遞全域座標 (可能為 null)
                putExtra("USER_LAT", GlobalState.globalLatitude)
                putExtra("USER_LNG", GlobalState.globalLongitude)
            }
            startActivity(intent)
        }
    }

    // --- Adapters ---

    class MainCategoryAdapter(
        private val mainList: List<String>,
        private val onSubItemClick: (String, String) -> Unit
    ) : RecyclerView.Adapter<MainCategoryAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvName: TextView = view.findViewById(R.id.tvMainCategoryName)
            val rvSub: RecyclerView = view.findViewById(R.id.rvSubCategories)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_main_category, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val name = mainList[position]
            holder.tvName.text = name
            
            // 同步信仰色彩至大類別標題
            try {
                val themeColor = android.graphics.Color.parseColor(GlobalState.selectedThemeColorHex)
                holder.tvName.setTextColor(themeColor)
            } catch (e: Exception) {
                holder.tvName.setTextColor(android.graphics.Color.WHITE)
            }
            
            // 重構為 2 排橫向滑動 Grid
            holder.rvSub.layoutManager = GridLayoutManager(holder.itemView.context, 2, GridLayoutManager.HORIZONTAL, false)
            
            // 恢復雙排橫向露邊公式：左邊距 5% + (卡片寬度 42% * 2) + 間距 = 右側露出約 10-15%
            val displayMetrics = holder.itemView.context.resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels
            
            val leftPadding = (screenWidth * 0.05).toInt()
            val itemWidth = (screenWidth * 0.42).toInt() // 恢復為約 42% 寬度，確保一屏顯示 2 排多一點
            
            // 強制微調高度：壓縮中縫空間 (10dp -> 4dp)，釋放空間給格子長高 (75dp -> 80dp)
            val minItemHeight = (80 * displayMetrics.density).toInt()
            
            holder.rvSub.setPadding(leftPadding, 0, (screenWidth * 0.05).toInt(), 0)
            holder.rvSub.clipToPadding = false
            
            val categoryIndex = name.split(". ")[0] // e.g. "1"
            
            val subItems = List(8) { i -> 
                val subIdx = i + 1
                val subId = "$categoryIndex-$subIdx"
                
                val context = holder.itemView.context
                val resName = "sub_item_${categoryIndex}_$subIdx"
                val resId = context.resources.getIdentifier(resName, "string", context.packageName)

                // 【銜接邏輯】優先檢查是否為第 11 大項 (公益關懷)
                val subDisplayName = if (categoryIndex == "11") {
                    Section11CharityData.ITEMS[subId] ?: "Item $subIdx"
                } else if (resId != 0) {
                    val rawName = context.getString(resId)
                    // 同步字典邏輯：優先使用 CategoryMenuData 的格式
                    val cleaned = rawName.replace("/", "").replace(" ", "").replace("\n", "")
                    val key = CategoryMenuData.NAME_MAPPING[cleaned]
                    if (key != null) {
                        CategoryMenuData.MENU_ITEMS[key] ?: rawName
                    } else {
                        rawName
                    }
                } else {
                    "Item $subIdx"
                }
                
                SubItemData(subId, subDisplayName)
            }
            holder.rvSub.adapter = SubCategoryAdapter(subItems, itemWidth, minItemHeight, onSubItemClick)
        }

        override fun getItemCount() = mainList.size
    }

    data class SubItemData(val id: String, val name: String)

    class SubCategoryAdapter(
        private val subList: List<SubItemData>,
        private val itemWidth: Int,
        private val minItemHeight: Int,
        private val onSubItemClick: (String, String) -> Unit
    ) : RecyclerView.Adapter<SubCategoryAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvSubName: TextView = view.findViewById(R.id.tvSubCategoryName)
            val tvSubBadgeTop: TextView = view.findViewById(R.id.tvSubBadgeTop)
            val tvSubBadgeBottom: TextView = view.findViewById(R.id.tvSubBadgeBottom)
            val blockContainer: View = view.findViewById(R.id.blockContainer)
            val rainbowBorder: ComposeView = view.findViewById(R.id.subCatRainbowBorder)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sub_category, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val data = subList[position]
            holder.tvSubName.text = data.name
            
            // 動態對齊使用者選定的信仰色彩
            try {
                val themeColor = android.graphics.Color.parseColor(GlobalState.selectedThemeColorHex)
                holder.tvSubName.setTextColor(themeColor)
            } catch (e: Exception) {
                holder.tvSubName.setTextColor(android.graphics.Color.WHITE)
            }
            
            // 處理 Badge 標籤化邏輯 - 1.0 最終版隱蔽測試標籤
            holder.tvSubBadgeTop.visibility = View.GONE
            holder.tvSubBadgeBottom.visibility = View.GONE
            
            // 應用精確寬度
            val params = holder.itemView.layoutParams
            params.width = itemWidth
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            holder.itemView.layoutParams = params
            
            // 設定豆腐塊容器的保底高度
            holder.blockContainer.minimumHeight = minItemHeight
            
            // 配置流光邊框
            holder.rainbowBorder.apply {
                isClickable = false
                isFocusable = false
                setContent {
                    RainbowBorderWrapper(modifier = Modifier.fillMaxSize(), cornerRadius = 16.dp, borderWidth = 2.dp) {
                        // 僅繪製邊框
                    }
                }
            }
            
            holder.itemView.setOnClickListener {
                onSubItemClick(data.id, data.name)
            }
        }

        override fun getItemCount() = subList.size
    }
}
