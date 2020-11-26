package com.yahiaangelo.markdownedittext

import android.content.Context
import android.text.*
import android.text.style.ClickableSpan
import android.text.style.QuoteSpan
import android.text.style.StrikethroughSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.getSpans
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.yahiaangelo.markdownedittext.model.EnhancedMovementMethod
import io.noties.markwon.*
import io.noties.markwon.core.spans.*
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tasklist.TaskListDrawable
import io.noties.markwon.ext.tasklist.TaskListItem
import io.noties.markwon.ext.tasklist.TaskListPlugin
import io.noties.markwon.ext.tasklist.TaskListSpan
import org.commonmark.node.SoftLineBreak

class MarkdownEditText : AppCompatEditText {

    private var markwon: Markwon
    private var textWatcher: TextWatcher? = null
    private var markdownStylesBar: MarkdownStylesBar? = null
    private var isSelectionStyling = false
    var taskBoxColor: Int = ResourcesCompat.getColor(resources, R.color.primary, context.theme)
    var taskBoxBackgroundColor: Int =
        ResourcesCompat.getColor(resources, R.color.icon, context.theme)

    private val textWatchers: MutableList<TextWatcher> = emptyList<TextWatcher>().toMutableList()

    constructor(context: Context) : super(context, null) {
        markwon = markwonBuilder(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(
        context,
        attrs,
        R.attr.editTextStyle
    ) {
        markwon = markwonBuilder(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        markwon = markwonBuilder(context)
    }


    private fun markwonBuilder(context: Context): Markwon {
        movementMethod = EnhancedMovementMethod().getsInstance()
        return Markwon.builder(context)
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(TaskListPlugin.create(taskBoxColor, taskBoxColor, taskBoxBackgroundColor))
            .usePlugin(object : AbstractMarkwonPlugin() {
                override fun configureVisitor(builder: MarkwonVisitor.Builder) {
                    super.configureVisitor(builder)
                    builder.on(
                        SoftLineBreak::class.java
                    ) { visitor, _ -> visitor.forceNewLine() }
                }

                override fun configureSpansFactory(builder: MarkwonSpansFactory.Builder) {
                    val origin = builder.getFactory(TaskListItem::class.java)

                    builder.setFactory(
                        TaskListItem::class.java
                    ) { configuration, props ->
                        val span = origin?.getSpans(configuration, props)

                        if (span !is TaskListSpan) {
                            null
                        } else {
                            val taskClick = object : ClickableSpan() {
                                override fun onClick(widget: View) {
                                    span.isDone = !span.isDone
                                    text?.setSpan(
                                        span,
                                        text?.getSpanStart(span)!!,
                                        text?.getSpanEnd(span)!!,
                                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                    )
                                }

                                override fun updateDrawState(ds: TextPaint) {
                                }
                            }
                            arrayOf(span, taskClick)
                        }
                    }


                }
            })
            .build()
    }

    fun setStylesBar(stylesBar: MarkdownStylesBar) {
        stylesBar.markdownEditText = this
        this.markdownStylesBar = stylesBar
    }

    fun triggerStyle(textStyle: TextStyle, stop: Boolean) {
        if (stop) {
            clearTextWatchers()
        } else {
            when(textStyle){
                TextStyle.UNORDERED_LIST -> triggerUnOrderedListStyle(stop)
                TextStyle.ORDERED_LIST -> triggerOrderedListStyle(stop)
                TextStyle.TASKS_LIST -> triggerTasksListStyle(stop)
                else-> {
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
                                after: Int
                            ) {
                            }

                            override fun onTextChanged(
                                s: CharSequence?,
                                start: Int,
                                before: Int,
                                count: Int
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



        }


    }

    fun triggerUnOrderedListStyle(stop: Boolean) {
        if (stop) {
            clearTextWatchers()
        } else {
            val currentLineStart = layout.getLineStart(getCurrentCursorLine())
            if (text!!.length < currentLineStart + 1 || text!!.getGivenSpansAt(
                    span = arrayOf(
                        TextStyle.UNORDERED_LIST
                    ), currentLineStart, currentLineStart + 1
                ).isEmpty()
            ) {
                if (text!!.isNotEmpty()) {
                    if (text!!.length > 1 && text!!.getGivenSpansAt(
                            span = arrayOf(
                                TextStyle.ORDERED_LIST,
                                TextStyle.TASKS_LIST,
                            ), selectionStart - 2, selectionStart
                        ).isEmpty()
                    ) {
                        if (text.toString().substring(text!!.length - 2, text!!.length) != "\n") {
                            text!!.insert(selectionStart, "\n ")
                        } else {
                            text!!.insert(selectionStart," ")
                        }
                    } else {
                        text!!.insert(selectionStart,"\n ")
                    }

                } else {
                    text!!.insert(selectionStart," ")
                }


                text!!.setSpan(
                    BulletListItemSpan(markwon.configuration().theme(), 0),
                    selectionStart - 1,
                    selectionStart,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }


            addTextWatcher(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (before < count) {
                        if (selectionStart == selectionEnd) {
                            val string = text.toString()
                            if (string.length > 1 && string[selectionStart - 1] == '\n') {
                                text!!.insert(selectionStart, " ")
                                text!!.setSpan(
                                    BulletListItemSpan(markwon.configuration().theme(), 0),
                                    selectionStart - 1,
                                    selectionStart,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                            }
                        }

                    }

                }

            })

        }


    }

    fun triggerOrderedListStyle(stop: Boolean) {
        if (stop) {
            clearTextWatchers()
        } else {
            var currentNum = 1
            if (text!!.isNotEmpty()) {
                if (text!!.length > 1 && text!!.getGivenSpansAt(
                        span = arrayOf(
                            TextStyle.TASKS_LIST,
                            TextStyle.UNORDERED_LIST,
                        ), text!!.length - 2, text!!.length
                    ).isEmpty()
                ) {
                    if (text.toString().substring(text!!.length - 2, text!!.length) != "\n") {
                        text!!.append("\n  ")
                    } else {
                        text!!.append("  ")
                    }
                } else {
                    text!!.append("\n  ")
                }

            } else {
                text!!.append("  ")
            }

            text!!.setSpan(
                OrderedListItemSpan(markwon.configuration().theme(), "${currentNum}-"),
                text!!.length - 2,
                text!!.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            currentNum++

            addTextWatcher(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (before < count) {
                        val string = text.toString()
                        if (string.isNotEmpty() && string[string.length - 1] == '\n') {
                            text!!.append("  ")
                            text!!.setSpan(
                                OrderedListItemSpan(
                                    markwon.configuration().theme(),
                                    "${currentNum}-"
                                ),
                                text!!.length - 2,
                                text!!.length,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                            currentNum++
                        }
                    }
                }

            })
        }
    }

    fun triggerTasksListStyle(stop: Boolean) {

        if (stop) {
            clearTextWatchers()
        } else {
            val currentLineStart = layout.getLineStart(getCurrentCursorLine())
            if (text!!.length < currentLineStart + 1 || text!!.getGivenSpansAt(
                    span = arrayOf(
                        TextStyle.TASKS_LIST
                    ), currentLineStart, currentLineStart + 1
                ).isEmpty()
            ) {
                if (text!!.isNotEmpty()) {
                    if (text!!.length > 1 && text!!.getGivenSpansAt(
                            span = arrayOf(
                                TextStyle.ORDERED_LIST,
                                TextStyle.UNORDERED_LIST,
                            ), selectionStart - 2, selectionStart
                        ).isEmpty()
                    ) {
                        if (text.toString().substring(text!!.length - 2, text!!.length) != "\n") {
                            text!!.insert(selectionStart,"\n ")
                        } else {
                            text!!.insert(selectionStart," ")
                        }
                    } else {
                        text!!.insert(selectionStart,"\n ")
                    }

                } else {
                    text!!.insert(selectionStart," ")
                }
                setTaskSpan(
                    selectionStart - 1,
                    selectionStart, false
                )
            }



            addTextWatcher(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                    if (before < count) {
                        if (selectionStart == selectionEnd) {
                            val string = text.toString()
                            if (string.length > 1 && string[selectionStart - 1] == '\n') {
                                text!!.insert(selectionStart, " ")
                                setTaskSpan(
                                    selectionStart - 1,
                                    selectionStart, false
                                )
                            }
                        }
                    }

                }

            })

        }
    }

    fun showInsertLinkDialog() {
        val textInputView = LayoutInflater.from(context).inflate(R.layout.link_input_layout, null)
        MaterialAlertDialogBuilder(context)
            .setView(textInputView)
            .setPositiveButton(
                "Add"
            ) { _, _ ->
                val title =
                    textInputView.findViewById<TextInputEditText>(R.id.link_input_title).text
                val url = textInputView.findViewById<TextInputEditText>(R.id.link_input_url).text
                if (!url.isNullOrEmpty()) {
                    addLinkSpan(title.toString(), url.toString())
                }
            }
            .setNegativeButton("Cancel") { _, _ -> }
            .show()


    }

    private fun addLinkSpan(title: String?, link: String) {
        val title1 = if (title.isNullOrEmpty()) link else title
        if (selectionStart == selectionEnd) {
            val cursorStart = selectionStart
            text!!.insert(cursorStart, title1)
            text!!.setSpan(
                LinkSpan(markwon.configuration().theme(), link, LinkResolverDef()),
                cursorStart,
                cursorStart + title1.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    private fun setTaskSpan(start: Int, end: Int, isDone: Boolean) {
        val taskSpan = TaskListSpan(
            markwon.configuration().theme(),
            TaskListDrawable(taskBoxColor, taskBoxColor, taskBoxBackgroundColor),
            isDone
        )
        text!!.setSpan(
            taskSpan,
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        text?.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val spanStart = text?.getSpanStart(taskSpan)
                val spanEnd = text?.getSpanEnd(taskSpan)
                taskSpan.isDone = !taskSpan.isDone
                if (spanStart != null && spanEnd != null) {
                    text!!.setSpan(
                        taskSpan,
                        spanStart,
                        spanEnd,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }

            }

            override fun updateDrawState(ds: TextPaint) {

            }
        }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }


    private fun styliseText(
        textStyle: TextStyle,
        start: Int
    ) {
        when (textStyle) {
            TextStyle.BOLD -> {
                text!!.setSpan(
                    StrongEmphasisSpan(),
                    start,
                    start + 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            TextStyle.ITALIC -> {
                text!!.setSpan(
                    EmphasisSpan(),
                    start,
                    start + 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            TextStyle.STRIKE -> {
                text!!.setSpan(
                    StrikethroughSpan(),
                    start,
                    start + 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

            }
            else -> {
            }
        }


    }

    private fun styliseText(
        textStyle: TextStyle,
        start: Int,
        end: Int
    ) {
        when (textStyle) {
            TextStyle.BOLD -> {
                text!!.setSpan(
                    StrongEmphasisSpan(),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            TextStyle.ITALIC -> {
                text!!.setSpan(
                    EmphasisSpan(),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            TextStyle.STRIKE -> {
                text!!.setSpan(
                    StrikethroughSpan(),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

            }
            else -> {
            }
        }


    }

    enum class TextStyle {
        BOLD,
        ITALIC,
        STRIKE,
        QUOTE,
        LINK,
        UNORDERED_LIST,
        ORDERED_LIST,
        TASKS_LIST
    }

    fun getMD(): String {
        clearTextWatchers()
        var mdText = text
        val startList = emptyList<Int>().toMutableList()
        val endList = emptyList<Int>().toMutableList()
        var i = 0
        val appliedListSpans = mutableListOf<Int>()

        filterSpans()
        for ((index, span) in text!!.getGivenSpans(
            span = TextStyle.values()
        ).withIndex()) {
            val start = text!!.getSpanStart(span)
            val end = text!!.getSpanEnd(span)
            startList.add(index, start)
            endList.add(index, end)
        }

        for ((index, start) in startList.sorted().withIndex()) {
            val end = endList.sorted()[index]
            val spannedText = end.let { text!!.substring(start, it) }
            val span = end.let {
                text!!.getGivenSpansAt(
                    span = TextStyle.values(), start, it
                )
            }

            for (selectedSpan in span) {


                if (selectedSpan is BulletListItemSpan) {
                    if (!appliedListSpans.contains(start)) {
                        val mdString = "* $spannedText"
                        mdText = SpannableStringBuilder(
                            mdText!!.replaceRange(
                                start + i,
                                end + i,
                                mdString
                            )
                        )
                        i += 2
                        appliedListSpans.add(start)
                    }

                } else if (selectedSpan is TaskListSpan) {
                    if (!appliedListSpans.contains(start)) {

                        val mdString =
                            if (selectedSpan.isDone) "* [x] $spannedText" else "* [ ] $spannedText"
                        mdText = SpannableStringBuilder(
                            mdText!!.replaceRange(
                                start + i,
                                end + i,
                                mdString
                            )
                        )
                        i += 6
                        appliedListSpans.add(start)
                    }
                } else {
                    if (spannedText.length > 1) {
                        when (selectedSpan) {
                            is StrongEmphasisSpan -> {
                                val mdString = "**$spannedText**"
                                mdText = SpannableStringBuilder(
                                    mdText!!.replaceRange(
                                        start + i,
                                        end + i,
                                        mdString
                                    )
                                )
                                i += 4
                            }
                            is EmphasisSpan -> {
                                val mdString = "_${spannedText}_"
                                mdText = SpannableStringBuilder(
                                    mdText!!.replaceRange(
                                        start + i,
                                        end + i,
                                        mdString
                                    )
                                )
                                i += 2
                            }
                            is StrikethroughSpan -> {
                                val mdString = "~~$spannedText~~"
                                mdText = SpannableStringBuilder(
                                    mdText!!.replaceRange(
                                        start + i,
                                        end + i,
                                        mdString
                                    )
                                )
                                i += 4
                            }
                            is OrderedListItemSpan -> {
                                val mdString = "${selectedSpan.number}$spannedText"
                                mdText = SpannableStringBuilder(
                                    mdText!!.replaceRange(
                                        start + i,
                                        end + i,
                                        mdString
                                    )
                                )
                                i += 2
                            }
                            is LinkSpan -> {
                                val mdString = "[$spannedText](${selectedSpan.link})"
                                mdText = SpannableStringBuilder(
                                    mdText!!.replaceRange(
                                        start + i,
                                        end + i,
                                        mdString
                                    )
                                )
                                i += 4 + (selectedSpan.link.length - spannedText.length)
                            }

                        }
                    }

                }

            }

        }
        return mdText.toString()
    }

    private fun filterSpans() {
        val spans = text?.getGivenSpans(
            span = arrayOf(
                TextStyle.BOLD,
                TextStyle.ITALIC,
                TextStyle.STRIKE,
                TextStyle.LINK
            )
        )

        if (spans != null) {
            for (span in spans) {
                val selectedSpans = text?.getGivenSpansAt(
                    span = arrayOf(span),
                    text?.getSpanStart(span)!!,
                    text?.getSpanEnd(span)!!
                )
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

        val listsSpans = text?.getGivenSpans(
            span = arrayOf(
                TextStyle.UNORDERED_LIST,
                TextStyle.TASKS_LIST
            )
        )

        if (!listsSpans.isNullOrEmpty()) {
            for (span in listsSpans) {
                val spanStart = text?.getSpanStart(span)
                val spanEnd = text?.getSpanEnd(span)

                if (spanEnd!! - spanStart!! > 1) {
                    text?.removeSpan(span)
                    text?.setSpan(
                        span,
                        spanStart,
                        spanStart + 1,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
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
                TextStyle.STRIKE -> {
                    this.getSpans<StrikethroughSpan>().forEach {
                        spanList.add(it)
                    }
                }
                TextStyle.QUOTE -> {
                    this.getSpans<QuoteSpan>().forEach {
                        spanList.add(it)
                    }
                }
                TextStyle.UNORDERED_LIST -> {
                    this.getSpans<BulletListItemSpan>().forEach {
                        spanList.add(it)
                    }
                }
                TextStyle.ORDERED_LIST -> {
                    this.getSpans<OrderedListItemSpan>().forEach {
                        spanList.add(it)
                    }
                }
                TextStyle.TASKS_LIST -> {
                    this.getSpans<TaskListSpan>().forEach {
                        spanList.add(it)
                    }
                }
                TextStyle.LINK -> {
                    this.getSpans<LinkSpan>().forEach {
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
        end: Int
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
        end: Int
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
                TextStyle.STRIKE -> {
                    this.getSpans<StrikethroughSpan>(start, end).forEach {
                        spanList.add(it)
                    }
                }
                TextStyle.QUOTE -> {
                    this.getSpans<QuoteSpan>(start, end).forEach {
                        spanList.add(it)
                    }
                }
                TextStyle.UNORDERED_LIST -> {
                    this.getSpans<BulletListItemSpan>(start, end).forEach {
                        spanList.add(it)
                    }
                }
                TextStyle.ORDERED_LIST -> {
                    this.getSpans<OrderedListItemSpan>(start, end).forEach {
                        spanList.add(it)
                    }
                }
                TextStyle.TASKS_LIST -> {
                    this.getSpans<TaskListSpan>(start, end).forEach {
                        spanList.add(it)
                    }
                }
                TextStyle.LINK -> {
                    this.getSpans<LinkSpan>(start, end).forEach {
                        spanList.add(it)
                    }
                }
            }
        }
        return spanList
    }

    private var selectedButtonId: Int? = null

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        if (selStart == selEnd && markdownStylesBar != null && selStart > 0) {
            val currentLineStart = layout.getLineStart(getCurrentCursorLine())
            val listsSpans = text!!.getGivenSpansAt(
                span = arrayOf(
                    TextStyle.UNORDERED_LIST,
                    TextStyle.TASKS_LIST
                ),
                start = currentLineStart, end = currentLineStart + 1
            )
            if (listsSpans.size > 0) {
                when (listsSpans[0]) {
                    is BulletListItemSpan -> {
                        val bulletButton =
                            markdownStylesBar!!.getViewWithId(R.id.style_button_unordered_list) as MaterialButton
                        if (!bulletButton.isChecked) {
                            bulletButton.isChecked = true
                        }
                        selectedButtonId = bulletButton.id
                    }
                    is OrderedListItemSpan -> {
                        val listButton =
                            markdownStylesBar!!.getViewWithId(R.id.style_button_ordered_list) as MaterialButton
                        if (!listButton.isChecked) {
                            listButton.isChecked = true
                        }
                        selectedButtonId = listButton.id
                    }
                    is TaskListSpan -> {
                        val taskButton =
                            markdownStylesBar!!.getViewWithId(R.id.style_button_task_list) as MaterialButton
                        if (!taskButton.isChecked) {
                            taskButton.isChecked = true
                        }
                        selectedButtonId = taskButton.id
                    }
                }
            } else {
                val selectedSpans = text!!.getGivenSpansAt(
                    span = arrayOf(
                        TextStyle.BOLD,
                        TextStyle.ITALIC,
                        TextStyle.STRIKE
                    ),
                    start = selStart - 1, end = selStart
                )
                if (selectedSpans.size > 0) {
                    for (span in selectedSpans.distinctBy { it.javaClass }) {
                        when (span) {
                            is StrongEmphasisSpan -> {
                                val boldButton =
                                    markdownStylesBar!!.getViewWithId(R.id.style_button_bold) as MaterialButton
                                if (!boldButton.isChecked) {
                                    boldButton.isChecked = true
                                }
                                selectedButtonId = boldButton.id
                            }
                            is EmphasisSpan -> {
                                val italicButton =
                                    markdownStylesBar!!.getViewWithId(R.id.style_button_italic) as MaterialButton
                                if (!italicButton.isChecked) {
                                    italicButton.isChecked = true
                                }
                                selectedButtonId = italicButton.id
                            }
                            is StrikethroughSpan -> {
                                val strikeThroughButton =
                                    markdownStylesBar!!.getViewWithId(R.id.style_button_strike) as MaterialButton
                                if (!strikeThroughButton.isChecked) {
                                    strikeThroughButton.isChecked = true
                                }
                                selectedButtonId = strikeThroughButton.id
                            }

                        }
                    }
                } else {
                     if (selectedButtonId != null) {
                         val button =
                             markdownStylesBar!!.getViewWithId(
                                 selectedButtonId!!
                             ) as MaterialButton
                         if (button.isChecked) {
                             button.isChecked = false
                         }
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

    private fun getCurrentCursorLine(): Int {
        return if (selectionStart != -1) layout.getLineForOffset(selectionStart) else -1
    }

    private fun getLineCharPosition(line: Int): Int {
        var chars = 1
        return if (line == 0) {
            0
        } else {
            for (i in 0 until line) {
                chars += text!!.lines()[i].length
            }
            chars
        }
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
