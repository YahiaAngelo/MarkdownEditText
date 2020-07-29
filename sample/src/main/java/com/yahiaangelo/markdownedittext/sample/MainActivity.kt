package com.yahiaangelo.markdownedittext.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import com.yahiaangelo.markdownedittext.MarkdownEditText
import com.yahiaangelo.markdownedittext.MarkdownStylesBar
import com.yahiaangelo.markdownedittext.sample.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.view.*

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