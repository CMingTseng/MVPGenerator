package com.lany.plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiJavaFile;
import com.lany.plugin.ClassGenerator;
import com.lany.plugin.ClassModel;
import com.lany.plugin.Constants;
import com.lany.plugin.dialog.MsgDialog;

import java.io.IOException;

/**
 * Created by Lany on 2016/12/18.
 */
public class GenerateAction extends AnAction {
    private ClassModel _classModel;
    private Editor _editor;
    private String _content;
    private boolean canCreate;
    private AnActionEvent _event;
    private String _path;
    private int mode;
    private final int MODE_CONTRACT = 0;
    private final int MODE_PRESENTER = 1;

    @Override
    public void actionPerformed(AnActionEvent e) {
        this._event = e;
        canCreate = true;
        init(e);
        getClassModel();
        createFiles();
        PsiJavaFile javaFile = (PsiJavaFile) e.getData(CommonDataKeys.PSI_FILE);

        System.out.println("current package name is :" + javaFile.getPackageName());
        try {
            if (canCreate) {
                createClassFiles();
//                MessagesCenter.showMessage("created success! please wait a moment","success");
                refreshProject(e);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }


    }

    private void init(AnActionEvent e) {
        _editor = e.getData(PlatformDataKeys.EDITOR);
        _classModel = new ClassModel();
    }

    private void refreshProject(AnActionEvent e) {
        e.getProject().getBaseDir().refresh(false, true);
    }

    /**
     * 创建class文件
     * create class files
     *
     * @throws IOException
     */
    private void createClassFiles() throws IOException {

        if (mode == MODE_CONTRACT) {
            createFileWithContract();

        } else if (mode == MODE_PRESENTER) {
            createClassWithPresenter();
        }
    }

    private void createClassWithPresenter() throws IOException {
        String className = _classModel.get_className();
        String classFullName = _classModel.get_classFullName();
        System.out.println("_path presenter:" + _path);
        ClassGenerator.createImplClass(_path
                , className
                , classFullName, Constants.MODEL
                , Constants.PRESENTER);
        ClassGenerator.createImplClass(
                _path, className
                , classFullName, Constants.PRESENTER
                , Constants.PRESENTER);
        ClassGenerator.createInterface(_path, className, classFullName, Constants.MODEL);


        ClassGenerator.createInterface(_path, className, classFullName, Constants.VIEW);
    }

    /**
     * 以contract模式生成 .java文件
     *
     * @throws IOException
     */
    private void createFileWithContract() throws IOException {
        String className = _classModel.get_className();
        String classFullName = _classModel.get_classFullName();
        System.out.println("_path:" + _path);


        // create presenter file
        ClassGenerator.createImplClass(_path
                , className
                , classFullName, Constants.MODEL
                , Constants.CONTRACT);


        // create presenter file
        ClassGenerator.createImplClass(
                _path, className
                , classFullName, Constants.PRESENTER
                , Constants.CONTRACT);
    }


    /**
     * 生成 contract类内容
     * create Contract Model Presenter
     */
    private void createFiles() {
        if (null == _classModel.get_className()) {
            return;
        }

        _path = ClassGenerator.getCurrentPath(_event, _classModel.get_classFullName());
        if (_path.contains("contract")) {
            System.out.println("_path replace contract " + _path);
            _path = _path.replace("contract/", "");
        } else if (_path.contains("presenter")) {

            System.out.println("_path replace contract " + _path);
            _path = _path.replace("presenter/", "");

        } else {
            if (mode == MODE_CONTRACT) {
                MsgDialog.showErrorMsg("Your Contract should in package 'contract'.");
            } else if (mode == MODE_PRESENTER) {
                MsgDialog.showErrorMsg("Your Presenter should in package 'presenter'.");
            }
            canCreate = false;
        }
        if (canCreate) {
            if (mode == MODE_CONTRACT) {
                setFileDocument();
            }
        }

    }

    /**
     * 生成 contract类内容
     * create Contract Model Presenter
     */
    private void setFileDocument() {


        int lastIndex = _content.lastIndexOf("}");
        _content = _content.substring(0, lastIndex);
        MsgDialog.showDebugMsg(_content, "debug");
        final String content = setContractContent();
        //wirte in runWriteAction
        WriteCommandAction.runWriteCommandAction(_editor.getProject(),
                new Runnable() {
                    @Override
                    public void run() {
                        _editor.getDocument().setText(content);
                    }
                });

    }

    private String setContractContent() {
        String className = _classModel.get_className();
        String content = _content + "public interface " + "View{\n}\n\n"
                + "public interface " + "Presenter{\n}\n\n"
                + "public interface " + "Model{\n}\n\n"
                + "\n}";

        return content;
    }


    private void getClassModel() {
        _content = _editor.getDocument().getText();

        String[] words = _content.split(" ");

        for (String word : words) {
            if (word.contains("Contract")) {
                String className = word.substring(0, word.indexOf("Contract"));
                _classModel.set_className(className);
                _classModel.set_classFullName(word);
                MsgDialog.showDebugMsg(className, "class name");
                mode = MODE_CONTRACT;
            } else if (word.contains("Presenter")) {


                String className = word.substring(0, word.indexOf("Presenter"));
                _classModel.set_className(className);
                _classModel.set_classFullName(word);
                mode = MODE_PRESENTER;
                MsgDialog.showDebugMsg(className, "class name");
            }
        }
        if (null == _classModel.get_className()) {
            MsgDialog.showErrorMsg("Create failed ,Can't found 'Contract' or 'Presenter' in your class name,your class name must contain 'Contract' or 'Presenter'");
            canCreate = false;
        }
    }


}