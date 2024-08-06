package com.example.submissionpertamaai

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.submissionpertamaai.DataStore.UserPreferences
import com.example.submissionpertamaai.DataStore.ViewModelFactory
import com.example.submissionpertamaai.Paging.LoadingStateAdapter
import com.example.submissionpertamaai.Paging.PagingModelFactory
import com.example.submissionpertamaai.Paging.PagingViewModel
import com.example.submissionpertamaai.Paging.StoryListAdapter
import com.example.submissionpertamaai.databinding.ActivityMainBinding
import com.example.submissionpertamaai.location.MapsActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

class MainActivity : AppCompatActivity() {
    private lateinit var rv_liststory: RecyclerView
    private lateinit var MyviewModel: MainViewModel
    private lateinit var pagingModel : PagingViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var builder : AlertDialog.Builder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        rv_liststory = findViewById(R.id.rv_liststory)

        val layoutManager = LinearLayoutManager(this)
        binding.rvListstory.layoutManager = LinearLayoutManager(this)
        binding.rvListstory.setHasFixedSize(true)




        logincheck()






    }



    private fun logincheck() {
        MyviewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferences.getInstance(dataStore))
        )[MainViewModel::class.java]

        MyviewModel.getuser().observe(this){
            if (it.token!=""){
                TOKEN = it.token
                getstory(it.token)

            }
            else{
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }


    }

    private fun logout(){
        MyviewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferences.getInstance(dataStore))
        )[MainViewModel::class.java]

        MyviewModel.logout()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun getstory(token : String) {
        val adapter = StoryListAdapter()
        binding.rvListstory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter{
                adapter.retry()
            }
        )
        pagingModel = ViewModelProvider(
            this,
            PagingModelFactory(this)
        )[PagingViewModel::class.java]

        pagingModel.story.observe(this) {
            adapter.submitData(lifecycle, it)
        }

        OnClickAdapter(adapter,token)
    }



    private fun OnClickAdapter(adapter:StoryListAdapter,token:String){
        val adapterU = adapter
        adapter.setOnItemClickCallback(object : StoryListAdapter.OnItemClickCallback{
            override fun onItemClicked(data: ListStoryItem) {
                if (data != null) {
                    val intent = Intent(this@MainActivity,DetailActivity::class.java)
                    intent.putExtra(DetailActivity.ID,data.id)
                    intent.putExtra(DetailActivity.TOKEN,token)
                    startActivity(intent)
                }
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu1 -> {

                builder = AlertDialog.Builder(this)
                builder.setTitle("Logout")
                    .setMessage("Apakah Anda yakin ingin Logout?")
                    .setCancelable(true)
                    .setPositiveButton("Logout"){dialogInterface,it->
                        logout()
                        finish()
                    }
                    .setNegativeButton("Tidak"){dialogInterface,it->
                        dialogInterface.cancel()

                    }
                    .show()

                return true
            }

            R.id.menu2 ->{
                val intent = Intent(this, UploadStoryActivity::class.java)
                startActivity(intent)

                return true
            }

            R.id.menu3 ->{
                val intent = Intent(this, DetailAccountActivity::class.java)
                startActivity(intent)

                return true
            }

            R.id.menu4 ->{
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)

                return true
            }

            else -> return true
        }
    }

    companion object {
        var TOKEN :String =""
    }

}