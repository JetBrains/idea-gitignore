package mobi.hsz.idea.gitignore.actions;

import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptorFactory;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.util.IncorrectOperationException;
import mobi.hsz.idea.gitignore.GitignoreFileType;
import mobi.hsz.idea.gitignore.lang.GitignoreLanguage;
import mobi.hsz.idea.gitignore.util.GitignoreIcons;

public class GitignoreTemplatesFactory implements FileTemplateGroupDescriptorFactory {
    private static final String TEXT = "### Created by http://gitignore.hsz.mobi\n\n";
    private final FileTemplateGroupDescriptor templateGroup;

    public GitignoreTemplatesFactory() {
        templateGroup = new FileTemplateGroupDescriptor(GitignoreLanguage.NAME, GitignoreIcons.FILE);
        templateGroup.addTemplate(GitignoreLanguage.FILENAME);
    }

    @Override
    public FileTemplateGroupDescriptor getFileTemplatesDescriptor() {
        return templateGroup;
    }

    public static PsiFile createFromTemplate(final PsiDirectory directory) throws IncorrectOperationException {
        final PsiFileFactory factory = PsiFileFactory.getInstance(directory.getProject());
        final PsiFile file = factory.createFileFromText(GitignoreLanguage.FILENAME, GitignoreFileType.INSTANCE, TEXT);
        return (PsiFile) directory.add(file);
    }
}
