package com.yahiaangelo.markdownedittext.model

import android.text.Selection
import android.text.Spannable
import android.text.method.ArrowKeyMovementMethod
import android.text.method.MovementMethod
import android.text.style.ClickableSpan
import android.view.MotionEvent
import android.widget.TextView

/**
* ArrowKeyMovementMethod does support selection of text but not the clicking of links.
* LinkMovementMethod does support clicking of links but not the selection of text.
* This class adds the link clicking to the ArrowKeyMovementMethod.
* We basically take the LinkMovementMethod onTouchEvent code and remove the line
*      Selection.removeSelection(buffer);
* which deselects all text when no link was found.
 * https://stackoverflow.com/a/23566268
*/
class EnhancedMovementMethod : ArrowKeyMovementMethod() {
    private var sInstance: EnhancedMovementMethod? = null

    fun getsInstance(): MovementMethod? {
        if (sInstance == null) {
            sInstance = EnhancedMovementMethod()
        }
        return sInstance
    }

    override fun onTouchEvent(widget: TextView, buffer: Spannable, event: MotionEvent): Boolean {
        val action = event.action
        if (action == MotionEvent.ACTION_UP ||
            action == MotionEvent.ACTION_DOWN
        ) {
            var x = event.x.toInt()
            var y = event.y.toInt()
            x -= widget.totalPaddingLeft
            y -= widget.totalPaddingTop
            x += widget.scrollX
            y += widget.scrollY
            val layout = widget.layout
            val line = layout.getLineForVertical(y)
            val off = layout.getOffsetForHorizontal(line, x.toFloat())
            val link = buffer.getSpans(
                off, off,
                ClickableSpan::class.java
            )
            if (link.isNotEmpty()) {
                if (action == MotionEvent.ACTION_UP) {
                    if (x < layout.getLineMax(0)){
                        link[0].onClick(widget)
                    }
                } else if (action == MotionEvent.ACTION_DOWN) {
                    Selection.setSelection(
                        buffer,
                        buffer.getSpanStart(link[0]),
                        buffer.getSpanEnd(link[0])
                    )
                }
                return true
            }
        }
        return super.onTouchEvent(widget, buffer, event)
    }
}