package com.example.myapplication.touch

import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.TouchDelegate
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.myapplication.R

class TouchExpandActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_touch_expand)

        val childView = findViewById<ConstraintLayout>(R.id.layoutChild).apply {
            setOnClickListener {
                Toast.makeText(this@TouchExpandActivity, "자식 클릭", Toast.LENGTH_LONG).show()
            }
        }

        val parent = findViewById<ConstraintLayout>(R.id.layoutParent)

        parent.post {
            val newHitRect = Rect()
            childView.getHitRect(newHitRect)
            newHitRect.top -= 100 // 100px
            newHitRect.left -= 100
            newHitRect.right += 100
            newHitRect.bottom += 100

            parent.touchDelegate = TouchDelegate(newHitRect, childView)
        }
    }
}