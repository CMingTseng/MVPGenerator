package com.lany.plugin.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.lany.plugin.dialog.FunctionDialog;

public class CodeAction extends BaseAnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        super.actionPerformed(event);
        Project project = event.getProject();
        PsiDirectory moduleDir = PsiDirectoryFactory.getInstance(project).createDirectory(event.getData(PlatformDataKeys.VIRTUAL_FILE));
        FunctionDialog dialog = new FunctionDialog();
        dialog.initData(event, moduleDir);
        dialog.setOnFunctionDialogListener(this);
        dialog.pack();
        dialog.setVisible(true);
    }
}
