package com.mayurg.scribblearena.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mayurg.scribblearena.data.remote.ws.models.PlayerData
import com.mayurg.scribblearena.databinding.ItemPlayerBinding
import com.mayurg.scribblearena.ui.drawing.DrawingActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Adapter class for displaying players in [DrawingActivity] drawer on the left.
 *
 * In shows current points & ranking of the players.
 * Also, An icon to indicate which player is drawing
 *
 * Created On 27/08/2021
 * @author Mayur Gajra
 */
class PlayerAdapter @Inject constructor() :
    RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {

    /**
     * View holder for displaying player details like name,points etc.
     */
    class PlayerViewHolder(val binding: ItemPlayerBinding) : RecyclerView.ViewHolder(binding.root)

    /**
     * This helper function helps in finding the difference in current list
     * & newly received list so we don't update the whole data
     */
    suspend fun updateDataset(newDataset: List<PlayerData>) = withContext(Dispatchers.Default) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return players.size
            }

            override fun getNewListSize(): Int {
                return newDataset.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return players[oldItemPosition] == newDataset[newItemPosition]
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return players[oldItemPosition] == newDataset[newItemPosition]
            }
        })

        withContext(Dispatchers.Main) {
            players = newDataset
            diff.dispatchUpdatesTo(this@PlayerAdapter)
        }
    }

    /**
     * Adapter list containing players data in the current room
     */
    var players = listOf<PlayerData>()
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        return PlayerViewHolder(
            ItemPlayerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val player = players[position]
        holder.binding.apply {
            val playerRankText = "${player.rank}. "
            tvRank.text = playerRankText
            tvScore.text = player.score.toString()
            tvUsername.text = player.username
            ivPencil.isVisible = player.isDrawing
        }
    }

    override fun getItemCount(): Int {
        return players.size
    }


}