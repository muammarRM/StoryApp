package com.dicoding.storyapp.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.dicoding.storyapp.databinding.ActivityLoginBinding
import com.dicoding.storyapp.view.ViewModelFactory
import com.dicoding.storyapp.view.main.MainActivity
import com.dicoding.storyapp.view.mycustomview.MyButton
import com.dicoding.storyapp.view.mycustomview.MyEditText

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding
    private var isDialogShown = false

    private lateinit var myButton: MyButton
    private lateinit var emailEditText: MyEditText
    private lateinit var passwordEditText: MyEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        myButton = binding.loginButton
        emailEditText = binding.edLoginEmail
        passwordEditText = binding.edLoginPassword

        setupView()
        setupAction()
        playAnimation()
        setupValidation()
        observeLoginStatus()
        observeLoadingState()
        setMyButtonEnable()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            viewModel.login(email, password)
        }
    }
    private fun setupValidation() {
        // Email validation
        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null && !android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                    emailEditText.error = "Email tidak valid"
                } else {
                    emailEditText.error = null
                }
                setMyButtonEnable()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Password validation
        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null && s.length < 8) {
                    passwordEditText.error = "Password tidak boleh kurang dari 8 karakter"
                } else {
                    passwordEditText.error = null
                }
                setMyButtonEnable()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
    private fun setMyButtonEnable() {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        myButton.isEnabled = email.isNotEmpty() && password.isNotEmpty() &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                password.length >= 8
    }
    private fun observeLoginStatus() {
        viewModel.loginStatus.observe(this, Observer { status ->
            when (status) {
                "success" -> {
                    if (!isFinishing && !isDestroyed) {
                        showSuccessDialog()
                    }
                }
                "error" -> {
                    viewModel.loginErrorMessage.observe(this, Observer { errorMessage ->
                        if (!isFinishing && !isDestroyed) {
                            showErrorDialog(errorMessage)
                        }
                    })
                }
            }
        })
    }

    private fun showSuccessDialog() {
        if (isDialogShown) return
        isDialogShown = true
        AlertDialog.Builder(this).apply {
            setTitle("Yeah!")
            setMessage("Anda berhasil login. Sudah tidak sabar untuk belajar ya?")
            setPositiveButton("Lanjut") { _, _ ->
                val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
            create()
            show()
            resetDialogState()
        }
    }
    private fun showErrorDialog(errorMessage: String?) {
        if (isDialogShown) return
        isDialogShown = true
        AlertDialog.Builder(this).apply {
            setTitle("Oops!")
            setMessage("Terjadi kesalahan: $errorMessage")
            setPositiveButton("Tutup") { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
            resetDialogState()
        }
    }

    private fun resetDialogState() {
        Handler(Looper.getMainLooper()).postDelayed({
            isDialogShown = false
        }, 500)
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val message =
            ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                login
            )
            startDelay = 100
        }.start()
    }

    private fun observeLoadingState() {
        viewModel.isLoading.observe(this, Observer { isLoading ->
            showLoading(isLoading)
        })
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}