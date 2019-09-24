package com.example.mvvm.viewModel

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvm.R
import com.example.mvvm.databinding.EntryBinding
import com.example.mvvm.model.AccountEntity
import com.squareup.picasso.Picasso

class AccountDetailsAdapter(private var accountData: MutableList<AccountEntity>?) :
    RecyclerView.Adapter<AccountDetailsAdapter.MyViewHolder>() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var context: Context
    private lateinit var model: AccountViewModel

    class MyViewHolder(val binding: EntryBinding) : RecyclerView.ViewHolder(binding.root)

    data class Entry(var name: String?, var gender: String?, var email: String?)

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        (parent.context).let {
            context = it as AppCompatActivity
            model = ViewModelProviders.of(it).get(AccountViewModel::class.java)
            return MyViewHolder(
                EntryBinding.inflate(
                    LayoutInflater.from(it), parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        bindData(binding = holder.binding, position = position)
        holder.itemView.apply {
            setBackgroundColor(highlightColor(position = position))
            setOnClickListener { _ ->
                (context as AppCompatActivity).apply {
                    model.let {
                        it.assignAccountDetails(accountData?.get(position), position)
                        if (resources.configuration.orientation ==
                            Configuration.ORIENTATION_PORTRAIT
                        ) {
                            title = getString(R.string.account_details)
                            it.viewDetailsContainerOnPortrait.value = true
                        } else {
                            title = getString(R.string.accounts)
                            it.viewDetailsContainerOnPortrait.value = false
                        }
                    }
                }
                notifyDataSetChanged()
                recyclerView.scrollToPosition(position)
            }
        }
    }

    override fun getItemCount(): Int = accountData?.size ?: 0

    private fun bindData(binding: EntryBinding, position: Int) {
        binding.apply {
            accountData?.get(position)?.let {
                entry = Entry("${it.firstName} ${it.lastName}", it.gender, it.email)
                Picasso.get().load(it.imageURL).into(avatar)
            }
            executePendingBindings()
        }
    }

    private fun highlightColor(position: Int): Int {
        return if ((context as AppCompatActivity).resources.configuration.orientation
            == Configuration.ORIENTATION_LANDSCAPE
            && model.getRecyclerViewPosition() == position
        )
            R.color.yellowHighlight
        else
            Color.TRANSPARENT
    }

    fun setData(data: MutableList<AccountEntity>) {
        this.accountData = data
        notifyDataSetChanged()
    }

}
