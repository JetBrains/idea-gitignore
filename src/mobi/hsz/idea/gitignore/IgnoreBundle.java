/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 hsz Jakub Chrzanowski <jakub@hsz.mobi>
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

package mobi.hsz.idea.gitignore;

import com.intellij.CommonBundle;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.containers.ContainerUtil;
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType;
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage;
import mobi.hsz.idea.gitignore.lang.kind.*;
import mobi.hsz.idea.gitignore.util.CachedConcurrentMap;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.PropertyKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * {@link ResourceBundle}/localization utils for the .ignore support plugin.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.4
 */
public class IgnoreBundle {
    /** Gitignore plugin ID. */
    @NonNls
    public static final String PLUGIN_ID = "mobi.hsz.idea.gitignore";

    /** The {@link ResourceBundle} path. */
    @NonNls
    public static final String BUNDLE_NAME = "messages.IgnoreBundle";

    /** The {@link ResourceBundle} instance. */
    @NotNull
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    /** {@link IgnoreBundle} is a non-instantiable static class. */
    private IgnoreBundle() {
    }

    /** Available IgnoreFileType instances. */
    public static final IgnoreLanguages LANGUAGES = new IgnoreLanguages(Arrays.asList(
            BazaarLanguage.INSTANCE,
            CloudFoundryLanguage.INSTANCE,
            ChefLanguage.INSTANCE,
            CvsLanguage.INSTANCE,
            DarcsLanguage.INSTANCE,
            DockerLanguage.INSTANCE,
            ElasticBeanstalkLanguage.INSTANCE,
            ESLintLanguage.INSTANCE,
            FloobitsLanguage.INSTANCE,
            FossilLanguage.INSTANCE,
            GitLanguage.INSTANCE,
            GitExcludeLanguage.INSTANCE,
            GoogleCloudLanguage.INSTANCE,
            HelmLanguage.INSTANCE,
            JetpackLanguage.INSTANCE,
            JSHintLanguage.INSTANCE,
            MercurialLanguage.INSTANCE,
            MonotoneLanguage.INSTANCE,
            NodemonLanguage.INSTANCE,
            NpmLanguage.INSTANCE,
            NuxtJSLanguage.INSTANCE,
            PerforceLanguage.INSTANCE,
            PrettierLanguage.INSTANCE,
            StyleLintLanguage.INSTANCE,
            StylintLanguage.INSTANCE,
            SwaggerCodegenLanguage.INSTANCE,
            TFLanguage.INSTANCE,
            UpLanguage.INSTANCE
    ));

    /** Available IgnoreFileType instances filtered with {@link IgnoreLanguage#isVCS()} condition. */
    public static final IgnoreLanguages VCS_LANGUAGES = new IgnoreLanguages(
            ContainerUtil.filter(LANGUAGES, IgnoreLanguage::isVCS)
    );

    /** Contains information about enabled/disabled languages. */
    public static final CachedConcurrentMap<IgnoreFileType, Boolean> ENABLED_LANGUAGES = CachedConcurrentMap.create(
            key -> key.getIgnoreLanguage().isEnabled()
    );

    /** Available syntax list. */
    public enum Syntax {
        GLOB, REGEXP;

        @NonNls
        private static final String KEY = "syntax:";

        @Nullable
        public static Syntax find(@Nullable String name) {
            if (name == null) {
                return null;
            }
            try {
                return Syntax.valueOf(name.toUpperCase());
            } catch (IllegalArgumentException iae) {
                return null;
            }
        }

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }

        /**
         * Returns {@link mobi.hsz.idea.gitignore.psi.IgnoreTypes#SYNTAX} element presentation.
         *
         * @return element presentation
         */
        public String getPresentation() {
            return StringUtil.join(KEY, " ", toString());
        }
    }

    /**
     * Loads a {@link String} from the {@link #BUNDLE} {@link ResourceBundle}.
     *
     * @param key    the key of the resource
     * @param params the optional parameters for the specific resource
     * @return the {@link String} value or {@code null} if no resource found for the key
     */
    public static String message(@PropertyKey(resourceBundle = BUNDLE_NAME) String key, Object... params) {
        return CommonBundle.message(BUNDLE, key, params);
    }

    /**
     * Loads a {@link String} from the {@link #BUNDLE} {@link ResourceBundle}.
     *
     * @param key          the key of the resource
     * @param defaultValue the default value that will be returned if there is nothing set
     * @param params       the optional parameters for the specific resource
     * @return the {@link String} value or {@code null} if no resource found for the key
     */
    public static String messageOrDefault(@PropertyKey(resourceBundle = BUNDLE_NAME) String key, String defaultValue,
                                          Object... params) {
        return CommonBundle.messageOrDefault(BUNDLE, key, defaultValue, params);
    }

    /**
     * Returns {@link IgnoreLanguage} matching to the given {@link VirtualFile}.
     *
     * @param file to obtain
     * @return matching language
     */
    @Nullable
    public static IgnoreLanguage obtainLanguage(@NotNull VirtualFile file) {
        final String filename = file.getName();
        for (IgnoreLanguage language : LANGUAGES) {
            if (language.getFilename().equals(filename)) {
                return language;
            }
        }
        return null;
    }

    /**
     * Simple {@link ArrayList} with method to find {@link IgnoreLanguage} by its name.
     */
    public static class IgnoreLanguages extends ArrayList<IgnoreLanguage> {
        public IgnoreLanguages(List<IgnoreLanguage> languages) {
            super(languages);
        }

        @Nullable
        public IgnoreLanguage get(@NotNull final String id) {
            for (IgnoreLanguage language : this) {
                if (id.equals(language.getID())) {
                    return language;
                }
            }
            return null;
        }
    }
}
