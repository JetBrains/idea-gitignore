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

package mobi.hsz.idea.gitignore.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.parser.GeneratedParserUtilBase;
import com.intellij.psi.tree.IElementType;

/**
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.8.1
 */
public class IgnoreParserUtil extends GeneratedParserUtilBase {

    public static int current_position_(PsiBuilder builder_) {
        try {
            if (GeneratedParserUtilBase.class.getMethod("current_position_", PsiBuilder.class) != null) {
                return GeneratedParserUtilBase.current_position_(builder_);
            }
        } catch (NoSuchMethodException ignored) {}
        return 0;
    }

    public static boolean nextTokenIs(PsiBuilder builder_, String frameName, IElementType... tokens) {
        try {
            if (GeneratedParserUtilBase.class.getMethod("nextTokenIs", PsiBuilder.class, String.class, IElementType.class) != null) {
                return GeneratedParserUtilBase.nextTokenIs(builder_, frameName, tokens);
            }
        } catch (NoSuchMethodException ignored) {}
        return true;
    }

    public static boolean empty_element_parsed_guard_(PsiBuilder builder_, String funcName_, int prev_position_) {
        try {
            if (GeneratedParserUtilBase.class.getMethod("nextTokenIs", PsiBuilder.class, String.class, int.class) != null) {
                return GeneratedParserUtilBase.empty_element_parsed_guard_(builder_, funcName_, prev_position_);
            }
        } catch (NoSuchMethodException ignored) {}
        return true;
    }

}
