package com.yahiaangelo.markdownedittext

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import com.google.android.material.button.MaterialButton

class MarkdownStylesBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    val horizontalListView = HorizontalListView(context, null)
    private var adapter: StylesBarAdapter
    var markdownEditText: MarkdownEditText? = null
        set(value) {
            adapter.markdownEditText = value
            field = value
        }

    init {
        horizontalListView.setDividerWidth(28)
        val styleButtons = ArrayList<StyleButton>()
        styleButtons.add(StyleButton(R.drawable.ic_format_bold, R.id.style_button_bold))
        styleButtons.add(StyleButton(R.drawable.ic_format_italic, R.id.style_button_italic))
        styleButtons.add(StyleButton(R.drawable.ic_format_strikethrough, R.id.style_button_strike))
        styleButtons.add(
            StyleButton(
                R.drawable.ic_format_list_bulleted,
                R.id.style_button_unordered_list
            )
        )
        styleButtons.add(
            StyleButton(
                R.drawable.ic_format_list_numbered,
                R.id.style_button_ordered_list
            )
        )

        adapter = StylesBarAdapter(styleButtons, context)
        val a = context.obtainStyledAttributes(attrs, R.styleable.MarkdownStylesBar)
        val buttonColorStateList = a.getColorStateList(R.styleable.MarkdownStylesBar_buttonColor)
        if (buttonColorStateList != null){
            adapter.styleButtonColor = buttonColorStateList
        }
        a.recycle()
        horizontalListView.adapter = adapter
        addView(horizontalListView)
        
    }


    class StylesBarAdapter(
        data: ArrayList<StyleButton>,
        context: Context
    ) : ArrayAdapter<StyleButton>(context, R.layout.styles_bar_item, data){
        private var selectedButton: MaterialButton? = null
        var markdownEditText: MarkdownEditText? = null
        var styleButtonColor: ColorStateList? = null
        private var lastPosition = -1
        private lateinit var viewHolder: ViewHolder
        private lateinit var result: View

        private open class ViewHolder(var materialButton: MaterialButton)

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val styleButton = getItem(position)
            if (convertView == null) {
                val inflater = LayoutInflater.from(context)
                result = inflater.inflate(R.layout.styles_bar_item, parent, false)
                viewHolder = ViewHolder(result.findViewById(R.id.style_button))
                result.tag = viewHolder

            } else {
                viewHolder = (convertView.tag as? ViewHolder)!!
                result = convertView
            }
            lastPosition = position
            if (styleButtonColor != null){
                viewHolder.materialButton.backgroundTintList = styleButtonColor
            }
            viewHolder.materialButton.icon = context.getDrawable(styleButton!!.icon)
            viewHolder.materialButton.id = styleButton.id
            viewHolder.materialButton.addOnCheckedChangeListener { button, _ ->
                if (selectedButton != null) {
                    if (selectedButton != button) {
                        styleButtonClick(selectedButton!!)
                        selectedButton!!.isChecked = false
                    }
                    styleButtonClick(button)
                    selectedButton = button
                } else {
                    styleButtonClick(button)
                    selectedButton = button
                }
            }
            viewHolder.materialButton.tag = position
            return result
        }

        private fun styleButtonClick(button: MaterialButton) {
            if (markdownEditText != null) {
                when (button.id) {
                    R.id.style_button_bold -> markdownEditText!!.triggerStyle(
                        MarkdownEditText.TextStyle.BOLD,
                        !button.isChecked
                    )
                    R.id.style_button_italic -> markdownEditText!!.triggerStyle(
                        MarkdownEditText.TextStyle.ITALIC,
                        !button.isChecked
                    )
                    R.id.style_button_strike -> markdownEditText!!.triggerStyle(
                        MarkdownEditText.TextStyle.STRIKE,
                        !button.isChecked
                    )
                    R.id.style_button_unordered_list -> markdownEditText!!.triggerUnOrderedListStyle(
                        !button.isChecked
                    )
                    R.id.style_button_ordered_list -> markdownEditText!!.triggerOrderedListStyle(!button.isChecked)
                }
            }
        }
    }

    class StyleButton(var icon: Int, var id: Int)

}