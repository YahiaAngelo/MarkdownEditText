# MarkdownEditText [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.yahiaangelo.markdownedittext/markdownedittext/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.yahiaangelo.markdownedittext/markdownedittext)

A native Rich text editor for android based on [Markwon](https://github.com/noties/Markwon) library with export to Markdown option

---
## Preview
<img src="https://raw.githubusercontent.com/YahiaAngelo/MarkdownEditText/master/preview/preview.gif" width="310">

---

## Usage:

### Adding the depencency
Add the dependency to your app build.gradle file:
```
implementation 'io.github.yahiaangelo.markdownedittex:markdownedittext:$latestVersion'
```
### XML
```xml
    <com.yahiaangelo.markdownedittext.MarkdownEditText
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:layout_constraintTop_toBottomOf="@id/appbar"
        android:id="@+id/edittext"
        android:gravity="top"/>

    <com.yahiaangelo.markdownedittext.MarkdownStylesBar
        android:layout_width="match_parent"
        android:layout_height="48dp"
        android:id="@+id/stylesbar"
        app:buttonColor="@color/style_button_colors"
        android:clipToPadding="false"
        android:paddingStart="8dp"
        android:paddingEnd="8dp"
        app:layout_constraintBottom_toBottomOf="parent"/>
```
### Code
```kotlin
   val markdownEditText = findViewById<MarkdownEditText>(R.id.edittext)
   val stylesBar = findViewById<MarkdownStylesBar>(R.id.stylesbar)
   markdownEditText.setStylesBar(stylesBar)
```
#### Customize default Styles bar :
```Kotlin
   //Select specific Styles to show
   stylesbar.stylesList = arrayOf(MarkdownEditText.TextStyle.BOLD, MarkdownEditText.TextStyle.ITALIC)
```
---

### Available styles:
* bold
* italic
* strike through
* links
* bullet list
* numbered list
* tasks list
---

### Libraries:
[Material Design components](https://github.com/material-components/material-components-android)

[Markwon](https://github.com/noties/Markwon)

---
## License: 
```
  Copyright 2020 YahiaAngelo

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
  ```
