package com.dicoding.storyapp.view.mycustomview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dicoding.storyapp.R

class MyEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs), View.OnTouchListener {

    private var clearButtonImage: Drawable
    private var customHint: String
    private var inputType: Int = 0
    private var _isValid = MutableLiveData(false)
    val isValid: LiveData<Boolean> get() = _isValid

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.MyEditText)
        customHint = attributes.getString(R.styleable.MyEditText_customHint) ?: ""
        inputType = attributes.getInt(R.styleable.MyEditText_android_inputType, 0)
        attributes.recycle()

        clearButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_close_black_24dp) as Drawable

        setOnTouchListener(this)

        setupTextWatcher()
    }

    private fun setupTextWatcher() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) showClearButton() else hideClearButton()
                validateInput(s.toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun validateInput(text: String) {
        error = when {
            text.isEmpty() -> {
                when (inputType) {
                    INPUT_TYPE_NAME -> "Nama tidak boleh kosong"
                    INPUT_TYPE_EMAIL -> "Email tidak boleh kosong"
                    INPUT_TYPE_PASSWORD -> "Password tidak boleh kosong"
                    INPUT_TYPE_DESCRIPTION -> "Deskripsi tidak boleh kosong"
                    else -> null
                }.also { _isValid.value = false }
            }
            inputType == INPUT_TYPE_EMAIL && !Patterns.EMAIL_ADDRESS.matcher(text).matches() -> {
                "Email tidak valid".also { _isValid.value = false }
            }
            inputType == INPUT_TYPE_PASSWORD && text.length < 8 -> {
                "Password tidak boleh kurang dari 8 karakter".also { _isValid.value = false }
            }
            else -> {
                _isValid.value = true
                null
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = customHint
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun showClearButton() {
        setButtonDrawables(endOfTheText = clearButtonImage)
    }

    private fun hideClearButton() {
        setButtonDrawables()
    }

    private fun setButtonDrawables(startOfTheText: Drawable? = null, topOfTheText:Drawable? = null, endOfTheText:Drawable? = null, bottomOfTheText: Drawable? = null){
        setCompoundDrawablesWithIntrinsicBounds(startOfTheText, topOfTheText, endOfTheText, bottomOfTheText)
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val clearButtonStart: Float
            val clearButtonEnd: Float
            var isClearButtonClicked = false

            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                clearButtonEnd = (clearButtonImage.intrinsicWidth + paddingStart).toFloat()
                when {
                    event.x < clearButtonEnd -> isClearButtonClicked = true
                }
            } else {
                clearButtonStart = (width - paddingEnd - clearButtonImage.intrinsicWidth).toFloat()
                when {
                    event.x > clearButtonStart -> isClearButtonClicked = true
                }
            }
            if (isClearButtonClicked) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        clearButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_close_black_24dp) as Drawable
                        showClearButton()
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        clearButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_close_black_24dp) as Drawable
                        when {
                            text != null -> text?.clear()
                        }
                        hideClearButton()
                        return true
                    }
                    else -> return false
                }
            } else return false
        }
        return false
    }


//    fun isValid(): Boolean {
//        return isValid
//    }
    companion object {
        const val INPUT_TYPE_NAME = 1
        const val INPUT_TYPE_EMAIL = 2
        const val INPUT_TYPE_PASSWORD = 3
        const val INPUT_TYPE_DESCRIPTION = 4
    }

}