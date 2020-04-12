package com.example.covid19tracker

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    lateinit var stateAdapter :StateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        list.addHeaderView(LayoutInflater.from(this).inflate(R.layout.item_header,list,false))

        fetchResults()
    }

    private fun fetchResults() {
        GlobalScope.launch {
            val respose = withContext(Dispatchers.IO) { Client.api.execute() }

            if(respose.isSuccessful){
            val data = Gson().fromJson(respose.body?.string(),Response::class.java)
                launch(Dispatchers.Main) {
                        bindCombinedData(data.statewise[0])
                    bindStatewiseData(data.statewise.subList(0,data.statewise.size))
                }
            }
        }
    }

    private fun bindStatewiseData(subList: List<StatewiseItem>) {
        stateAdapter= StateAdapter(subList)
        list.adapter = stateAdapter


    }


    private fun bindCombinedData(data: StatewiseItem) {
        val lastUpdatedTime  = data.lastupdatedtime
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        var update = "Last Updated\n ${getTimeAgo(simpleDateFormat.parse(lastUpdatedTime))}"
        lastupdatedTv.text = update.toString()

        ConfirmedTv.text = data.confirmed
        deceasedTv.text = data.deaths
        activeTv.text = data.active
        recoveredTv.text = data.recovered
    }


    }

    fun getTimeAgo(past:Date): String {
        val now = Date()
        val seconds = TimeUnit.MILLISECONDS.toSeconds(now.time - past.time)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(now.time - past.time)
        val hours = TimeUnit.MILLISECONDS.toHours(now.time - past.time)

        return when {
            seconds < 60 -> {
                "Few seconds ago"
            }
            minutes < 60 -> {
                "$minutes minutes ago"
            }
            hours < 24 -> {
                "$hours hour ${minutes % 60} min ago"
            }
            else -> {
                SimpleDateFormat("dd/mm/yy, hh:mm a").format(past).toString()
            }
        }
}
