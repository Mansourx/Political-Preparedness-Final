package com.example.android.politicalpreparedness.election.adapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.android.politicalpreparedness.models.Election

@BindingAdapter("electionName")
fun TextView.setElectionName(item: Election?){
    item?.let{
        text = item.name
    }
}

@BindingAdapter("electionDatetime")
fun TextView.setElectionDatetime(item: Election?){
    item?.let{
        text = item.electionDay.toString()
    }
}