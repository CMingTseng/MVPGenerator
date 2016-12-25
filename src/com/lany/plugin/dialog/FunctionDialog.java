package com.lany.plugin.dialog;

import com.lany.plugin.model.EditEntity;

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

    private OnFunctionDialogListener mListener;
    private EditEntity editEntity;

    public FunctionDialog() {
        setContentPane(contentPane);
        setModal(true);
        setMinimumSize(new Dimension(500, 500));
        getRootPane().setDefaultButton(createBtn);

        setLocationRelativeTo(null);//居中显示

        editEntity = new EditEntity();

        //获取与编辑器关联的模型
        Document doc = inputEdit.getDocument();

        //添加DocumentListener监听器
        doc.addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                Document doc = e.getDocument();
                try {
                    String content = doc.getText(0, doc.getLength()); //返回文本框输入的内容
                    modelImplEdit.setText(content + "ModelImpl");
                    modelInterfaceEdit.setText(content + "Model");
                    viewInterfaceEdit.setText(content + "View");
                    presenterImplEdit.setText(content + "PresenterImpl");
                    presenterInterfaceEdit.setText(content + "Presenter");
                } catch (BadLocationException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {

            }

            @Override
            public void changedUpdate(DocumentEvent e) {

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

    private void onOK() {
        String name = inputEdit.getText().toString().trim();
        System.out.println("name:" + name);
        if (!name.equals("")) {
            String modelImplName = modelImplEdit.getText().toString().trim();
            editEntity.setModelImplName(modelImplName);

            String modelInterfaceName = modelInterfaceEdit.getText().toString().trim();
            editEntity.setModelInterfaceName(modelInterfaceName);

            String viewInterfaceName = viewInterfaceEdit.getText().toString().trim();
            editEntity.setViewInterfaceName(viewInterfaceName);

            String presenterImplName = presenterImplEdit.getText().toString().trim();
            editEntity.setPresenterImplName(presenterImplName);

            String presenterInterfaceName = presenterInterfaceEdit.getText().toString().trim();
            editEntity.setPresenterInterfaceName(presenterInterfaceName);

            editEntity.setName(name);
            System.out.println("editEntity:" + editEntity.toString());
            if (null != mListener) {
                mListener.onCreateBtnClicked(editEntity);
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
            editEntity.setCreatePresenterInterface(isSelected);
            System.out.println("createPresenterClassCheckBox:" + isSelected);
        } else if (source == createPresenterImplClassCheckBox) {
            editEntity.setCreatePresenterImpl(isSelected);
            System.out.println("createPresenterImplClassCheckBox:" + isSelected);
        } else if (source == createViewInterfaceCheckBox) {
            editEntity.setCreateViewInterface(isSelected);
            System.out.println("createViewInterfaceCheckBox:" + isSelected);
        } else if (source == createModelClassCheckBox) {
            editEntity.setCreateModelInterface(isSelected);
            System.out.println("createModelClassCheckBox:" + isSelected);
        } else if (source == createModelImplClassCheckBox) {
            editEntity.setCreateModelImpl(isSelected);
            System.out.println("createModelImplClassCheckBox:" + isSelected);
        }

    }

    public interface OnFunctionDialogListener {
        void onCreateBtnClicked(EditEntity editEntity);

        void onCancelBtnClicked();
    }

    public void setOnFunctionDialogListener(OnFunctionDialogListener listener) {
        this.mListener = listener;
    }
}
