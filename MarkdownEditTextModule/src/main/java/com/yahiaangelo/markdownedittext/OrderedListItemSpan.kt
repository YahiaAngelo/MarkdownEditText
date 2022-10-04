package com.yahiaangelo.markdownedittext

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.Spanned
import android.text.style.LeadingMarginSpan
import android.widget.TextView
import io.noties.markwon.core.MarkwonTheme
import io.noties.markwon.utils.LeadingMarginUtils


class OrderedListItemSpan(
    private val theme: MarkwonTheme,
    val number: String
) : LeadingMarginSpan {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // we will use this variable to check if our order number text exceeds block margin,
    // so we will use it instead of block margin
    // @since 1.0.3
    private var margin = 0
    override fun getLeadingMargin(first: Boolean): Int {
        // @since 2.0.1 we return maximum value of both (now we should measure number before)
        return margin.coerceAtLeast(theme.blockMargin)
    }

    override fun drawLeadingMargin(
        c: Canvas,
        p: Paint,
        x: Int,
        dir: Int,
        top: Int,
        baseline: Int,
        bottom: Int,
        text: CharSequence,
        start: Int,
        end: Int,
        first: Boolean,
        layout: Layout
    ) {

        // if there was a line break, we don't need to draw anything
        if (!first
            || !LeadingMarginUtils.selfStart(start, text, this)
        ) {
            return
        }
        paint.set(p)
        theme.applyListItemStyle(paint)

        // if we could force usage of #measure method then we might want skip this measuring here
        // but this won't hold against new values that a TextView can receive (new text size for
        // example...)
        val numberWidth = (paint.measureText(number) + .5f).toInt()

        // @since 1.0.3
        var width = theme.blockMargin
        if (numberWidth > width) {
            // let's keep this logic here in case a user decided not to call #measure and is fine
            // with current implementation
            width = numberWidth
            margin = numberWidth
        } else {
            margin = 0
        }
        val left: Int
        left = if (dir > 0) {
            x + width * dir - numberWidth
        } else {
            x + width * dir + (width - numberWidth)
        }

        // @since 1.1.1 we are using `baseline` argument to position text
        c.drawText(number, left.toFloat(), baseline.toFloat(), paint)
    }

    companion object {
        /**
         * Process supplied `text` argument and supply TextView paint to all OrderedListItemSpans
         * in order for them to measure number.
         *
         *
         * NB, this method must be called *before* setting text to a TextView (`TextView#setText`
         * internally can trigger new Layout creation which will ask for leading margins right away)
         *
         * @param textView to which markdown will be applied
         * @param text     parsed markdown to process
         * @since 2.0.1
         */
        fun measure(textView: TextView, text: CharSequence) {
            if (text !is Spanned) {
                // nothing to do here
                return
            }
            val spans = text.getSpans(
                0,
                text.length,
                OrderedListItemSpan::class.java
            )
            if (spans != null) {
                val paint = textView.paint
                for (span in spans) {
                    span.margin = (paint.measureText(span.number) + .5f).toInt()
                }
            }
        }
    }

}
