package com.yahiaangelo.markdownedittext.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import com.yahiaangelo.markdownedittext.sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.edittext.setStylesBar(binding.stylesbar)
        binding.toolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.to_md -> binding.edittext.text = SpannableStringBuilder(binding.edittext.getMD())
                R.id.render_md -> binding.edittext.renderMD()
            }
            return@setOnMenuItemClickListener true
        }
    }
}