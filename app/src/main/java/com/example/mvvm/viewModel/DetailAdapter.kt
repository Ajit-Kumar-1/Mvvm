package com.example.mvvm.viewModel

import android.content.res.Configuration
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvm.R
import com.example.mvvm.databinding.EntryBinding
import com.example.mvvm.model.Entry
import com.example.mvvm.model.StringValues
import com.squareup.picasso.Picasso
import org.json.JSONObject

class DetailAdapter(private val dataArray: ArrayList<String>) :
    RecyclerView.Adapter<DetailAdapter.MyViewHolder>() {

    private val final = StringValues()
    private var recyclerView:RecyclerView? = null

    class MyViewHolder(val binding: EntryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder =
        MyViewHolder(EntryBinding.inflate(LayoutInflater.from(parent.context), parent,
            false))

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.apply {
            val model = ViewModelProviders.of(itemView.context as AppCompatActivity)
                .get(ProfileViewModel::class.java)
            itemView.setBackgroundColor(
                if (model.getPosition() == position &&
                    (itemView.context as AppCompatActivity).
                        resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
                    Color.argb(255, 255, 240, 120)
                else
                    Color.TRANSPARENT
            )

            binding.apply {
                JSONObject(dataArray[position]).let {
                    entry = Entry(
                        "${it.getString(final.FIRST_NAME)} ${it.getString(final.LAST_NAME)}",
                        it.getString(final.GENDER),
                        it.getString(final.EMAIL))
                    Picasso.get().load(it.getJSONObject(final.LINKS).getJSONObject(final.AVATAR)
                        .getString(final.HREF)).into(avatar)
                    executePendingBindings()
                }
            }

            itemView.setOnClickListener { view ->
                (view.context as AppCompatActivity).apply {
                    model.let {
                        it.assignment(position)
                        if (resources.configuration.orientation ==
                            Configuration.ORIENTATION_PORTRAIT) {
                            (this as ActivityCallBack).showDetails()
                            title = getString(R.string.account_details)
                            it.active = true
                        }
                        else {
                            title = getString(R.string.accounts)
                            it.active = false
                        }
                    }
                }
                notifyDataSetChanged()
                recyclerView?.scrollToPosition(position)
            }
        }
    }

    override fun getItemCount(): Int = dataArray.size

}
