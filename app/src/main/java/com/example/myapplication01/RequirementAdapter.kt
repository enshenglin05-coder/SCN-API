package com.example.myapplication01

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.TextView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import java.util.*

class RequirementAdapter(
    private var cards: List<RequirementCard>,
    private var userLat: Double? = null,
    private var userLng: Double? = null,
    private val onAction: (ActionType, RequirementCard?, View?) -> Unit,
    private val onPublishAction: (PublishActionType, View?) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_PUBLISH = 0
        const val TYPE_CARD = 1
    }

    enum class ActionType { NAVIGATE, CONTACT, PHONE_CALL, SOCIAL, DELETE, MENU, LIKE, DISLIKE, PACK, AI }
    enum class PublishActionType { BACK, LOCATION, TAB_SELECT, PUBLISH, REFRESH, SAVE_DRAFT, CLEAR_DRAFT }

    class PublishViewHolder(view: View) : RecyclerView.ViewHolder(view)

    class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCategoryTag: TextView = view.findViewById(R.id.tv_card_title)
        val tvLikeCount: TextView = view.findViewById(R.id.tv_like_count)
        val tvDislikeCount: TextView = view.findViewById(R.id.tv_dislike_count)
        val btnLikeGroup: View = view.findViewById(R.id.btn_like_group)
        val btnDislikeGroup: View = view.findViewById(R.id.btn_dislike_group)
        val btnRiskControlMenu: View = view.findViewById(R.id.layout_report_area)
        val tvReportCount: TextView = view.findViewById(R.id.tv_report_count)
        val tvPublishTime: TextView = view.findViewById(R.id.tv_publish_time)
        val tvLocationCoords: TextView = view.findViewById(R.id.tv_location_coords)
        val btnGoogleMaps: Button = view.findViewById(R.id.btn_google_maps)
        val cvDescriptionCompose: ComposeView = view.findViewById(R.id.cv_description_compose)
        val btnExternalLink: Button = view.findViewById(R.id.btn_external_link)
        val btnContactAction: MaterialButton = view.findViewById(R.id.btn_contact_action)
        val btnPhoneAction: MaterialButton = view.findViewById(R.id.btn_phone_action)
        val cvGoogleMapsBorder: ComposeView = view.findViewById(R.id.cv_google_maps_border)
        val cvExternalLinkBorder: ComposeView = view.findViewById(R.id.cv_external_link_border)
        val cvContactBorder: ComposeView = view.findViewById(R.id.cv_contact_border)
        val cvPhoneBorder: ComposeView = view.findViewById(R.id.cv_phone_border)
        val cvDeleteBorder: ComposeView = view.findViewById(R.id.cv_delete_border)
        val flContactAction: View = view.findViewById(R.id.fl_contact_action)
        val flPhoneAction: View = view.findViewById(R.id.fl_phone_action)
        val flDeleteCard: View = view.findViewById(R.id.fl_delete_card)
        val btnDeleteCard: Button = view.findViewById(R.id.btn_delete_card)
        val tvCardId: TextView = view.findViewById(R.id.tv_card_id)
        val tvSwipeHint: TextView = view.findViewById(R.id.tv_swipe_hint)
        val btnHeaderPack: Button = view.findViewById(R.id.btn_header_pack)
        val cvHeaderPackBorder: ComposeView = view.findViewById(R.id.cv_header_pack_border)
        val btnHeaderAi: Button = view.findViewById(R.id.btn_header_ai)
        val cvHeaderAiBorder: ComposeView = view.findViewById(R.id.cv_header_ai_border)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) TYPE_PUBLISH else TYPE_CARD
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_PUBLISH) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_viewport_publish, parent, false)
            PublishViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_requirement_post, parent, false)
            CardViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PublishViewHolder) {
            bindPublishForm(holder)
        } else if (holder is CardViewHolder) {
            bindRequirementCard(holder, position - 1)
        }
    }

    private fun bindPublishForm(holder: PublishViewHolder) {
        // 通知 Activity 獲取視圖引用以便執行原有邏輯
        onPublishAction(PublishActionType.REFRESH, holder.itemView)
    }

    private fun bindRequirementCard(holder: CardViewHolder, index: Int) {
        val card = cards[index]
        val context = holder.itemView.context
        
        // 0. 讀取並解析主題顏色 (UI2 傳遞)
        val themeColorInt = ThemeUtils.parseColor(card.theme_color)
        
        // 額外處理：為背景提供極低透明度的主題色襯托
        val tintColorStateList = ColorStateList.valueOf(themeColorInt).withAlpha(15) // 6% 透明度
        holder.cvDescriptionCompose.backgroundTintList = tintColorStateList

        // 1. 基本資訊與色彩同步
        val drawerTitle = CategoryRepository.getCategoryTitle(card.category_code)
        holder.tvCategoryTag.text = TextFormatter.formatCardTitle(drawerTitle)
        holder.tvCategoryTag.maxLines = 2
        holder.tvCategoryTag.setTextColor(themeColorInt) // 同步主題色
        
        holder.tvCardId.visibility = View.GONE // 1.0 隱藏內部 ID
        holder.tvLikeCount.text = card.like_count.toString()
        holder.tvDislikeCount.text = card.dislike_count.toString()
        holder.tvReportCount.text = "檢舉 ${card.reporter_ids.size}"

        // 2. 時間
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        holder.tvPublishTime.text = "發布時間：${sdf.format(Date(card.created_at))}"

        // 3. 座標與距離及動態編號 (例如 【卡片 #01】)
        val latStr = String.format(Locale.getDefault(), "%.6f", card.gps_lat)
        val lngStr = String.format(Locale.getDefault(), "%.6f", card.gps_lng)
        val curLat = userLat
        val curLng = userLng
        val distText = if (curLat != null && curLng != null) {
            val d = DistanceUtils.calculateDistance(curLat, curLng, card.gps_lat, card.gps_lng)
            " (距離: ${String.format(Locale.getDefault(), "%.2f", d)} km)"
        } else ""
        val cardIndexStr = String.format(Locale.getDefault(), "【卡片 #%02d】", index + 1)
        holder.tvLocationCoords.text = "位置: $latStr, $lngStr$distText $cardIndexStr"

        // 4. Header 按鈕邏輯 (一鍵打包 & AI 發布)
        holder.cvHeaderPackBorder.apply {
            isClickable = false
            isFocusable = false
            setContent {
                RainbowBorderWrapper(
                    modifier = Modifier.fillMaxSize(),
                    cornerRadius = 8.dp,
                    borderWidth = 2.2.dp
                ) {}
            }
        }
        holder.cvHeaderAiBorder.apply {
            isClickable = false
            isFocusable = false
            setContent {
                RainbowBorderWrapper(
                    modifier = Modifier.fillMaxSize(),
                    cornerRadius = 8.dp,
                    borderWidth = 2.2.dp
                ) {}
            }
        }
        holder.btnHeaderPack.setOnClickListener { onAction(ActionType.PACK, card, it) }
        holder.btnHeaderAi.setOnClickListener { onAction(ActionType.AI, card, it) }

        // 5. 說明文字與展開邏輯 (精準修復：切換為 Compose State 驅動)
        holder.cvDescriptionCompose.setContent {
            // 使用 remember(card.card_id) 確保在 RecyclerView 複用時狀態正確對應
            var isExpanded by remember(card.card_id) { mutableStateOf(false) }
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .clickable { isExpanded = !isExpanded }
            ) {
                Text(
                    text = "說明：${card.description}",
                    color = ComposeColor(themeColorInt),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp) // 預留底部空間給按鈕
                )

                // 展開/收起 按鈕
                Text(
                    text = if (isExpanded) "（收起）" else "（展開）",
                    color = if (isExpanded) ComposeColor(0xFF00FFFF) else ComposeColor(0xFF4285F4),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .clickable { isExpanded = !isExpanded }
                )
            }
        }

        // 5. Google Maps 邊框 (膠囊型)
        holder.cvGoogleMapsBorder.apply {
            isClickable = false
            isFocusable = false
            setContent {
                RainbowBorderWrapper(
                    modifier = Modifier.fillMaxSize(),
                    cornerRadius = 18.dp,
                    borderWidth = 2.2.dp,
                    underlayColor = ComposeColor(themeColorInt).copy(alpha = 0.4f)
                ) {}
            }
        }

        // 7. 操作按鈕與檢舉彈窗
        val voteState = VoteManager.getUserVoteState(context, card.card_id)
        holder.btnLikeGroup.alpha = if (voteState == 1) 1.0f else 0.5f
        holder.btnDislikeGroup.alpha = if (voteState == -1) 1.0f else 0.5f

        holder.btnLikeGroup.setOnClickListener { onAction(ActionType.LIKE, card, it) }
        holder.btnDislikeGroup.setOnClickListener { onAction(ActionType.DISLIKE, card, it) }
        holder.btnRiskControlMenu.setOnClickListener { onAction(ActionType.MENU, card, it) }

        // 7. 通訊按鈕顏色邏輯 (11x8 統一外殼模組化)
        val contactPrefix = card.contact_method.name

        // 7.1 通訊對接按鈕 (LINE/WeChat/Telegram/Phone)
        if (card.contact_value.isNotEmpty()) {
            holder.flContactAction.visibility = View.VISIBLE
            
            // 獨立套用 128 色流光邊框
            holder.cvContactBorder.apply {
                isClickable = false
                isFocusable = false
                setContent {
                    RainbowBorderWrapper(
                        modifier = Modifier.fillMaxSize(),
                        cornerRadius = 22.dp,
                        borderWidth = 2.2.dp
                    ) {}
                }
            }

            val cleanVal = ContactUtils.cleanContactValue(card.contact_value)
            when (card.contact_method) {
                RequirementCard.ContactMethod.LINE -> {
                    holder.btnContactAction.text = "💬 LINE ID ($cleanVal)"
                    holder.btnContactAction.setTextColor(Color.parseColor("#06C755"))
                }
                RequirementCard.ContactMethod.TELEGRAM -> {
                    holder.btnContactAction.text = "💬 Telegram ($cleanVal)"
                    holder.btnContactAction.setTextColor(Color.parseColor("#24A1DE"))
                }
                RequirementCard.ContactMethod.WECHAT -> {
                    holder.btnContactAction.text = "💬 WeChat ID (微信: $cleanVal)"
                    holder.btnContactAction.setTextColor(Color.parseColor("#07C160"))
                }
                RequirementCard.ContactMethod.PHONE -> {
                    holder.btnContactAction.text = "💬 撥打電話/簡訊 ($cleanVal)"
                    holder.btnContactAction.setTextColor(Color.parseColor("#4285F4"))
                }
                else -> {
                    holder.btnContactAction.text = "$contactPrefix ($cleanVal)"
                    holder.btnContactAction.setTextColor(themeColorInt) // 同步主題色
                }
            }
            holder.btnContactAction.backgroundTintList = null
        } else {
            holder.flContactAction.visibility = View.GONE
        }

        // 7.2 電話撥打按鈕 (原生佈局)
        val isDuplicatePhone = card.contact_method == RequirementCard.ContactMethod.PHONE && 
            card.contact_value == card.phone_number

        if (!card.phone_number.isNullOrEmpty() && !isDuplicatePhone) {
            holder.flPhoneAction.visibility = View.VISIBLE
            
            // 獨立套用 128 色流光邊框
            holder.cvPhoneBorder.apply {
                isClickable = false
                isFocusable = false
                setContent {
                    RainbowBorderWrapper(
                        modifier = Modifier.fillMaxSize(),
                        cornerRadius = 22.dp,
                        borderWidth = 2.2.dp
                    ) {}
                }
            }

            holder.btnPhoneAction.text = "📞 撥打電話 (${card.phone_number})"
            holder.btnPhoneAction.setTextColor(Color.parseColor("#4285F4"))
            holder.btnPhoneAction.backgroundTintList = null
            holder.btnPhoneAction.setOnClickListener { onAction(ActionType.PHONE_CALL, card, it) }
        } else {
            holder.flPhoneAction.visibility = View.GONE
        }

        // 8. 下架按鈕
        holder.flDeleteCard.visibility = if (card.isSelfOwner) View.VISIBLE else View.GONE
        
        // 獨立套用 128 色流光邊框
        if (card.isSelfOwner) {
            holder.cvDeleteBorder.apply {
                isClickable = false
                isFocusable = false
                setContent {
                    RainbowBorderWrapper(
                        modifier = Modifier.fillMaxSize(),
                        cornerRadius = 18.dp,
                        borderWidth = 2.0.dp,
                        underlayColor = androidx.compose.ui.graphics.Color(themeColorInt).copy(alpha = 0.4f)
                    ) {}
                }
            }
        }

        // 9. 外部連結膠囊邊框 (高度 36dp -> 半徑 18dp)
        if (!card.media_url.isNullOrEmpty()) {
            holder.btnExternalLink.visibility = View.VISIBLE
            holder.cvExternalLinkBorder.apply {
                isClickable = false
                isFocusable = false
                setContent {
                    RainbowBorderWrapper(
                        modifier = Modifier.fillMaxSize(),
                        cornerRadius = 18.dp,
                        borderWidth = 2.0.dp,
                        underlayColor = androidx.compose.ui.graphics.Color(themeColorInt).copy(alpha = 0.4f)
                    ) {}
                }
            }
            holder.btnExternalLink.setOnClickListener { onAction(ActionType.SOCIAL, card, it) }
        } else {
            holder.btnExternalLink.visibility = View.GONE
        }

        // 其他固定文字
        holder.btnGoogleMaps.text = context.getString(R.string.card_btn_map)
        holder.btnGoogleMaps.setTextColor(themeColorInt) // 導航文字同步主題色
        holder.btnExternalLink.text = context.getString(R.string.card_btn_ext)

        // 10. 底部向上滑動提示 (動態文字 + 呼吸動畫)
        holder.tvSwipeHint.text = if (card.drawer_type == "1A") 
            "↑ 向上滑動可查看需求列表" 
        else 
            "↑ 向上滑動可查看供應列表"

        // 呼吸動畫效果
        val swipeAnim = android.view.animation.AlphaAnimation(0.4f, 1.0f).apply {
            duration = 1200
            repeatMode = android.view.animation.Animation.REVERSE
            repeatCount = android.view.animation.Animation.INFINITE
        }
        holder.tvSwipeHint.startAnimation(swipeAnim)

        // 事件監聽
        holder.btnGoogleMaps.setOnClickListener { onAction(ActionType.NAVIGATE, card, it) }
        holder.btnContactAction.setOnClickListener { onAction(ActionType.CONTACT, card, it) }
        holder.btnDeleteCard.setOnClickListener { onAction(ActionType.DELETE, card, it) }
    }

    override fun getItemCount() = cards.size + 1

    fun updateData(newCards: List<RequirementCard>) {
        updateData(newCards, this.userLat, this.userLng)
    }

    fun updateData(newCards: List<RequirementCard>, newLat: Double?, newLng: Double?) {
        this.cards = newCards
        this.userLat = newLat
        this.userLng = newLng
        notifyDataSetChanged()
    }
}
