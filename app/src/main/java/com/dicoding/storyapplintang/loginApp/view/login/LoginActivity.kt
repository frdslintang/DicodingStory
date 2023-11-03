package com.dicoding.storyapplintang.loginApp.view.login
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.dicoding.storyapplintang.R
import com.dicoding.storyapplintang.databinding.ActivityLoginBinding
import com.dicoding.storyapplintang.databinding.ActivityLoginBinding.*
import com.dicoding.storyapplintang.loginApp.data.LoginPreference
import com.dicoding.storyapplintang.loginApp.utils.Result
import com.dicoding.storyapplintang.loginApp.view.main.MainActivity
import com.dicoding.storyapplintang.loginApp.view.signup.RegisterActivity
import com.dicoding.storyapplintang.loginApp.view.welcome.WelcomeActivity
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")


class LoginActivity : AppCompatActivity() {

    private val binding: ActivityLoginBinding by lazy {
        inflate(layoutInflater)
    }

    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory.getInstance(
            LoginPreference.getInstance(dataStore)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        initialCheck()
        setupAction()
        playAnimation()

    }

    override fun onResume() {
        super.onResume()
        initialCheck()
    }

    private fun initialCheck() {
        loginViewModel.checkIfFirstTime().observe(this) {
            if (it) {
                val intent = Intent(this, WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            val emailLayout = binding.emailInputLayout
            val passwordLayout = binding.passwordInputLayout

            emailLayout.error = null
            passwordLayout.error = null

            if (email.isNotEmpty() && password.isNotEmpty()) {
                val result = loginViewModel.login(email, password)
                result.observe(this) { loginResult ->
                    when (loginResult) {
                        is Result.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.loginButton.isEnabled = false
                        }

                        is Result.Error -> {
                            binding.progressBar.visibility = View.INVISIBLE
                            val error = loginResult.error
                            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                            binding.loginButton.isEnabled = true
                        }

                        is Result.Success -> {
                            binding.progressBar.visibility = View.INVISIBLE
                            val data = loginResult.data
                            loginViewModel.saveToken(data.token)
                            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                            Log.d("LoginActivity", "Token: ${data.token}")
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            } else {
                if (email.isEmpty()) emailLayout.error = "Email cannot be empty!"
                if (password.isEmpty()) passwordLayout.error =
                    "Password must be at least 8 characters!"
            }
        }

        binding.signupButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }


        binding.signupButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }





    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title,
                login
            )
            startDelay = 100
        }.start()
    }

}