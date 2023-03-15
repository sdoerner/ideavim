/*
 * Copyright 2003-2023 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package org.jetbrains.plugins.ideavim.helper

import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.CaretVisualAttributes
import com.intellij.openapi.editor.VisualPosition
import com.intellij.openapi.editor.ex.EditorSettingsExternalizable
import com.maddyhome.idea.vim.VimPlugin
import com.maddyhome.idea.vim.api.injector
import com.maddyhome.idea.vim.helper.VimBehaviorDiffers
import com.maddyhome.idea.vim.newapi.vim
import org.jetbrains.plugins.ideavim.SkipNeovimReason
import org.jetbrains.plugins.ideavim.TestWithoutNeovim
import org.jetbrains.plugins.ideavim.VimTestCase
import org.junit.jupiter.api.Test

class CaretVisualAttributesHelperTest : VimTestCase() {
  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  @Test
  fun `test default normal mode caret is block`() {
    configureByText("I found it in a legendary land")
    assertCaretVisualAttributes(CaretVisualAttributes.Shape.BLOCK, 0F)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  @Test
  fun `test default insert mode caret is vertical bar`() {
    configureByText("I found it in a legendary land")
    typeText("i")
    assertCaretVisualAttributes(CaretVisualAttributes.Shape.BAR, 0.25F)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  @Test
  fun `test insert mode caret is reset after Escape`() {
    configureByText("I found it in a legendary land")
    typeText("i", "<Esc>")
    assertCaretVisualAttributes(CaretVisualAttributes.Shape.BLOCK, 0F)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  @Test
  fun `test default replace mode caret is underscore`() {
    configureByText("I found it in a legendary land")
    typeText("R")
    assertCaretVisualAttributes(CaretVisualAttributes.Shape.UNDERSCORE, 0.2F)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  @Test
  fun `test default op pending caret is thick underscore`() {
    configureByText("I found it in a legendary land")
    typeText("d")
    assertCaretVisualAttributes(CaretVisualAttributes.Shape.UNDERSCORE, 0.5F)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  @Test
  fun `test caret is reset after op pending`() {
    configureByText("I found it in a legendary land")
    typeText("d$")
    assertCaretVisualAttributes(CaretVisualAttributes.Shape.BLOCK, 0F)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  @Test
  fun `test default visual mode caret is block`() {
    configureByText("I found it in a legendary land")
    typeText("ve")
    assertCaretVisualAttributes(CaretVisualAttributes.Shape.BLOCK, 0F)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  @Test
  fun `test visual block hides secondary carets`() {
    configureByLines(5, "I found it in a legendary land")
    typeText("w", "<C-V>2j5l")
    assertCaretVisualAttributes(CaretVisualAttributes.Shape.BLOCK, 0F)
    fixture.editor.caretModel.allCarets.forEach {
      if (it != fixture.editor.caretModel.primaryCaret) {
        assertCaretVisualAttributes(it, CaretVisualAttributes.Shape.BAR, 0F)
      }
    }
  }

  @VimBehaviorDiffers(description = "Vim does not change the caret for select mode", shouldBeFixed = false)
  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  @Test
  fun `test select mode uses insert mode caret`() {
    // Vim doesn't have a different caret for SELECT, and doesn't have an option in guicursor to change SELECT mode
    configureByText("I found it in a legendary land")
    typeText("v7l", "<C-G>")
    assertCaretVisualAttributes(CaretVisualAttributes.Shape.BAR, 0.25F)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  @Test
  fun `test replace character uses replace mode caret`() {
    configureByText("I ${c}found it in a legendary land")
    typeText("r")
    assertCaretVisualAttributes(CaretVisualAttributes.Shape.UNDERSCORE, 0.2F)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  @Test
  fun `test caret reset after replacing character`() {
    configureByText("I ${c}found it in a legendary land")
    typeText("r", "z")
    assertCaretVisualAttributes(CaretVisualAttributes.Shape.BLOCK, 0F)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  @Test
  fun `test caret reset after escaping replace character`() {
    configureByText("I ${c}found it in a legendary land")
    typeText("r", "<Esc>")
    assertCaretVisualAttributes(CaretVisualAttributes.Shape.BLOCK, 0F)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  @Test
  fun `test caret reset after cancelling replace character`() {
    configureByText("I ${c}found it in a legendary land")
    typeText("r", "<Left>")
    assertCaretVisualAttributes(CaretVisualAttributes.Shape.BLOCK, 0F)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  @Test
  fun `test visual replace character uses replace mode caret`() {
    configureByText("I ${c}found it in a legendary land")
    typeText("ve", "r")
    assertCaretVisualAttributes(CaretVisualAttributes.Shape.UNDERSCORE, 0.2F)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  @Test
  fun `test caret reset after completing visual replace character`() {
    configureByText("I ${c}found it in a legendary land")
    typeText("ve", "r", "z")
    assertCaretVisualAttributes(CaretVisualAttributes.Shape.BLOCK, 0F)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  @Test
  fun `test caret reset after escaping visual replace character`() {
    configureByText("I ${c}found it in a legendary land")
    typeText("ve", "r", "<Esc>")
    assertCaretVisualAttributes(CaretVisualAttributes.Shape.BLOCK, 0F)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  @Test
  fun `test caret reset after cancelling visual replace character`() {
    configureByText("I ${c}found it in a legendary land")
    typeText("ve", "r", "<Left>")
    assertCaretVisualAttributes(CaretVisualAttributes.Shape.BLOCK, 0F)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  @Test
  fun `test nested visual mode in ide gets visual caret`() {
    configureByText("I ${c}found it in a legendary land")
    enterCommand("set keymodel=startsel,stopsel")
    typeText("i", "<S-Right><S-Right><S-Right>")
    assertCaretVisualAttributes(CaretVisualAttributes.Shape.BLOCK, 0F)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  @Test
  fun `test caret reset to insert after leaving nested visual mode`() {
    configureByText("I ${c}found it in a legendary land")
    enterCommand("set keymodel=startsel,stopsel")
    typeText("i", "<S-Right><S-Right><S-Right>", "<Right>")
    assertCaretVisualAttributes(CaretVisualAttributes.Shape.BAR, 0.25F)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  @Test
  fun `test caret reset to insert after cancelling nested visual mode`() {
    configureByText("I ${c}found it in a legendary land")
    enterCommand("set keymodel=startsel,stopsel")
    typeText("i", "<S-Right><S-Right><S-Right>", "<Esc>")
    assertCaretVisualAttributes(CaretVisualAttributes.Shape.BAR, 0.25F)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  @Test
  fun `test changing guicursor option updates caret immediately`() {
    configureByText("I found it in a legendary land")
    enterCommand("set guicursor=n:hor22")
    assertCaretVisualAttributes(CaretVisualAttributes.Shape.UNDERSCORE, 0.22F)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  @Test
  fun `test changing guicursor option invalidates caches correctly`() {
    configureByText("I found it in a legendary land")
    typeText("i")
    assertCaretVisualAttributes(CaretVisualAttributes.Shape.BAR, 0.25F)
    typeText("<Esc>")
    enterCommand("set guicursor=i:hor22")
    typeText("i")
    assertCaretVisualAttributes(CaretVisualAttributes.Shape.UNDERSCORE, 0.22F)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  @Test
  fun `test caret uses last matching guicursor option`() {
    configureByText("I found it in a legendary land")
    // This will give us three matching options for INSERT
    enterCommand("set guicursor+=i:ver25")
    enterCommand("set guicursor+=i:hor75")
    typeText("i")
    assertCaretVisualAttributes(CaretVisualAttributes.Shape.UNDERSCORE, 0.75F)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  @Test
  fun `test block used when caret shape is unspecified`() {
    configureByText("I found it in a legendary land")
    enterCommand("set guicursor=c:ver25")
    assertCaretVisualAttributes(CaretVisualAttributes.Shape.BLOCK, 0.0F)
    typeText("i")
    assertCaretVisualAttributes(CaretVisualAttributes.Shape.BLOCK, 0.0F)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  @Test
  fun `test 'all' guicursor option`() {
    configureByText("I found it in a legendary land")
    enterCommand("set guicursor+=a:ver25")
    assertCaretVisualAttributes(CaretVisualAttributes.Shape.BAR, 0.25F)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  @Test
  fun `test 'all' guicursor option without cursor shape does not affect existing shapes`() {
    configureByText("I found it in a legendary land")
    enterCommand("set guicursor+=a:blinkwait200-blinkoff125-blinkon150-Cursor/lCursor")
    typeText("i")
    assertCaretVisualAttributes(CaretVisualAttributes.Shape.BAR, 0.25F)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  @Test
  fun `test 'all' guicursor option can be overridden`() {
    configureByText("I found it in a legendary land")
    // A specific entry added after "all" takes precedence
    enterCommand("set guicursor+=a:ver25")
    enterCommand("set guicursor+=i:hor75")
    typeText("i")
    assertCaretVisualAttributes(CaretVisualAttributes.Shape.UNDERSCORE, 0.75F)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  @Test
  fun `test block caret setting overrides guicursor`() {
    val originalValue = EditorSettingsExternalizable.getInstance().isBlockCursor
    EditorSettingsExternalizable.getInstance().isBlockCursor = true
    try {
      configureByText("I found it in a legendary land")
      typeText("i")
      assertCaretVisualAttributes(CaretVisualAttributes.Shape.BLOCK, 1.0F)
    } finally {
      EditorSettingsExternalizable.getInstance().isBlockCursor = originalValue
    }
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  @Test
  fun `test reset caret shape when disable plugin`() {
    configureByText("I found it in a legendary land")
    typeText("i")
    VimPlugin.setEnabled(false)
    assertCaretVisualAttributes(CaretVisualAttributes.Shape.DEFAULT, 1.0f)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  @Test
  fun `test reset caret shape when disable plugin 2`() {
    configureByText("I found it in a legendary land")
    typeText("v2e")
    VimPlugin.setEnabled(false)
    assertCaretVisualAttributes(CaretVisualAttributes.Shape.DEFAULT, 1.0f)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  @Test
  fun `test reset caret shape when disable plugin 3`() {
    configureByText("I found it in a legendary land")
    typeText("r")
    VimPlugin.setEnabled(false)
    assertCaretVisualAttributes(CaretVisualAttributes.Shape.DEFAULT, 1.0f)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  @Test
  fun `test adding new caret via IJ`() {
    configureByText("${c}I found it in a legendary land")
    fixture.editor.caretModel.addCaret(VisualPosition(0, 5))
    assertCaretVisualAttributes(CaretVisualAttributes.Shape.BLOCK, 0f)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  @Test
  fun `test adding new caret below`() {
    configureByText(
      """
      |${c}I found it in a legendary land
      |all rocks and lavender and tufted grass,
      """.trimMargin(),
    )
    injector.actionExecutor.executeAction(
      "EditorCloneCaretBelow",
      injector.executionContextManager.onEditor(fixture.editor.vim),
    )
    kotlin.test.assertEquals(2, fixture.editor.caretModel.caretCount)
    assertCaretVisualAttributes(CaretVisualAttributes.Shape.BLOCK, 0f)
  }

  private fun assertCaretVisualAttributes(expectedShape: CaretVisualAttributes.Shape, expectedThickness: Float) {
    assertCaretVisualAttributes(fixture.editor.caretModel.primaryCaret, expectedShape, expectedThickness)
  }

  private fun assertCaretVisualAttributes(
    caret: Caret,
    expectedShape: CaretVisualAttributes.Shape,
    expectedThickness: Float,
  ) {
    kotlin.test.assertEquals(expectedShape, caret.visualAttributes.shape)
    kotlin.test.assertEquals(expectedThickness, caret.visualAttributes.thickness)
  }
}
