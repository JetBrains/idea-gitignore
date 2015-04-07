/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 hsz Jakub Chrzanowski <jakub@hsz.mobi>
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

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * {@link Icons} class that holds icon resources.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.2.1
 */
public class Icons {
    /** General ignore icon. */
    public static final Icon IGNORE = IconLoader.getIcon("/icons/icon.png");

    /** Bazaar icon. */
    public static final Icon BAZAAR = IconLoader.getIcon("/icons/icon_bazaar.png");

    /** Chefignore icon. */
    public static final Icon CHEF = IconLoader.getIcon("/icons/icon_chef.png");

    /** Cvsignore icon. */
    public static final Icon CVS = IconLoader.getIcon("/icons/icon_cvs.png");

    /** Darcs icon. */
    public static final Icon DARCS = IconLoader.getIcon("/icons/icon_darcs.png");

    /** Dockerignore icon. */
    public static final Icon DOCKER = IconLoader.getIcon("/icons/icon_docker.png");

    /** Git icon. */
    public static final Icon GIT = IconLoader.getIcon("/icons/icon_git.png");

    /** Mercurial icon. */
    public static final Icon MERCURIAL = IconLoader.getIcon("/icons/icon_mercurial.png");

    /** JSHint icon. */
    public static final Icon JSHINT = IconLoader.getIcon("/icons/icon_jshint.png");

    /** Monotone icon. */
    public static final Icon MONOTONE = IconLoader.getIcon("/icons/icon_monotone.png");

    /** Npmignore icon. */
    public static final Icon NPM = IconLoader.getIcon("/icons/icon_npm.png");

    /** Private constructor to prevent creating {@link Icons} instance. */
    private Icons() {
    }
}
