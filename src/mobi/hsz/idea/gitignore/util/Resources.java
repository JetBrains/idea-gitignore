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

import mobi.hsz.idea.gitignore.lang.gitignore.GitignoreLanguage;
import mobi.hsz.idea.gitignore.settings.IgnoreSettings;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * {@link Resources} util class that contains methods that work on plugin's resources.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.2
 */
public class Resources {
    /** Path to the gitignore templates list. */
    @NonNls
    private static final String GITIGNORE_TEMPLATES_PATH = "/templates.list";

    /** List of fetched {@link Template} elements from resources. */
    private static List<Template> resourceTemplates;

    /** Private constructor to prevent creating {@link Resources} instance. */
    private Resources() {
    }

    /**
     * Returns list of gitignore templates
     *
     * @return Gitignore templates list
     */
    public static List<Template> getGitignoreTemplates() {
        if (resourceTemplates == null) {
            resourceTemplates = new ArrayList<Template>();

            // fetch templates from resources
            try {
                String list = getResourceContent(GITIGNORE_TEMPLATES_PATH);
                BufferedReader br = new BufferedReader(new StringReader(list));

                for (String line; (line = br.readLine()) != null; ) {
                    line = "/" + line;
                    File file = getResource(line);
                    String content = getResourceContent(line);
                    resourceTemplates.add(new Template(file, content));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        final List<Template> templates = new ArrayList<Template>();
        templates.addAll(resourceTemplates);

        // fetch user templates
        IgnoreSettings settings = IgnoreSettings.getInstance();
        for (IgnoreSettings.UserTemplate userTemplate : settings.getUserTemplates()) {
            templates.add(new Template(userTemplate));
        }

        return templates;
    }

    /**
     * Returns gitignore templates directory
     *
     * @return Resources directory
     */
    public static File getResource(String path) {
        URL resource = Resources.class.getResource(path);
        assert resource != null;
        return new File(resource.getPath());
    }

    /**
     * Reads resource file and returns its content as a String
     *
     * @param path Resource path
     * @return Content
     */
    public static String getResourceContent(String path) {
        return convertStreamToString(Resources.class.getResourceAsStream(path));
    }

    /**
     * Converts InputStream resource to String
     *
     * @param inputStream Input stream
     * @return Content
     */
    protected static String convertStreamToString(InputStream inputStream) {
        Scanner s = new Scanner(inputStream).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    /**
     * {@link Template} entity that defines template fetched from resources or {@link IgnoreSettings}.
     */
    public static class Template implements Comparable<Template> {
        /** {@link File} pointer. <code>null</code> if template is fetched from {@link IgnoreSettings}. */
        private final File file;

        /** Template name. */
        private final String name;

        /** Template content. */
        private final String content;

        /** Template's {@link Container}. */
        private final Container container;

        /**
         * Defines if template is fetched from resources ({@link Container#ROOT} directory or {@link Container#GLOBAL}
         * subdirectory) or is user defined and fetched from {@link IgnoreSettings}.
         */
        public static enum Container {
            USER, ROOT, GLOBAL
        }

        /**
         * Builds a new instance of {@link Template}.
         * {@link Container} will be set to {@link Container#ROOT} or {@link Container#GLOBAL} depending on its location.
         *
         * @param file    template's file
         * @param content template's content
         */
        public Template(File file, String content) {
            this.file = file;
            this.name = file.getName().replace(GitignoreLanguage.INSTANCE.getFilename(), "");
            this.content = content;
            this.container = file.getParent().endsWith("Global") ? Container.GLOBAL : Container.ROOT;
        }

        /**
         * Builds a new instance of {@link Template}.
         * {@link Container} will be set to {@link Container#USER}.
         *
         * @param userTemplate {@link IgnoreSettings} user template object
         */
        public Template(IgnoreSettings.UserTemplate userTemplate) {
            this.file = null;
            this.name = userTemplate.getName();
            this.content = userTemplate.getContent();
            this.container = Container.USER;
        }

        /**
         * Gets template's file.
         *
         * @return template's file
         */
        public File getFile() {
            return file;
        }

        /**
         * Gets template's name.
         *
         * @return template's name
         */
        public String getName() {
            return name;
        }

        /**
         * Gets template's content.
         *
         * @return template's content
         */
        public String getContent() {
            return content;
        }

        /**
         * Gets template's container.
         *
         * @return template's container
         */
        public Container getContainer() {
            return container;
        }

        /**
         * Returns string representation of {@link Template}.
         *
         * @return template's name
         */
        @Override
        public String toString() {
            return name;
        }

        /**
         * Compares given template to the current one.
         *
         * @param template template to compare
         * @return templates comparison
         */
        @Override
        public int compareTo(@NotNull final Template template) {
            return name.compareToIgnoreCase(template.name);
        }
    }

}
