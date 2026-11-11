package com.srilakshmikanthanp.clipbird.ui

import javax.swing.JOptionPane
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.UIManager

object Dialog {
  fun showError(title: String, message: String) {
    runCatching { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()) }

    val text = JTextArea(message, 0, 60).apply {
      isEditable = false
      lineWrap = true
      wrapStyleWord = true
      isOpaque = false
      border = null
    }

    val content = JScrollPane(text).apply {
      border = null
    }

    JOptionPane.showMessageDialog(
      null,
      content,
      title,
      JOptionPane.ERROR_MESSAGE
    )
  }
}
