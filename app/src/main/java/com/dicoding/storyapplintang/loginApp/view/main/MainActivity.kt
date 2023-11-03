package com.dicoding.storyapplintang.loginApp.view.main
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapplintang.R
import com.dicoding.storyapplintang.databinding.ActivityMainBinding
import com.dicoding.storyapplintang.loginApp.data.LoginPreference
import com.dicoding.storyapplintang.loginApp.data.remote.story.StoryAdapter
import com.dicoding.storyapplintang.loginApp.view.login.LoginActivity
import com.dicoding.storyapplintang.loginApp.utils.Result
import com.dicoding.storyapplintang.loginApp.view.maps.MapsActivity
import com.dicoding.storyapplintang.loginApp.view.postingStory.PostingActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory.getInstance(this, LoginPreference.getInstance(dataStore))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButton()
    }

    override fun onResume() {
        super.onResume()
        checkSessionValid()
    }

    private fun checkSessionValid() {
        viewModel.checkIfTokenAvailable().observe(this) {
            if (it == "null") {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                setupView("Bearer $it")
            }
        }
    }

    private fun setupView(token: String) {
        viewModel.getStory(token).observe(this) {
            when (it) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    val error = it.error
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                }

                is Result.Success -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    val data = it.data
                    binding.rvStory.apply {
                        adapter = StoryAdapter(this@MainActivity, data)
                        layoutManager = LinearLayoutManager(this@MainActivity)
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.act_logout) {
            viewModel.logout()
        }

        if (item.itemId == R.id.act_map) {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }


    private fun setupButton() {
        binding.btnAdd.setOnClickListener {
            val intent = Intent(this, PostingActivity::class.java)
            startActivity(intent)
        }
    }

}