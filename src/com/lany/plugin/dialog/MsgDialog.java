package com.lany.plugin.dialog;

import com.intellij.openapi.ui.Messages;


public class MsgDialog {

    public static void showErrorMsg(String context) {
        Messages.showMessageDialog(context, "Error", Messages.getErrorIcon());
    }

    public static void showMsg(String context, String title) {
        Messages.showMessageDialog(context, title, Messages.getInformationIcon());
    }

    public static void showDebugMsg(String context, String title) {
        //Messages.showMessageDialog(context, title, Messages.getErrorIcon());
    }
}
