package com.mayurg.scribblearena.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mayurg.scribblearena.data.remote.ws.models.Announcement
import com.mayurg.scribblearena.data.remote.ws.models.BaseModel
import com.mayurg.scribblearena.data.remote.ws.models.ChatMessage
import com.mayurg.scribblearena.databinding.ItemAnnouncementBinding
import com.mayurg.scribblearena.databinding.ItemChatMessageIncomingBinding
import com.mayurg.scribblearena.databinding.ItemChatMessageOutgoingBinding
import com.mayurg.scribblearena.ui.drawing.DrawingActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created On 19/08/2021
 * @author Mayur Gajra
 */

private const val VIEW_TYPE_INCOMING_MESSAGE = 0
private const val VIEW_TYPE_OUTGOING_MESSAGE = 1
private const val VIEW_TYPE_ANNOUNCEMENT = 2

/**
 * Adapter for displaying the chat messages & announcements inside the [DrawingActivity]
 *
 * @param username is the currently loggedIn/selected user name so we can differentiate
 * between [VIEW_TYPE_OUTGOING_MESSAGE] & [VIEW_TYPE_INCOMING_MESSAGE]
 */
class ChatMessageAdapter(
    private val username: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * View holder for displaying messages sent by other users
     */
    class IncomingChatMessageViewHolder(val binding: ItemChatMessageIncomingBinding) :
        RecyclerView.ViewHolder(binding.root)

    /**
     * View holder for displaying messages sent currently logged in user
     */
    class OutGoingChatMessageViewHolder(val binding: ItemChatMessageOutgoingBinding) :
        RecyclerView.ViewHolder(binding.root)

    /**
     * View holder for displaying announcements
     * such as:
     *          Player has joined
     *          Player has left
     */
    class AnnouncementViewHolder(val binding: ItemAnnouncementBinding) :
        RecyclerView.ViewHolder(binding.root)

    /**
     * Adapter list containing various subclasses of [BaseModel]
     */
    var chatObjects = listOf<BaseModel>()


    /**
     * This helper function helps in finding the difference in current list
     * & newly received list so we don't update the whole data
     */
    suspend fun updateDataset(newDataset: List<BaseModel>) = withContext(Dispatchers.Default) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return chatObjects.size
            }

            override fun getNewListSize(): Int {
                return newDataset.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return chatObjects[oldItemPosition] == newDataset[newItemPosition]
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return chatObjects[oldItemPosition] == newDataset[newItemPosition]
            }
        })

        withContext(Dispatchers.Main) {
            chatObjects = newDataset
            diff.dispatchUpdatesTo(this@ChatMessageAdapter)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_INCOMING_MESSAGE -> IncomingChatMessageViewHolder(
                ItemChatMessageIncomingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            VIEW_TYPE_OUTGOING_MESSAGE -> OutGoingChatMessageViewHolder(
                ItemChatMessageOutgoingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            VIEW_TYPE_ANNOUNCEMENT -> AnnouncementViewHolder(
                ItemAnnouncementBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            else -> throw IllegalStateException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AnnouncementViewHolder -> {
                val announcement = chatObjects[position] as Announcement
                holder.binding.apply {
                    tvAnnouncement.text = announcement.message
                    val dateFormat = SimpleDateFormat("kk:mm:ss", Locale.getDefault())
                    val date = dateFormat.format(announcement.timestamp)
                    tvTime.text = date

                    when (announcement.announcementType) {
                        Announcement.TYPE_EVERYBODY_GUESSED_IT -> {
                            root.setBackgroundColor(Color.LTGRAY)
                            tvAnnouncement.setTextColor(Color.BLACK)
                            tvTime.setTextColor(Color.BLACK)
                        }

                        Announcement.TYPE_PLAYER_GUESSED_WORD -> {
                            root.setBackgroundColor(Color.YELLOW)
                            tvAnnouncement.setTextColor(Color.BLACK)
                            tvTime.setTextColor(Color.BLACK)
                        }

                        Announcement.TYPE_PLAYER_JOINED -> {
                            root.setBackgroundColor(Color.GREEN)
                            tvAnnouncement.setTextColor(Color.BLACK)
                            tvTime.setTextColor(Color.BLACK)
                        }

                        Announcement.TYPE_PLAYER_LEFT -> {
                            root.setBackgroundColor(Color.RED)
                            tvAnnouncement.setTextColor(Color.WHITE)
                            tvTime.setTextColor(Color.WHITE)
                        }
                    }
                }
            }

            is IncomingChatMessageViewHolder -> {
                val message = chatObjects[position] as ChatMessage
                holder.binding.apply {
                    tvMessage.text = message.message
                    tvUsername.text = message.from
                    val dateFormat = SimpleDateFormat("kk:mm:ss", Locale.getDefault())
                    val date = dateFormat.format(message.timestamp)
                    tvTime.text = date
                }
            }

            is OutGoingChatMessageViewHolder -> {
                val message = chatObjects[position] as ChatMessage
                holder.binding.apply {
                    tvMessage.text = message.message
                    tvUsername.text = message.from
                    val dateFormat = SimpleDateFormat("kk:mm:ss", Locale.getDefault())
                    val date = dateFormat.format(message.timestamp)
                    tvTime.text = date
                }
            }


        }
    }

    override fun getItemCount(): Int {
        return chatObjects.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (val obj = chatObjects[position]) {
            is Announcement -> VIEW_TYPE_ANNOUNCEMENT
            is ChatMessage -> {
                if (username == obj.from) {
                    VIEW_TYPE_OUTGOING_MESSAGE
                } else {
                    VIEW_TYPE_INCOMING_MESSAGE
                }
            }
            else -> throw IllegalStateException("Unknown view type")
        }
    }

}