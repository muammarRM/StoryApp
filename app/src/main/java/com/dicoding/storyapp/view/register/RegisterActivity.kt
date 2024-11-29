package com.dicoding.storyapp.view.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
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
import com.dicoding.storyapp.databinding.ActivityRegisterBinding
import com.dicoding.storyapp.view.ViewModelFactory
import com.dicoding.storyapp.view.mycustomview.MyButton
import com.dicoding.storyapp.view.mycustomview.MyEditText

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var isDialogShown = false

    private lateinit var myButton: MyButton
    private lateinit var nameEditText: MyEditText
    private lateinit var emailEditText: MyEditText
    private lateinit var passwordEditText: MyEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        myButton = binding.signupButton
        nameEditText = binding.edRegisterName
        emailEditText = binding.edRegisterEmail
        passwordEditText = binding.edRegisterPassword
        setupView()
        setupAction()
        playAnimation()

        setupValidation()
        observeRegisterStatus()
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
        binding.signupButton.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            showLoading(true)
            viewModel.register(name, email, password)
        }
    }
    private fun setupValidation() {
        nameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null && s.isEmpty()) {
                    nameEditText.error = "Nama tidak boleh kosong"
                } else {
                    nameEditText.error = null
                }
                setMyButtonEnable()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
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
        val name = nameEditText.text.toString()
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        myButton.isEnabled = name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                password.length >= 8
    }
    private fun observeRegisterStatus() {
        viewModel.registerStatus.observe(this, Observer { status ->
            showLoading(false)
            when (status) {
                "success" -> {
                    if (!isFinishing && !isDestroyed) {
                        showSuccessDialog()
                    }
                }
                "error" -> {
                    viewModel.registerErrorMessage.observe(this, Observer { errorMessage ->
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
        // Tampilkan AlertDialog untuk sukses
        AlertDialog.Builder(this).apply {
            setTitle("Yeah!")
            setMessage("Akun dengan ${binding.edRegisterEmail.text} sudah jadi nih. Yuk, login dan belajar coding.")
            setPositiveButton("Lanjut") { _, _ ->
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
        // Tampilkan AlertDialog untuk error
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

    fun stopAnimation() {
        binding.imageView.animate().cancel()
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
        val nameTextView =
            ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(100)
        val nameEditTextLayout =
            ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 1f).setDuration(100)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)


        AnimatorSet().apply {
            playSequentially(
                title,
                nameTextView,
                nameEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                signup
            )
            startDelay = 100
        }.start()
    }


    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}