package com.yahiaangelo.markdownedittext

import android.content.Context
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.getSpans
import com.google.android.material.button.MaterialButton
import com.yahiaangelo.markdownedittext.model.EnhancedMovementMethod
import io.noties.markwon.Markwon
import io.noties.markwon.core.MarkwonTheme
import io.noties.markwon.core.spans.EmphasisSpan
import io.noties.markwon.core.spans.HeadingSpan
import io.noties.markwon.core.spans.StrongEmphasisSpan

class MarkdownEditText : AppCompatEditText {

    private var markwon: Markwon
    private var textWatcher: TextWatcher? = null
    private var markdownStylesBar: MarkdownStylesBar? = null
    private var isSelectionStyling = false
    private val textWatchers: MutableList<TextWatcher> = emptyList<TextWatcher>().toMutableList()
    private var markDownTheme: MarkwonTheme
    var onCopyPasteListener: OnCopyPasteListener? = null

    constructor(context: Context) : super(context, null) {
        markwon = markwonBuilder(context)
        markDownTheme = MarkwonTheme.create(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, R.attr.editTextStyle) {
        markwon = markwonBuilder(context)
        markDownTheme = MarkwonTheme.create(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        markwon = markwonBuilder(context)
        markDownTheme = MarkwonTheme.create(context)
    }

    private fun markwonBuilder(context: Context): Markwon {
        movementMethod = EnhancedMovementMethod().getsInstance()
        return Markwon.builder(context).build()
    }

    fun setStylesBar(stylesBar: MarkdownStylesBar) {
        stylesBar.markdownEditText = this
        this.markdownStylesBar = stylesBar
    }

    fun triggerStyle(textStyle: TextStyle, stop: Boolean) {
        if (stop) {
            clearTextWatchers()
        } else {
            if (isSelectionStyling) {
                styliseText(textStyle, selectionStart, selectionEnd)
                isSelectionStyling = false
            } else {
                textWatcher = object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {}

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int,
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int,
                    ) {

                        if (before < count) {
                            styliseText(textStyle, start)
                        }
                    }
                }
                addTextWatcher(textWatcher!!)
            }
        }
    }

