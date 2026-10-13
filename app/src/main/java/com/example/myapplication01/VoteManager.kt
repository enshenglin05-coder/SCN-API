package com.example.myapplication01

import android.content.Context
import android.content.SharedPreferences

object VoteManager {

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences("card_vote_prefs", Context.MODE_PRIVATE)
    }

    // 取得當前使用者對該卡片的表態狀態 (0:無, 1:讚, -1:倒讚)
    fun getUserVoteState(context: Context, cardId: String): Int {
        return getPrefs(context).getInt("card_vote_$cardId", 0)
    }

    // 處理「讚 (👍)」點擊邏輯
    fun onUpVoteClicked(context: Context, cardId: String, currentUp: Int, currentDown: Int): Pair<Int, Int> {
        val currentState = getUserVoteState(context, cardId)
        val prefs = getPrefs(context).edit()

        return when (currentState) {
            1 -> { // 原本是讚 -> 取消讚
                prefs.putInt("card_vote_$cardId", 0).apply()
                Pair(currentUp - 1, currentDown)
            }
            -1 -> { // 原本是倒讚 -> 改成讚
                prefs.putInt("card_vote_$cardId", 1).apply()
                Pair(currentUp + 1, currentDown - 1)
            }
            else -> { // 原本未表態 -> 加上讚
                prefs.putInt("card_vote_$cardId", 1).apply()
                Pair(currentUp + 1, currentDown)
            }
        }
    }

    // 處理「倒讚 (👎)」點擊邏輯
    fun onDownVoteClicked(context: Context, cardId: String, currentUp: Int, currentDown: Int): Pair<Int, Int> {
        val currentState = getUserVoteState(context, cardId)
        val prefs = getPrefs(context).edit()

        return when (currentState) {
            -1 -> { // 原本是倒讚 -> 取消倒讚
                prefs.putInt("card_vote_$cardId", 0).apply()
                Pair(currentUp, currentDown - 1)
            }
            1 -> { // 原本是讚 -> 改成倒讚
                prefs.putInt("card_vote_$cardId", -1).apply()
                Pair(currentUp - 1, currentDown + 1)
            }
            else -> { // 原本未表態 -> 加上倒讚
                prefs.putInt("card_vote_$cardId", -1).apply()
                Pair(currentUp, currentDown + 1)
            }
        }
    }
}
