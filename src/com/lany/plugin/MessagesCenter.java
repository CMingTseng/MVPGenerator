package com.lany.plugin;

import com.intellij.openapi.ui.Messages;

/**
 * Created by Lany on 2016/12/18.
 */
public class MessagesCenter {

    public static void showErrorMessage(String context,String title){
        Messages.showMessageDialog(context,title,Messages.getErrorIcon());
    }
    public static void showMessage(String context,String title){
        Messages.showMessageDialog(context,title,Messages.getInformationIcon());
    }

    public static void showDebugMessage(String context,String title){
        if(false) {
            Messages.showMessageDialog(context, title, Messages.getErrorIcon());
        }
    }
}
