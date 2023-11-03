package com.dicoding.storyapplintang.loginApp.data.remote.story

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.storyapplintang.databinding.ItemPostBinding
import com.dicoding.storyapplintang.loginApp.data.local.Room.Entity
import com.dicoding.storyapplintang.loginApp.view.Detail.DetailActivity
import androidx.core.util.Pair

class StoryAdapter(private val context: Context, private val list: List<Entity>) : RecyclerView.Adapter<StoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tbl_story = list[position]
        holder.bind(tbl_story)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(private val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(tbl_story: Entity) {
            with(binding) {
                tvName.text = tbl_story.name
                tvDescription.text = tbl_story.description

                Glide.with(itemView.context)
                    .load(tbl_story.photoUrl)
                    .into(ivStory)

                cardStory.setOnClickListener {
                    val intent = Intent(itemView.context, DetailActivity::class.java)
                    intent.putExtra(DetailActivity.NameStory,tbl_story.name)
                    intent.putExtra(DetailActivity.DescStory, tbl_story.description)
                    intent.putExtra(DetailActivity.ImageStory, tbl_story.photoUrl)

                    val optionsCompat: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(ivStory, "image"),
                        Pair(tvName, "name"),
                        Pair(tvDescription, "description")
                    )

                    itemView.context.startActivity(intent, optionsCompat.toBundle())
                }
            }
        }
    }
}
