package com.yahiaangelo.markdownedittext

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yahiaangelo.markdownedittext.adapter.StylesBarAdapter
import com.yahiaangelo.markdownedittext.model.StyleButton

class MarkdownStylesBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var stylesBarAdapter: StylesBarAdapter
    private var styleButtons: ArrayList<StyleButton>
    private var recyclerView: RecyclerView = RecyclerView(context)
    var markdownEditText: MarkdownEditText? = null
        set(value) {
            stylesBarAdapter.markdownEditText = value
            field = value
        }

    init {
        val linearLayoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        recyclerView.layoutManager = linearLayoutManager
        styleButtons = ArrayList()
        styleButtons.add(StyleButton(R.drawable.ic_format_bold, R.id.style_button_bold))
        styleButtons.add(StyleButton(R.drawable.ic_format_italic, R.id.style_button_italic))
        styleButtons.add(StyleButton(R.drawable.ic_format_strikethrough, R.id.style_button_strike))
        styleButtons.add(StyleButton(R.drawable.ic_format_list_bulleted, R.id.style_button_unordered_list))
        styleButtons.add(StyleButton(R.drawable.ic_format_list_numbered, R.id.style_button_ordered_list))
        styleButtons.add(StyleButton(R.drawable.ic_check_box, R.id.style_button_task_list))
        stylesBarAdapter = StylesBarAdapter(styleButtons)
        recyclerView.adapter = stylesBarAdapter

        val a = context.obtainStyledAttributes(attrs, R.styleable.MarkdownStylesBar)
        val buttonColorStateList = a.getColorStateList(R.styleable.MarkdownStylesBar_buttonColor)
        if (buttonColorStateList != null){
            stylesBarAdapter.buttonsColor = buttonColorStateList
        }
        a.recycle()
        addView(recyclerView)
        
    }

    fun getViewWithId(id: Int): View{
        return recyclerView.findViewById(id)
    }

    }
