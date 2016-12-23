package com.lany.plugin.dialog;

import com.intellij.openapi.ui.Messages;

import javax.swing.*;
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

    public FunctionDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(createBtn);

        setLocationRelativeTo(null);//居中显示

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


            dispose();
        } else {
            errorHintLabel.setText("content is empty");
        }
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
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
        if (source == createPresenterClassCheckBox) {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                System.out.println("createPresenterClassCheckBox:" + true);
            } else {
                System.out.println("createPresenterClassCheckBox:" + false);
            }
        } else if (source == createPresenterImplClassCheckBox) {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                System.out.println("createPresenterImplClassCheckBox:" + true);
            } else {
                System.out.println("createPresenterImplClassCheckBox:" + false);
            }
        } else if (source == createViewInterfaceCheckBox) {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                System.out.println("createViewInterfaceCheckBox:" + true);
            } else {
                System.out.println("createViewInterfaceCheckBox:" + false);
            }
        } else if (source == createModelClassCheckBox) {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                System.out.println("createModelClassCheckBox:" + true);
            } else {
                System.out.println("createModelClassCheckBox:" + false);
            }
        } else if (source == createModelImplClassCheckBox) {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                System.out.println("createModelImplClassCheckBox:" + true);
            } else {
                System.out.println("createModelImplClassCheckBox:" + false);
            }
        }

    }
}
