package com.lany.plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.lany.plugin.dialog.FunctionDialog;
import com.lany.plugin.model.EditInfo;
import com.lany.plugin.utils.PsiHelper;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;


public abstract class BaseAnAction extends AnAction implements FunctionDialog.OnFunctionDialogListener {
    private AnActionEvent event;

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        event = anActionEvent;
    }

    @Override
    public void onCreateBtnClicked(EditInfo editInfo) {
        try {
            PsiHelper.create(event, editInfo);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        event.getProject().getBaseDir().refresh(false, true);
    }

    @Override
    public void onCancelBtnClicked() {

    }
}
