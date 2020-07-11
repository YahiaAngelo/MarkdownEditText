# MarkdownEditText [ ![Download](https://api.bintray.com/packages/yahiaangelo/MarkdownEditText/com.yahiaangelo.markdownedittext/images/download.svg) ](https://bintray.com/yahiaangelo/MarkdownEditText/com.yahiaangelo.markdownedittext/_latestVersion)

A native Rich text editor for android based on [Markwon](https://github.com/noties/Markwon) library with export to Markdown option

---
## Preivew
<img src="https://raw.githubusercontent.com/YahiaAngelo/MarkdownEditText/master/preview/preview.gif" width="310">

---

## Usage:

### Adding the depencency
Add the dependency to your app build.gradle file:
```
implementation 'com.yahiaangelo.markdownedittext:markdownedittext:1.0.0'
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
---

### Available styles:
* bold
* italic
* strike through
* bullet list
* numbered list
---

### Libraries:

[Markwon](https://github.com/noties/Markwon)
[Material Design components](https://github.com/material-components/material-components-android)
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