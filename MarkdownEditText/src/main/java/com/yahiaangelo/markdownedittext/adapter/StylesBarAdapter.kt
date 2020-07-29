package com.yahiaangelo.markdownedittext.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.yahiaangelo.markdownedittext.MarkdownEditText
import com.yahiaangelo.markdownedittext.R
import com.yahiaangelo.markdownedittext.model.StyleButton

class StylesBarAdapter(private val buttonsList: ArrayList<StyleButton>) : RecyclerView.Adapter<StylesBarAdapter.MyViewHolder>() {

    private var selectedButton: MaterialButton? = null
    var buttonsColor: ColorStateList? = null
    var markdownEditText: MarkdownEditText? = null

    class MyViewHolder(val styleButton: MaterialButton) : RecyclerView.ViewHolder(styleButton)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val styleButtonView = LayoutInflater.from(parent.context).inflate(R.layout.styles_bar_item, parent, false) as MaterialButton
        return MyViewHolder(styleButtonView)
    }


    override fun getItemCount(): Int {
       return buttonsList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val styleButtonModel = buttonsList[position]
        if (buttonsColor != null){
            holder.styleButton.backgroundTintList = buttonsColor
        }
        holder.styleButton.icon = holder.itemView.context.getDrawable(styleButtonModel.icon)
        holder.styleButton.id = styleButtonModel.id
        holder.styleButton.addOnCheckedChangeListener { button, _ ->
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
    }

    private fun styleButtonClick(button: MaterialButton) {
        if(markdownEditText != null) {
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
