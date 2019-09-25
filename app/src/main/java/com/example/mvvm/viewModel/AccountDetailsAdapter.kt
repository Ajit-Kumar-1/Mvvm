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
import com.example.mvvm.model.AccountEntity
import com.squareup.picasso.Picasso

class AccountDetailsAdapter(private var accountData: MutableList<AccountEntity>?) :
    RecyclerView.Adapter<AccountDetailsAdapter.MyViewHolder>() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var activity: AppCompatActivity
    private lateinit var model: AccountViewModel

    class MyViewHolder(val binding: EntryBinding) : RecyclerView.ViewHolder(binding.root)

    data class Entry(var name: String?, var gender: String?, var email: String?)

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder =
        parent.let {
            activity = it.context as AppCompatActivity
            model = ViewModelProviders.of(activity).get(AccountViewModel::class.java)
            MyViewHolder(EntryBinding.inflate(LayoutInflater.from(activity), parent, false))
        }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int): Unit = holder.run {
        bindData(binding, position)
        if (position == 0) setInitialAccount()
        itemView.let {
            it.setBackgroundColor(highlightColor(position = position))
            it.setOnClickListener {
                model.assignAccountDetails(accountData?.get(position),position)
                setLayoutTitleAndVisibility()
                notifyDataSetChanged()
                recyclerView.scrollToPosition(position)
            }
        }
    }

    override fun getItemCount(): Int = accountData?.size ?: 0

    private fun bindData(binding: EntryBinding, position: Int): Unit = binding.run {
        accountData?.get(position)?.let {
            entry = Entry("${it.firstName} ${it.lastName}", it.gender, it.email)
            Picasso.get().load(it.imageURL).into(avatar)
        }
        executePendingBindings()
    }

    private fun setInitialAccount(): Unit = model.run {
        if (getSelectedItemPosition() == 0 && enableAccountDetailEdit.value == false)
            loadInitialAccount(accountData?.get(0))
    }

    private fun setLayoutTitleAndVisibility(): Unit = activity.run {
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            title = getString(R.string.account_details)
            model.viewDetailsContainerOnPortrait.value = true
        } else {
            title = getString(R.string.accounts)
            model.viewDetailsContainerOnPortrait.value = false
        }
    }

    private fun highlightColor(position: Int): Int = if (model.getSelectedItemPosition() == position
        && activity.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    ) R.color.yellowHighlight else Color.TRANSPARENT

    fun setData(data: MutableList<AccountEntity>) {
        this.accountData = data
        notifyDataSetChanged()
    }

}
