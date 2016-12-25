package com.lany.plugin.listener;


import com.lany.plugin.model.EditEntity;

/**
 * 编辑完成监听
 */
public interface EditorListener {
    void editOver(EditEntity editEntity);
}
