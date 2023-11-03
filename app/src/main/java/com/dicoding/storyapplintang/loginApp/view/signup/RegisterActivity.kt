package com.dicoding.storyapplintang.loginApp.view.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storyapplintang.R
import com.dicoding.storyapplintang.databinding.ActivitySignupBinding
import com.dicoding.storyapplintang.databinding.ActivitySignupBinding.inflate
import com.dicoding.storyapplintang.loginApp.utils.Result
import com.dicoding.storyapplintang.loginApp.view.login.LoginActivity

class RegisterActivity : AppCompatActivity(), View.OnClickListener, View.OnFocusChangeListener, View.OnKeyListener {

    private val binding: ActivitySignupBinding by lazy {
        inflate(layoutInflater)
    }

    private val registerViewModel: RegisterViewModel by viewModels {
        RegisterViewModelFactory.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.edRegisterName.onFocusChangeListener = this
        binding.edRegisterEmail.onFocusChangeListener = this
        binding.edRegisterPassword.onFocusChangeListener = this
        binding.edRegisterPasswordConfirm.onFocusChangeListener = this
        setupView()
        playAnimation()

    }

    private fun validateName(): Boolean {
        val value = binding.edRegisterName.text.toString()
        if (value.isEmpty()) {
            binding.nameInputLayout.error = ERROR_NAME_REQUIRED
            return false
        }
        binding.nameInputLayout.error = null
        return true
    }

    private fun validateEmail(): Boolean {
        val userEmail = binding.edRegisterEmail.text.toString()
        if (userEmail.isEmpty()) {
            binding.emailInputLayout.error = ERROR_EMAIL_REQUIRED
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            binding.emailInputLayout.error = ERROR_INVALID_EMAIL
            return false
        }
        binding.emailInputLayout.error = null
        return true
    }

    private fun validatePassword(): Boolean {
        val userPassword = binding.edRegisterPassword.text.toString()
        if (userPassword.isEmpty()) {
            binding.passwordInputLayout.error = "Password is Required"
            return false
        }
        if (userPassword.length < 8) {
            binding.passwordInputLayout.error = "Password is Invalid"
            return false
        }
        binding.passwordInputLayout.error = null
        return true
    }

    private fun validateConfirmPassword(): Boolean {
        val userConfirmPassword = binding.edRegisterPasswordConfirm.text.toString()
        if (userConfirmPassword.isEmpty()) {
            binding.passwordConfirmInputLayout.error = "Confirm Password is Required"
            return false
        }
        if (userConfirmPassword.length < 8) {
            binding.passwordConfirmInputLayout.error = "Confirm Password is Invalid"
            return false
        }
        binding.passwordConfirmInputLayout.error = null
        return true
    }

    private fun validatePasswordAndConfirmPassword(): Boolean {
        val userPassword = binding.edRegisterPassword.text.toString()
        val userConfirmPassword = binding.edRegisterPasswordConfirm.text.toString()
        if (userPassword != userConfirmPassword) {
            binding.passwordConfirmInputLayout.error = "Password doesn't match"
            return false
        }
        binding.passwordConfirmInputLayout.error = null
        return true
    }

    private fun setupView() {
        supportActionBar?.title = getString(R.string.register)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.signupButton.setOnClickListener {
            val name = binding.edRegisterName.text
            val email = binding.edRegisterEmail.text
            val password = binding.edRegisterPassword.text
            if (!name.isNullOrEmpty() && !email.isNullOrEmpty() && !password.isNullOrEmpty()) {
                val result = registerViewModel.register(name.toString(), email.toString(), password.toString()
                )
                result.observe(this) {
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
                            Toast.makeText(this, getString(R.string.register_successful), Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }
            } else {
                if (name.isNullOrEmpty()) binding.nameInputLayout.error = getString(R.string.name_cannot_empty)
                if (email.isNullOrEmpty()) binding.emailInputLayout.error = getString(R.string.email_cannot_empty)
                if (email.isNullOrEmpty()) binding.passwordInputLayout.error = getString(R.string.password_minimum)
            }
        }

        binding.loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
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
        val nameEditTextLayout = ObjectAnimator.ofFloat(binding.nameInputLayout, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout = ObjectAnimator.ofFloat(binding.emailInputLayout, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(binding.passwordInputLayout, View.ALPHA, 1f).setDuration(100)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title,
                nameEditTextLayout,
                emailEditTextLayout,
                passwordEditTextLayout,
                signup
            )
            startDelay = 100
        }.start()
    }


    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (view != null) {
            when (view.id) {
                R.id.ed_register_name -> {
                    if (hasFocus) {
                        if (binding.nameInputLayout.isErrorEnabled) {
                            binding.nameInputLayout.isErrorEnabled = false
                        }
                    } else {
                        validateName()
                    }
                }
                R.id.ed_register_email -> {
                    if (hasFocus) {
                        if (binding.emailInputLayout.isErrorEnabled) {
                            binding.emailInputLayout.isErrorEnabled = false
                        }
                    } else {
                        validateEmail()
                    }
                }
                R.id.ed_register_password -> {
                    if (hasFocus) {
                        if (binding.passwordInputLayout.isErrorEnabled) {
                            binding.passwordInputLayout.isErrorEnabled = false
                        }
                    } else {
                        if (validatePassword() && binding.edRegisterPasswordConfirm.text!!.isNotEmpty() &&
                            validateConfirmPassword() && validatePasswordAndConfirmPassword()
                        ) {
                            if (binding.passwordConfirmInputLayout.isErrorEnabled) {
                                binding.passwordConfirmInputLayout.isErrorEnabled = false
                            }

                            if (passwordsMatch()) {
                                binding.passwordConfirmInputLayout.apply {
                                    setStartIconDrawable(R.drawable.ic_checked)
                                    setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
                                }
                            }
                        }
                    }
                }
                R.id.ed_register_passwordConfirm -> {
                    if (hasFocus) {
                        if (binding.passwordConfirmInputLayout.isErrorEnabled) {
                            binding.passwordConfirmInputLayout.isErrorEnabled = false
                        }
                    }
                }
            }
        }
    }

    override fun onKey(view: View?, event: Int, keyEvent: KeyEvent?): Boolean {
        return false
    }

    private fun passwordsMatch(): Boolean {
        val userPassword = binding.edRegisterPassword.text.toString()
        val userConfirmPassword = binding.edRegisterPasswordConfirm.text.toString()
        return userPassword == userConfirmPassword
    }

    companion object {
        private const val ERROR_NAME_REQUIRED = "Name is Required"
        private const val ERROR_EMAIL_REQUIRED = "Email is Required"
        private const val ERROR_INVALID_EMAIL = "Invalid Email Address"
    }

    override fun onClick(p0: View?) {
        TODO("Not yet implemented")
    }
}
