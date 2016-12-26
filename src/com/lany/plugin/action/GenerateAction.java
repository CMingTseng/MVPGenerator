package com.lany.plugin.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.psi.PsiJavaFile;
import com.lany.plugin.dialog.FunctionDialog;

public class GenerateAction extends BaseAnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        super.actionPerformed(event);
        PsiJavaFile javaFile = (PsiJavaFile) event.getData(CommonDataKeys.PSI_FILE);
        FunctionDialog dialog = new FunctionDialog();
        dialog.initData(event, javaFile);
        dialog.setOnFunctionDialogListener(this);
        dialog.pack();
        dialog.setVisible(true);
    }
}