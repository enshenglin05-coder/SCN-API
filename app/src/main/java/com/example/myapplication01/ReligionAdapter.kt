package com.example.myapplication01

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.recyclerview.widget.RecyclerView

class ReligionAdapter(
    private var cards: List<ReligionCard>,
    private val onCardSelected: (ReligionCard) -> Unit
) : RecyclerView.Adapter<ReligionAdapter.ViewHolder>() {

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val composeContent: ComposeView = view.findViewById(R.id.cardComposeContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_religion_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val card = cards[position]
        val context = holder.itemView.context
        
        val isSelected = selectedPosition == position

        // 獲取配色
        val (titleColorHex, _) = when (card.category) {
            ReligionCategory.ELDER -> "#FFD700" to "#FFE082"
            ReligionCategory.COMPASSION -> "#D8A7CA" to "#C38EB4"
            ReligionCategory.WARRIOR -> "#4FC3F7" to "#81D4FA"
            ReligionCategory.WISDOM -> "#E040FB" to "#EA80FC"
            ReligionCategory.CHRISTIAN -> "#FFFFFF" to "#DDDDDD"
            ReligionCategory.BUDDHIST_AMBER -> "#FFB74D" to "#FFE0B2"
            ReligionCategory.BUDDHIST_GOLD -> "#FFD700" to "#FFE082"
            ReligionCategory.SHINTO -> "#FF7043" to "#FFAB91"
            ReligionCategory.JUDAISM -> "#29B6F6" to "#81D4FA"
            ReligionCategory.ISLAM -> "#81C784" to "#C8E6C9"
            ReligionCategory.CATHOLIC_BLUE -> "#81D4FA" to "#B3E5FC"
            ReligionCategory.CATHOLIC_GOLD -> "#FFE082" to "#FFF9C4"
            ReligionCategory.GENERAL -> "#A8E6CF" to "#C8E6C9"
        }

        // 使用 ComposeView 渲染全透明背景、流光邊框與鏤空文字
        holder.composeContent.apply {
            isClickable = false
            isFocusable = false
            setContent {
                key(position, isSelected) {
                    ReligionCardComposeView(
                        name = context.getString(card.nameResId),
                        desc = context.getString(card.descResId),
                        themeColor = ComposeColor(android.graphics.Color.parseColor(titleColorHex)),
                        isSelected = isSelected,
                        isRainbowEnabled = GlobalState.isRainbowEnabled
                    )
                }
            }
        }

        holder.itemView.setOnClickListener {
            val currentPos = holder.adapterPosition
            if (currentPos != RecyclerView.NO_POSITION && selectedPosition != currentPos) {
                selectedPosition = currentPos
                
                // 觸發全螢幕閃爆 (採用該卡片自身的主題色)
                GlobalState.flashColor = ComposeColor(android.graphics.Color.parseColor(titleColorHex))
                GlobalState.flashTrigger = System.currentTimeMillis()
                
                // 儲存選定的信仰顏色，供後續 UI 頁面對齊使用
                GlobalState.selectedThemeColorHex = titleColorHex

                notifyDataSetChanged()
                onCardSelected(card)
            }
        }
    }

    override fun getItemCount() = cards.size

    fun updateData(newCards: List<ReligionCard>) {
        this.cards = newCards
        this.selectedPosition = RecyclerView.NO_POSITION
        notifyDataSetChanged()
    }
}
