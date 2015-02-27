/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 hsz Jakub Chrzanowski <jakub@hsz.mobi>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package mobi.hsz.idea.gitignore.util;

import com.intellij.ide.IdeBundle;
import com.intellij.openapi.application.ApplicationNamesInfo;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.application.ex.ApplicationEx;
import com.intellij.openapi.application.ex.ApplicationManagerEx;
import com.intellij.openapi.application.impl.LaterInvocator;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NonNls;

/**
 * Restart IntelliJ util tool.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.10
 */
public class Restart {

    @NonNls
    private static final String POSTPONE = "&Postpone";

    /**
     * Shutdowns or restarts IntelliJ.
     *
     * @param title dialog title
     */
    public static void shutdownOrRestartApp(String title) {
        final ApplicationEx app = ApplicationManagerEx.getApplicationEx();
        int response = app.isRestartCapable() ? showRestartIDEADialog(title) : showShutDownIDEADialog(title);
        if (response == Messages.YES) {
            LaterInvocator.invokeLater(new Runnable() {
                @Override
                public void run() {
                    app.restart(true);
                }
            }, ModalityState.NON_MODAL);
        }
    }

    @Messages.YesNoResult
    private static int showRestartIDEADialog(final String title) {
        String message = IdeBundle.message("message.idea.restart.required", ApplicationNamesInfo.getInstance().getFullProductName());
        return Messages.showYesNoDialog(message, title, "Restart", POSTPONE, Messages.getQuestionIcon());
    }

    @Messages.YesNoResult
    private static int showShutDownIDEADialog(final String title) {
        String message = IdeBundle.message("message.idea.shutdown.required", ApplicationNamesInfo.getInstance().getFullProductName());
        return Messages.showYesNoDialog(message, title, "Shut Down", POSTPONE, Messages.getQuestionIcon());
    }

}
