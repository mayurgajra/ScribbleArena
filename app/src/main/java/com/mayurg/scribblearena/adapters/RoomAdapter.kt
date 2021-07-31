package com.mayurg.scribblearena.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mayurg.scribblearena.data.remote.ws.Room
import com.mayurg.scribblearena.databinding.ItemRoomBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created On 31/07/2021
 * @author Mayur Gajra
 */
class RoomAdapter @Inject constructor() :
    RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {

    class RoomViewHolder(val binding: ItemRoomBinding) : RecyclerView.ViewHolder(binding.root)

    suspend fun updateDataset(newDataset: List<Room>) = withContext(Dispatchers.Default){
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback(){
            override fun getOldListSize(): Int {
                return rooms.size
            }

            override fun getNewListSize(): Int {
               return newDataset.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return rooms[oldItemPosition] == newDataset[newItemPosition]
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return rooms[oldItemPosition] == newDataset[newItemPosition]
            }
        })

        withContext(Dispatchers.Main){
            rooms = newDataset
            diff.dispatchUpdatesTo(this@RoomAdapter)
        }
    }

    var rooms = listOf<Room>()
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        return RoomViewHolder(
            ItemRoomBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = rooms[position]
        holder.binding.apply {
            tvRoomName.text = room.name
            val playerCountText = "${room.playerCount}/${room.maxPlayers}"
            tvRoomPersonCount.text = playerCountText

            root.setOnClickListener {
                onRoomClickListener?.let { click ->
                    click(room)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return rooms.size
    }

    private var onRoomClickListener: ((Room) -> Unit)? = null

    fun setOnRoomClickListener(listener: ((Room) -> Unit)) {
        onRoomClickListener = listener
    }


}