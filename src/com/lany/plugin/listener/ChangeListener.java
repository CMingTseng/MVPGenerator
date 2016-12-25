package com.lany.plugin.listener;


import com.lany.plugin.model.MethodEntity;

/**
 * Created by jiana on 12/12/16.
 */
public interface ChangeListener {
    void add(int type, MethodEntity methodEntity);
    void del(int type, MethodEntity methodEntity);
}
