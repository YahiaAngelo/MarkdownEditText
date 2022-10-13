package com.yahiaangelo.markdownedittext

import android.app.Activity
import android.text.SpannableStringBuilder
import androidx.core.view.children
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController

//Must run with java9
@RunWith(RobolectricTestRunner::class)
class MarkdownEditTextTest {

    private lateinit var activityController: ActivityController<Activity>
    private lateinit var activity: Activity
    private lateinit var markdownEditText: MarkdownEditText
    private lateinit var markdownStylesBar: MarkdownStylesBar
    private val testText = "Test test testTest 1234 \n" +
            " 1234 testTest test Test Test test testTest 1234 \n" +
            " 1234 testTest test Test Test test testTest 1234 \n" +
            " 1234 testTest test Test Test test testTest 1234 \n" +
            " 1234 testTest test Test"
    @Before
    fun setup(){
        activityController = Robolectric.buildActivity(Activity::class.java)
        activity = activityController.get()

        markdownEditText = MarkdownEditText(activity)
        markdownStylesBar = MarkdownStylesBar(activity)
    }

    @Test
    fun stylesBarTest(){
        markdownEditText.setStylesBar(markdownStylesBar)
        for(stylesButton in markdownStylesBar.children){
            stylesButton.performClick()
            for (char in testText.toCharArray()){
                markdownEditText.append("$char")
            }
        }
    }

    @Test
    fun markdownExportTest(){
        markdownEditText.setStylesBar(markdownStylesBar)
        for(stylesButton in markdownStylesBar.children){
            stylesButton.performClick()
            for (char in testText.toCharArray()){
                markdownEditText.append("$char")
                markdownEditText.text = SpannableStringBuilder(markdownEditText.getMD())
                markdownEditText.renderMD()
            }
        }
    }
}