    private fun styliseText(
        textStyle: TextStyle,
        start: Int,
    ) {

        when (textStyle) {
            TextStyle.BOLD -> {
                text!!.setSpan(StrongEmphasisSpan(), start, start + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            TextStyle.ITALIC -> {
                text!!.setSpan(EmphasisSpan(), start, start + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            TextStyle.HEADER -> {
                text!!.setSpan(HeadingSpan(markDownTheme, 2), start, start + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }

    private fun styliseText(
        textStyle: TextStyle,
        start: Int,
        end: Int,
    ) {
        when (textStyle) {
            TextStyle.BOLD -> {
                text!!.setSpan(StrongEmphasisSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            TextStyle.ITALIC -> {
                text!!.setSpan(EmphasisSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            TextStyle.HEADER -> {
                text!!.setSpan(HeadingSpan(markDownTheme, 2), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }

    enum class TextStyle {
        BOLD, ITALIC, HEADER
    }

    fun getMD(): String {
        clearTextWatchers()
        var mdText = text
        val startList = emptyList<Int>().toMutableList()
        val endList = emptyList<Int>().toMutableList()
        var i = 0

        filterSpans()
        for ((index, span) in text!!.getGivenSpans(span = TextStyle.values()).withIndex()) {
            val start = text!!.getSpanStart(span)
            val end = text!!.getSpanEnd(span)
            startList.add(index, start)
            endList.add(index, end)
        }

        for ((index, start) in startList.sorted().withIndex()) {
            val end = endList.sorted()[index]
            val spannedText = end.let { text!!.substring(start, it) }
            val span = end.let { text!!.getGivenSpansAt(span = TextStyle.values(), start, it) }

            span.forEach { selectedSpan ->
                if (spannedText.isNotEmpty() && spannedText != "\n" && spannedText != " ") {
                    when (selectedSpan) {
                        is StrongEmphasisSpan -> {
                            val mdString = "**$spannedText**"
                            mdText = SpannableStringBuilder(mdText!!.replaceRange(start + i, end + i, mdString))
                            i += 4
                        }
                        is EmphasisSpan -> {
                            val mdString = "_${spannedText}_"
                            mdText = SpannableStringBuilder(mdText!!.replaceRange(start + i, end + i, mdString))
                            i += 2
                        }
                    }
                }
            }
        }
        return mdText.toString().replace("****", "").replace("__", "")
    }

    private fun filterSpans() {
        val spans = text?.getGivenSpans(span = arrayOf(TextStyle.BOLD, TextStyle.ITALIC))

        if (spans != null) {
            for (span in spans) {
                val selectedSpans =
                    text?.getGivenSpansAt(span = arrayOf(span), text?.getSpanStart(span)!!, text?.getSpanEnd(span)!!)
                if (selectedSpans!!.size > 1) {
                    var smallSpanIndex = 0
                    var spanSize: Int? = null
                    for ((index, selectedSpan) in selectedSpans.withIndex()) {
                        if (text?.getSpanStart(selectedSpan) != null) {
                            val spanStart = text?.getSpanStart(selectedSpan)
                            val spanEnd = text?.getSpanEnd(selectedSpan)!!
                            if (spanSize == null) {
                                spanSize = spanEnd - spanStart!!
                                smallSpanIndex = index
                            } else {
                                if (spanEnd - spanStart!! < spanSize) {
                                    spanSize = spanEnd - spanStart
                                    smallSpanIndex = index
                                }
                            }
                        }
                    }
                    text?.removeSpan(selectedSpans[smallSpanIndex])
                }
            }
        }
    }

    private fun Editable.getGivenSpans(vararg span: TextStyle): MutableList<Any> {
        val spanList = emptyArray<Any>().toMutableList()
        for (selectedSpan in span) {
            when (selectedSpan) {
                TextStyle.BOLD -> {
                    this.getSpans<StrongEmphasisSpan>().forEach {
                        spanList.add(it)
                    }
                }
                TextStyle.ITALIC -> {
                    this.getSpans<EmphasisSpan>().forEach {
                        spanList.add(it)
                    }
                }
            }
        }
        return spanList
    }

    private fun Editable.getGivenSpansAt(
        vararg span: Any,
        start: Int,
        end: Int,
    ): MutableList<Any> {
        val spanList = emptyArray<Any>().toMutableList()
        for (selectedSpan in span) {
            this.getSpans(start, end, selectedSpan::class.java).forEach {
                spanList.add(it)
            }
        }
        return spanList
    }

    private fun Editable.getGivenSpansAt(
        vararg span: TextStyle,
        start: Int,
        end: Int,
    ): MutableList<Any> {
        val spanList = emptyArray<Any>().toMutableList()
        for (selectedSpan in span) {
            when (selectedSpan) {
                TextStyle.BOLD -> {
                    this.getSpans<StrongEmphasisSpan>(start, end).forEach {
                        spanList.add(it)
                    }
                }
                TextStyle.ITALIC -> {
                    this.getSpans<EmphasisSpan>(start, end).forEach {
                        spanList.add(it)
                    }
                }
            }
        }
        return spanList
    }

    private var selectedButtonId: Int? = null

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        if (selStart == selEnd && markdownStylesBar != null && selStart > 0) {
            val selectedSpans = text!!.getGivenSpansAt(span = arrayOf(TextStyle.BOLD, TextStyle.ITALIC),
                start = selStart - 1,
                end = selStart)
            if (selectedSpans.size > 0) {
                for (span in selectedSpans.distinctBy { it.javaClass }) {
                    when (span) {
                        is StrongEmphasisSpan -> {
                            val boldButton = markdownStylesBar!!.getViewWithId(R.id.style_button_bold) as MaterialButton?
                            if (boldButton != null && !boldButton.isChecked) {
                                boldButton.isChecked = true
                            }
                            selectedButtonId = boldButton?.id
                        }
                        is EmphasisSpan -> {
                            val italicButton = markdownStylesBar!!.getViewWithId(R.id.style_button_italic) as MaterialButton?
                            if (italicButton != null && !italicButton.isChecked) {
                                italicButton.isChecked = true
                            }
                            selectedButtonId = italicButton?.id
                        }
                    }
                }
            } else {
                if (selectedButtonId != null) {
                    val button = markdownStylesBar!!.getViewWithId(selectedButtonId!!) as MaterialButton?
                    if (button != null && button.isChecked) {
                        button.isChecked = false
                    }
                }
            }
        } else if (selStart != selEnd && markdownStylesBar != null) {
            isSelectionStyling = true
        }
    }

    private fun addTextWatcher(textWatcher: TextWatcher) {
        textWatchers.add(textWatcher)
        addTextChangedListener(textWatcher)
    }

    private fun clearTextWatchers() {
        for (textWatcher in textWatchers) {
            removeTextChangedListener(textWatcher)
        }
    }

    override fun onTextContextMenuItem(id: Int): Boolean {
        when (id) {
            android.R.id.cut -> onCut()
            android.R.id.copy -> onCopy()
            android.R.id.paste -> onPaste()

        }
        return super.onTextContextMenuItem(id)
    }

    fun onCut() {
        onCopyPasteListener?.onCut()
    }

    fun onCopy() {
        onCopyPasteListener?.onCopy()
    }

    fun onPaste() {
        onCopyPasteListener?.onPaste()
    }

    //https://gist.github.com/guillermomuntaner/82491cbf0c88dec560a5
    interface OnCopyPasteListener {
        fun onCut()
        fun onCopy()
        fun onPaste()
    }

    //Renders md text in editText
    fun renderMD() {
        this.text = SpannableStringBuilder(markwon.toMarkdown(text.toString()))
    }

    //Renders given md string
    fun renderMD(md: String) {
        this.text = SpannableStringBuilder(markwon.toMarkdown(md))
    }
}
