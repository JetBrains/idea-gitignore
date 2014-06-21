package mobi.hsz.idea.gitignore.actions;

import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptorFactory;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.util.IncorrectOperationException;
import mobi.hsz.idea.gitignore.GitignoreFileType;
import mobi.hsz.idea.gitignore.util.GitignoreIcons;

public class GitignoreTemplatesFactory implements FileTemplateGroupDescriptorFactory {
    private static final String FILE_NAME = "bash-script.sh";
    private final FileTemplateGroupDescriptor templateGroup;

    public GitignoreTemplatesFactory() {
        templateGroup = new FileTemplateGroupDescriptor("xxxx", GitignoreIcons.FILE);
        templateGroup.addTemplate(FILE_NAME);
    }

    @Override
    public FileTemplateGroupDescriptor getFileTemplatesDescriptor() {
        return templateGroup;
    }

    public static PsiFile createFromTemplate(final PsiDirectory directory, final String name, String fileName) throws IncorrectOperationException {
        final String text = "#!/bin/bash\n";
        final PsiFileFactory factory = PsiFileFactory.getInstance(directory.getProject());

        final PsiFile file = factory.createFileFromText(fileName, GitignoreFileType.INSTANCE, text);

        return (PsiFile) directory.add(file);
    }
}
