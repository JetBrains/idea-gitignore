/*
 * The MIT License (MIT)
 *
 * Copyright (c) today.year hsz Jakub Chrzanowski <jakub@hsz.mobi>
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

import mobi.hsz.idea.gitignore.GitignoreLanguage;
import mobi.hsz.idea.gitignore.settings.GitignoreSettings;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Resources {

    @NonNls
    private static final String GITIGNORE_TEMPLATES_PATH = "/templates.list";

    private static List<Template> resourceTemplates;

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
        GitignoreSettings settings = GitignoreSettings.getInstance();
        for (GitignoreSettings.UserTemplate userTemplate : settings.getUserTemplates()) {
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

    public static class Template implements Comparable<Template> {
        private final File file;
        private final String name;
        private final String content;
        private final Container container;

        public static enum Container {
            USER, ROOT, GLOBAL;
        }

        public Template(File file, String content) {
            this.file = file;
            this.name = file.getName().replace(GitignoreLanguage.FILENAME, "");
            this.content = content;
            this.container = file.getParent().endsWith("Global") ? Container.GLOBAL : Container.ROOT;
        }

        public Template(GitignoreSettings.UserTemplate userTemplate) {
            this.file = null;
            this.name = userTemplate.getName();
            this.content = userTemplate.getContent();
            this.container = Container.USER;
        }

        public File getFile() {
            return file;
        }

        public String getName() {
            return name;
        }

        public String getContent() {
            return content;
        }

        public Container getContainer() {
            return container;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public int compareTo(@NotNull final Template template) {
            return name.compareToIgnoreCase(template.name);
        }
    }

}
