package com.lany.plugin.model;

import com.intellij.psi.PsiDirectory;


public class InitEntity {
    private PsiDirectory[] psiDirectories;

    public InitEntity(PsiDirectory[] psiDirectories) {
        this.psiDirectories = psiDirectories;
    }

    public PsiDirectory[] getPsiDirectories() {
        return psiDirectories;
    }

    public void setPsiDirectories(PsiDirectory[] psiDirectories) {
        this.psiDirectories = psiDirectories;
    }
}
