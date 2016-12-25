package com.lany.plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.intellij.psi.search.GlobalSearchScope;
import com.lany.plugin.dialog.FunctionDialog;
import com.lany.plugin.model.EditEntity;
import com.lany.plugin.utils.ClassGenerator;
import com.lany.plugin.model.ClassModel;
import com.lany.plugin.Constants;

import java.io.IOException;


public class GenerateAction extends AnAction {
    private ClassModel mClassModel;
    private Editor mEditor;
    private String mContent;
    private boolean canCreate;
    private AnActionEvent mAnActionEvent;
    private String _path;
    private int mode;
    private final int MODE_CONTRACT = 0;
    private final int MODE_PRESENTER = 1;

    @Override
    public void actionPerformed(AnActionEvent event) {
        mAnActionEvent = event;
        canCreate = true;
        mEditor = event.getData(PlatformDataKeys.EDITOR);
        mClassModel = new ClassModel();

        PsiJavaFile javaFile = (PsiJavaFile) event.getData(CommonDataKeys.PSI_FILE);
        System.out.println("当前包名:" + javaFile.getPackageName());//com.lany.presenter
        System.out.println("当前类名:" + javaFile.getName());//LoginPresenter.java
        System.out.println("当前类继承的列表:" + javaFile.getImportList().getText());
        System.out.println("当前文件类型:" + javaFile.getFileType());
        System.out.println("当前类父类名称:" + javaFile.getParent().getName());
        System.out.println("当前类内容:" + javaFile.getText());

        Project project = event.getProject();
        PsiDirectory moduleDir = PsiDirectoryFactory.getInstance(project).createDirectory(event.getData(PlatformDataKeys.VIRTUAL_FILE));
        System.out.println("当前目录名称:" + moduleDir.getName());
        GlobalSearchScope searchScope = GlobalSearchScope.allScope(project);
        System.out.println("搜索范围:" + searchScope.getDisplayName());
        PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
        System.out.println("factory:" + factory.toString());

        FunctionDialog dialog = new FunctionDialog();
        dialog.setOnFunctionDialogListener(new FunctionDialog.OnFunctionDialogListener() {

            @Override
            public void onCreateBtnClicked(EditEntity editEntity) {
                getClassModel();
                createFiles();

                try {
                    if (canCreate) {
                        createClassFiles();//创建MVP类
                        refreshProject(event);
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void onCancelBtnClicked() {

            }
        });
        dialog.pack();
        dialog.setVisible(true);
    }

    private void getClassModel() {
        mContent = mEditor.getDocument().getText();//获取当前类的所有内容
        System.out.println("当前内容mContent:" + mContent);
        String[] words = mContent.split(" ");

        for (String word : words) {
            if (word.contains("Contract")) {
                String className = word.substring(0, word.indexOf("Contract"));
                mClassModel.setClassName(className);
                mClassModel.setClassFullName(word);
                System.out.println("类名:" + className);
                System.out.println("类全名:" + word);
                mode = MODE_CONTRACT;
            } else if (word.contains("Presenter")) {
                String className = word.substring(0, word.indexOf("Presenter"));
                mClassModel.setClassName(className);
                mClassModel.setClassFullName(word);
                mode = MODE_PRESENTER;
                System.out.println("类名:" + className);
                System.out.println("类全名:" + word);
            }
        }
        if (null == mClassModel.getClassName()) {
            Messages.showMessageDialog("Create failed ,Can't found 'Contract' or 'Presenter' in your class name,your class name must contain 'Contract' or 'Presenter'", "Error", Messages.getErrorIcon());
            canCreate = false;
        }
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
        String className = mClassModel.getClassName();
        String classFullName = mClassModel.getClassFullName();
        System.out.println("_path presenter:" + _path);
        System.out.println("类名:" + className);
        System.out.println("类全名:" + classFullName);

        ClassGenerator.createImplClass(_path, className, classFullName, Constants.MODEL, Constants.PRESENTER);
        ClassGenerator.createImplClass(_path, className, classFullName, Constants.PRESENTER, Constants.PRESENTER);
        ClassGenerator.createInterface(_path, className, Constants.MODEL);
        ClassGenerator.createInterface(_path, className, Constants.VIEW);
    }

    /**
     * 以contract模式生成 .java文件
     *
     * @throws IOException
     */
    private void createFileWithContract() throws IOException {
        String className = mClassModel.getClassName();
        String classFullName = mClassModel.getClassFullName();
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
        if (null == mClassModel.getClassName()) {
            return;
        }

        _path = ClassGenerator.getCurrentPath(mAnActionEvent, mClassModel.getClassFullName());
        if (_path.contains("contract")) {
            System.out.println("_path replace contract " + _path);
            _path = _path.replace("contract/", "");
        } else if (_path.contains("presenter")) {

            System.out.println("_path replace contract " + _path);
            _path = _path.replace("presenter/", "");

        } else {
            if (mode == MODE_CONTRACT) {
                Messages.showMessageDialog("Your Contract should in package 'contract'.", "Error", Messages.getErrorIcon());
            } else if (mode == MODE_PRESENTER) {
                Messages.showMessageDialog("Your Presenter should in package 'presenter'.", "Error", Messages.getErrorIcon());
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
        int lastIndex = mContent.lastIndexOf("}");
        mContent = mContent.substring(0, lastIndex);
        System.out.println("mContent:" + mContent);
        final String content = setContractContent();
        //wirte in runWriteAction
        WriteCommandAction.runWriteCommandAction(mEditor.getProject(),
                new Runnable() {
                    @Override
                    public void run() {
                        mEditor.getDocument().setText(content);
                    }
                });
    }

    private String setContractContent() {
        String className = mClassModel.getClassName();
        String content = mContent + "public interface " + "View{\n}\n\n"
                + "public interface " + "Presenter{\n}\n\n"
                + "public interface " + "Model{\n}\n\n"
                + "\n}";

        return content;
    }


}