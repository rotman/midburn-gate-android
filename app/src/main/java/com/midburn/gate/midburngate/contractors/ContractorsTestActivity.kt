package com.midburn.gate.midburngate.contractors

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.midburn.gate.midburngate.R
import kotlinx.android.synthetic.main.activity_sapak_test.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ContractorsTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sapak_test)

        //region GitHub Demo
        val githubRetro = Retrofit.Builder().baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create()).build()
        val gitHubService = githubRetro.create(GitHubService::class.java)
        githubBtn.setOnClickListener {
            gitHubService.testGet().enqueue(object : Callback<List<TestObj>> {
                override fun onFailure(call: Call<List<TestObj>>?, t: Throwable?) {
                    Snackbar.make(root, "sapak details call failed", Snackbar.LENGTH_SHORT).show()
                    Log.w(this@ContractorsTestActivity.localClassName, t)
                }

                override fun onResponse(call: Call<List<TestObj>>?, response: Response<List<TestObj>>?) {
                    Snackbar.make(root, "SUCCESS!", Snackbar.LENGTH_SHORT).show()
                    Log.i(this@ContractorsTestActivity.localClassName, response?.body()?.toString())
                }

            })
        }
        //endregion
        details_btn.setOnClickListener {
            NetworkApi.getEvents(it.context, object : NetworkApi.Callback<List<String>> {
                override fun onSuccess(response: List<String>) {
                    Snackbar.make(root, "Success!", Snackbar.LENGTH_SHORT).show()
                }

                override fun onFailure(throwable: Throwable) {
                    Snackbar.make(root, "Booooo!", Snackbar.LENGTH_SHORT).show()
                }

            })
        }
    }
}
