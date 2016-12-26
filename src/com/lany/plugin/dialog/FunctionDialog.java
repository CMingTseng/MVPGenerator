package com.lany.plugin.dialog;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.intellij.psi.search.GlobalSearchScope;
import com.lany.plugin.model.EditInfo;
import org.apache.http.util.TextUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.*;

public class FunctionDialog extends JDialog implements ItemListener {
    private JPanel contentPane;
    private JButton createBtn;
    private JButton cancelBtn;
    private JTextField inputEdit;
    private JCheckBox createPresenterClassCheckBox;
    private JCheckBox createPresenterImplClassCheckBox;
    private JCheckBox createViewInterfaceCheckBox;
    private JCheckBox createModelClassCheckBox;
    private JCheckBox createModelImplClassCheckBox;
    private JLabel errorHintLabel;

    private JTextField modelImplEdit;
    private JTextField modelInterfaceEdit;
    private JTextField viewInterfaceEdit;
    private JTextField presenterImplEdit;
    private JTextField presenterInterfaceEdit;
    private JCheckBox createContractClassCheckBox;
    private JTextField createContractClassEdit;

    private OnFunctionDialogListener mListener;
    private EditInfo mEditInfo;

    public FunctionDialog() {
        mEditInfo = new EditInfo();


        //---------------------------------------------------------------------
        setTitle("Edit content");
        setContentPane(contentPane);
        setModal(true);
        setMinimumSize(new Dimension(500, 500));
        getRootPane().setDefaultButton(createBtn);

        setLocationRelativeTo(null);//居中显示


        //获取与编辑器关联的模型
        Document doc = inputEdit.getDocument();

        //添加DocumentListener监听器
        doc.addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                autoInput(e.getDocument());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                autoInput(e.getDocument());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                autoInput(e.getDocument());
            }
        });

        inputEdit.addInputMethodListener(new InputMethodListener() {
            @Override
            public void inputMethodTextChanged(InputMethodEvent event) {
                String inputContent = event.getText().toString().trim();
                if (!inputContent.equals("")) {

                    errorHintLabel.setText("");
                } else {
                    errorHintLabel.setText("content is empty");
                }
            }

            @Override
            public void caretPositionChanged(InputMethodEvent event) {

            }
        });


        createBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        cancelBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        createContractClassCheckBox.addItemListener(this);
        createPresenterClassCheckBox.addItemListener(this);
        createPresenterImplClassCheckBox.addItemListener(this);
        createViewInterfaceCheckBox.addItemListener(this);
        createModelClassCheckBox.addItemListener(this);
        createModelImplClassCheckBox.addItemListener(this);

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public void initData(AnActionEvent event,PsiJavaFile javaFile) {
        System.out.println("当前包名:" + javaFile.getPackageName());//com.lany.presenter
        System.out.println("当前类名:" + javaFile.getName());//LoginPresenter.java
        System.out.println("当前类继承的列表:" + javaFile.getImportList().getText());
        System.out.println("当前文件类型:" + javaFile.getFileType());
        System.out.println("当前类父类名称:" + javaFile.getParent().getName());
        System.out.println("当前类内容:" + javaFile.getText());

        mEditInfo.setCurrentPsiDirectory(javaFile.getContainingDirectory());

        String currentJavaFileName = javaFile.getName();
        currentJavaFileName = currentJavaFileName.replace(".java", "");
        if (currentJavaFileName.contains("Presenter")) {
            currentJavaFileName = currentJavaFileName.replace("Presenter", "");
        }
        if (currentJavaFileName.contains("Contract")) {
            currentJavaFileName = currentJavaFileName.replace("Contract", "");
        }
        inputEdit.setText(currentJavaFileName);
    }


    public void initData(AnActionEvent event,PsiDirectory psiDirectory) {
        Project project = event.getProject();
        PsiDirectory moduleDir = PsiDirectoryFactory.getInstance(project).createDirectory(event.getData(PlatformDataKeys.VIRTUAL_FILE));
        System.out.println("当前目录名称:" + moduleDir.getName());
        GlobalSearchScope searchScope = GlobalSearchScope.allScope(project);
        System.out.println("搜索范围:" + searchScope.getDisplayName());
        PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
        System.out.println("factory:" + factory.toString());
    }

    private void refreshProject(AnActionEvent e) {
        e.getProject().getBaseDir().refresh(false, true);
    }

    private void autoInput(Document doc) {
        try {
            String content = doc.getText(0, doc.getLength()); //返回文本框输入的内容
            if (TextUtils.isEmpty(content)) {
                modelImplEdit.setText("");
                modelInterfaceEdit.setText("");
                viewInterfaceEdit.setText("");
                presenterImplEdit.setText("");
                presenterInterfaceEdit.setText("");
                createContractClassEdit.setText("");
            } else {
                modelImplEdit.setText(content + "ModelImpl");
                modelInterfaceEdit.setText(content + "Model");
                viewInterfaceEdit.setText(content + "View");
                presenterImplEdit.setText(content + "PresenterImpl");
                presenterInterfaceEdit.setText(content + "Presenter");
                createContractClassEdit.setText(content + "Contract");
            }
        } catch (BadLocationException e1) {
            e1.printStackTrace();
        }
    }

    private void onOK() {
        String name = inputEdit.getText().toString().trim();
        System.out.println("name:" + name);
        if (!name.equals("")) {
            String modelImplName = modelImplEdit.getText().toString().trim();
            mEditInfo.setModelImplName(modelImplName);

            String contractName = createContractClassEdit.getText().toString().trim();
            mEditInfo.setContractName(contractName);

            String modelInterfaceName = modelInterfaceEdit.getText().toString().trim();
            mEditInfo.setModelInterfaceName(modelInterfaceName);

            String viewInterfaceName = viewInterfaceEdit.getText().toString().trim();
            mEditInfo.setViewInterfaceName(viewInterfaceName);

            String presenterImplName = presenterImplEdit.getText().toString().trim();
            mEditInfo.setPresenterImplName(presenterImplName);

            String presenterInterfaceName = presenterInterfaceEdit.getText().toString().trim();
            mEditInfo.setPresenterInterfaceName(presenterInterfaceName);

            mEditInfo.setName(name);
            System.out.println("EditInfo:" + mEditInfo.toString());
            if (null != mListener) {
                mListener.onCreateBtnClicked(mEditInfo);
            }

            dispose();
        } else {
            errorHintLabel.setText("content is empty");
        }
    }

    private void onCancel() {
        dispose();
        if (null != mListener) {
            mListener.onCancelBtnClicked();
        }
    }

    public static void main(String[] args) {
        FunctionDialog dialog = new FunctionDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    @Override
    public void itemStateChanged(ItemEvent event) {
        //获取改变的复选按键
        Object source = event.getItemSelectable();
        boolean isSelected = event.getStateChange() == ItemEvent.SELECTED;
        if (source == createPresenterClassCheckBox) {
            mEditInfo.setCreatePresenterInterface(isSelected);
            System.out.println("createPresenterClassCheckBox:" + isSelected);
            presenterInterfaceEdit.setVisible(isSelected);
        } else if (source == createPresenterImplClassCheckBox) {
            mEditInfo.setCreatePresenterImpl(isSelected);
            System.out.println("createPresenterImplClassCheckBox:" + isSelected);
            presenterImplEdit.setVisible(isSelected);
        } else if (source == createViewInterfaceCheckBox) {
            mEditInfo.setCreateViewInterface(isSelected);
            System.out.println("createViewInterfaceCheckBox:" + isSelected);
            viewInterfaceEdit.setVisible(isSelected);
        } else if (source == createModelClassCheckBox) {
            mEditInfo.setCreateModelInterface(isSelected);
            System.out.println("createModelClassCheckBox:" + isSelected);
            modelInterfaceEdit.setVisible(isSelected);
        } else if (source == createModelImplClassCheckBox) {
            mEditInfo.setCreateModelImpl(isSelected);
            System.out.println("createModelImplClassCheckBox:" + isSelected);
            modelImplEdit.setVisible(isSelected);
        } else if (source == createContractClassCheckBox) {
            mEditInfo.setCreateContract(isSelected);
            System.out.println("createContractClassCheckBox:" + isSelected);
            createContractClassEdit.setVisible(isSelected);
        }

    }

    public interface OnFunctionDialogListener {
        void onCreateBtnClicked(EditInfo editInfo);

        void onCancelBtnClicked();
    }

    public void setOnFunctionDialogListener(OnFunctionDialogListener listener) {
        this.mListener = listener;
    }
}
