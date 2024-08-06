package com.example.submissionpertamaai.CustomeView
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class PasswordCustomeView: AppCompatEditText {

    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        hint = "Masukkan Password"
        if(text.toString().isNotEmpty()) {
            if (text.toString().length < 8) {

                error = "Password Yang Anda Masukkan Kurang Dari 8 Karakter"
            }
        }
    }


}