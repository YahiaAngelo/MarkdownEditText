package com.yahiaangelo.markdownedittext.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.yahiaangelo.markdownedittext.MarkdownEditText
import com.yahiaangelo.markdownedittext.R
import com.yahiaangelo.markdownedittext.model.StyleButton

class StylesBarAdapter(private var buttonsList: ArrayList<StyleButton>) : RecyclerView.Adapter<StylesBarAdapter.MyViewHolder>() {

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

    fun setStyles(newButtonsList: ArrayList<StyleButton>){
        buttonsList = newButtonsList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val styleButtonModel = buttonsList[position]
        if (buttonsColor != null){
            holder.styleButton.backgroundTintList = buttonsColor
        }
        holder.styleButton.icon = holder.itemView.context.getDrawable(styleButtonModel.icon)
        holder.styleButton.id = styleButtonModel.id
        if (styleButtonModel.id == R.id.style_button_link){
            holder.styleButton.isCheckable = false
            holder.styleButton.setOnClickListener {
                styleButtonClick(holder.styleButton)
            }
        }
        holder.styleButton.addOnCheckedChangeListener { button, _ ->
            if (selectedButton != null) {
                if (selectedButton != button) {
                    selectedButton!!.isChecked = false
                    styleButtonClick(selectedButton!!)
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
                R.id.style_button_unordered_list -> markdownEditText!!.triggerStyle(
                    MarkdownEditText.TextStyle.UNORDERED_LIST,
                    !button.isChecked
                )
                R.id.style_button_ordered_list -> markdownEditText!!.triggerStyle(
                    MarkdownEditText.TextStyle.ORDERED_LIST,
                    !button.isChecked)
                R.id.style_button_task_list -> markdownEditText!!.triggerStyle(
                    MarkdownEditText.TextStyle.TASKS_LIST,
                    !button.isChecked)
                R.id.style_button_link -> markdownEditText!!.showInsertLinkDialog()
            }

        }
    }
}
