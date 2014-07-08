package mobi.hsz.idea.gitignore.file;

import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptorFactory;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.util.IncorrectOperationException;
import mobi.hsz.idea.gitignore.GitignoreBundle;
import mobi.hsz.idea.gitignore.GitignoreLanguage;
import mobi.hsz.idea.gitignore.util.Icons;

public class GitignoreTemplatesFactory implements FileTemplateGroupDescriptorFactory {
    private static final String TEXT = GitignoreBundle.message("file.templateNote");
    private final FileTemplateGroupDescriptor templateGroup;

    public GitignoreTemplatesFactory() {
        templateGroup = new FileTemplateGroupDescriptor(GitignoreLanguage.NAME, Icons.FILE);
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
