package com.lany.plugin.utils;

import com.intellij.ide.util.DirectoryUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.lany.plugin.model.EditInfo;
import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class PsiHelper {

    public static void create(AnActionEvent e, EditInfo info) throws FileNotFoundException, UnsupportedEncodingException {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                new WriteCommandAction(e.getProject()) {
                    @Override
                    protected void run(@NotNull Result result) throws Throwable {
                        Project project = e.getProject();
                        PsiDirectory moduleDir = PsiDirectoryFactory.getInstance(project).createDirectory(e.getData(PlatformDataKeys.VIRTUAL_FILE));
                        GlobalSearchScope searchScope = GlobalSearchScope.allScope(project);
                        PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();

                        PsiClass classPresenter = createClass(moduleDir, "presenter", info.getPresenterImplName());
                        PsiClass classModel = createClass(moduleDir, "model", info.getModelImplName());
                        if (info.isCreateContract()) {
                            PsiClass classContract = createClass(moduleDir, "contract", info.getContractName());

                            PsiClass viewInterface = factory.createInterface(info.getViewInterfaceName());
                            PsiClass presenterInterface = factory.createInterface(info.getPresenterInterfaceName());
                            PsiClass modelInterface = factory.createInterface(info.getModelInterfaceName());
                            classContract.add(viewInterface);
                            classContract.add(presenterInterface);
                            classContract.add(modelInterface);

                            importClass(classContract, classPresenter, project);
                            importClass(classContract, classModel, project);
                            implementInterface(factory, searchScope, classPresenter, classContract.getName() + "." + presenterInterface.getName());
                            implementInterface(factory, searchScope, classModel, classContract.getName() + "." + modelInterface.getName());

                            openFiles(project, classContract, classPresenter, classModel);
                        } else {
                            PsiClass viewInterface = createInterface(moduleDir, "view", info.getViewInterfaceName());
                            PsiClass presenterInterface = createInterface(moduleDir, "presenter", info.getPresenterInterfaceName());
                            PsiClass modelInterface = createInterface(moduleDir, "model", info.getModelInterfaceName());

                            implementInterface(factory, searchScope, classPresenter, presenterInterface.getName());
                            implementInterface(factory, searchScope, classModel, modelInterface.getName());

                            openFiles(project, classPresenter, classModel, viewInterface, presenterInterface, modelInterface);
                        }
                    }
                }.execute();
            }
        });

    }

    public static void createContract(AnActionEvent e, EditInfo editEntity) {


        //create package and class


//        importContractClass(project, classContract, editEntity.getViewParent());
//        importContractClass(project, classContract, editEntity.getPresenterParent());
//        importContractClass(project, classContract, editEntity.getPresenterParent());
//
//
//        //add parent interface class
//        extendsClass(factory, searchScope, viewInterface, editEntity.getViewParent());
//        extendsClass(factory, searchScope, presenterInterface, editEntity.getPresenterParent());
//        extendsClass(factory, searchScope, modelInterface, editEntity.getModelParent());
//
//        //add method to view,presenter,model interface
//        addMethodToClass(project, viewInterface, editEntity.getView(), false);
//        addMethodToClass(project, presenterInterface, editEntity.getPresenter(), false);
//        addMethodToClass(project, modelInterface, editEntity.getModel(), false);
//
//
//
//        PsiImportStatement importStatement = factory.createImportStatement(classContract);
//        ((PsiJavaFile) classPresenter.getContainingFile()).getImportList().add(importStatement);
//        ((PsiJavaFile) classModel.getContainingFile()).getImportList().add(importStatement);
//        ((PsiJavaFile) classView.getContainingFile()).getImportList().add(importStatement);
//
//        impInterface(factory, searchScope, classPresenter, editEntity.getContractName() + "Contract.Presenter");
//        impInterface(factory, searchScope, classModel, editEntity.getContractName() + "Contract.Model");
//        impInterface(factory, searchScope, classView, editEntity.getContractName() + "Contract.View");
//
//        openFiles(project, classContract, classPresenter, classModel, classView);
    }


    private static void importContractClass(Project project, PsiClass classContract, String viewParent) {
        String[] strings = viewParent.split("\\.");
        if (strings.length < 1) {
            return;
        }
        searchAndImportClass(strings[0], classContract, project);
    }

    private static PsiClass createClass(PsiDirectory moduleDir, String packageName, String className) {
        PsiDirectory subDir = moduleDir.findSubdirectory(packageName);
        if (subDir == null) {
            subDir = moduleDir.createSubdirectory(packageName);
        }
        return JavaDirectoryService.getInstance().createClass(subDir, className);
    }

    private static PsiClass createInterface(PsiDirectory moduleDir, String packageName, String interfaceName) {
        PsiDirectory subDir = moduleDir.findSubdirectory(packageName);
        if (subDir == null) {
            subDir = moduleDir.createSubdirectory(packageName);
        }
        return JavaDirectoryService.getInstance().createInterface(subDir, interfaceName);
    }


    /**
     * implements interface
     *
     * @param factory
     * @param searchScope
     * @param psiClass
     * @param className
     */
    private static void implementInterface(PsiElementFactory factory, GlobalSearchScope searchScope, PsiClass psiClass, String className) {
        PsiJavaCodeReferenceElement pjcre = factory.createFQClassNameReferenceElement(className, searchScope);
        psiClass.getImplementsList().add(pjcre);
    }

    /**
     * extends class
     *
     * @param factory
     * @param searchScope
     * @param psiClass
     * @param className
     */
    private static void extendsClass(PsiElementFactory factory, GlobalSearchScope searchScope, PsiClass psiClass, String className) {
        if (className == null || "".equals(className)) {
            return;
        }
        PsiJavaCodeReferenceElement pjcre = factory.createFQClassNameReferenceElement(className, searchScope);
        psiClass.getExtendsList().add(pjcre);
    }

    /**
     * search and import class
     *
     * @param name
     * @param resClass
     * @param project
     */
    private static void searchAndImportClass(String name, PsiClass resClass, Project project) {
        if ("".equals(name) || "void".equals(name)) return;
        PsiClass importClass = searchClassByName(name, project);
        if (importClass == null) return;
        PsiJavaFile psiJavaFile = ((PsiJavaFile) importClass.getContainingFile());
        String packageName = psiJavaFile.getPackageName();
        if (packageName.contains("java.lang")) return;
        importClass(importClass, resClass, project);
    }

    /**
     * search class by class name.
     *
     * @param name
     * @param project
     * @return
     */
    private static PsiClass searchClassByName(String name, Project project) {
        GlobalSearchScope searchScope = GlobalSearchScope.allScope(project);
        PsiClass[] psiClasses = PsiShortNamesCache.getInstance(project).getClassesByName(name, searchScope);
        if (psiClasses.length == 1) {
            return psiClasses[0];
        }
        if (psiClasses.length > 1) {
            for (PsiClass pc :
                    psiClasses) {
                PsiJavaFile psiJavaFile = (PsiJavaFile) pc.getContainingFile();
                String packageName = psiJavaFile.getPackageName();
                if (List.class.getPackage().getName().equals(packageName) ||
                        packageName.contains("io.xujiaji.xmvp")) {
                    return pc;
                }
            }
        }
        return null;
    }

    /**
     * import class
     *
     * @param importClass
     * @param resClass
     * @param project
     */
    private static void importClass(PsiClass importClass, PsiClass resClass, Project project) {
        if (importClass == null || resClass == null) return;
        PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
        PsiImportStatement importStatement = factory.createImportStatement(importClass);
        PsiJavaFile psiJavaFile = ((PsiJavaFile) resClass.getContainingFile());
        PsiImportList psiImportList = psiJavaFile.getImportList();
        if (psiImportList == null) return;
        for (PsiImportStatement pis : psiImportList.getImportStatements()) {
            if (pis.getText().equals(importStatement.getText())) {
                return;
            }
        }
        psiImportList.add(importStatement);
    }

    /**
     * open mvp's java file.
     *
     * @param project
     */
    private static void openFiles(Project project, PsiClass... psiClasses) {
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        for (PsiClass psiClass : psiClasses) {
            fileEditorManager.openFile(psiClass.getContainingFile().getVirtualFile(), true, true);
        }
    }

    public static PsiDirectory[] dirList(AnActionEvent e) {
        PsiDirectory moduleDir = PsiDirectoryFactory.getInstance(e.getProject()).createDirectory(e.getData(PlatformDataKeys.VIRTUAL_FILE));
        return moduleDir.getSubdirectories();
//        for (PsiDirectory pd : subDirs) {
////            int start = moduleDir.getName().length();
////            int end = pd.getName().length();
////            String name = pd.getName().substring(start, end);
//            System.out.println(pd.getName());
//        }
    }

    public static PsiJavaFile getJavaFile(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        return (PsiJavaFile) psiFile;
    }

    public static PsiClass exist(PsiFile psiFile, String generateClass) {
        PsiClass psiClass = null;
        PsiDirectory psiDirectory = getJavaSrc(psiFile);
        if (psiDirectory == null || psiDirectory.getVirtualFile().getCanonicalPath() == null) {
            return null;
        }

        File file = new File(psiDirectory.getVirtualFile().getCanonicalPath().concat("/")
                .concat(generateClass.trim().replace(".", "/")).concat(".java"));

        String[] strArray = generateClass.replace(" ", "").split("\\.");
        if (TextUtils.isEmpty(generateClass)) {
            return null;
        }
        String className = strArray[strArray.length - 1];
        String packName = generateClass.substring(generateClass.length() - className.length(), generateClass.length());
        if (file.exists()) {
            for (int i = 0; i < strArray.length - 1; i++) {
                psiDirectory = psiDirectory.findSubdirectory(strArray[i]);
                if (psiDirectory == null) {
                    return null;
                }
            }
            PsiFile psiFile1 = psiDirectory.findFile(className + ".java");
            if ((psiFile1 instanceof PsiJavaFile) && ((PsiJavaFile) psiFile1).getClasses().length > 0) {
                psiClass = ((PsiJavaFile) psiFile1).getClasses()[0];
            }
        }
        return psiClass;
    }

    public static PsiDirectory getJavaSrc(PsiFile psiFile) {
        PsiDirectory psiDirectory = null;
        if (psiFile instanceof PsiJavaFileImpl) {
            String packageName = ((PsiJavaFileImpl) psiFile).getPackageName();
            String[] arg = packageName.split("\\.");
            psiDirectory = psiFile.getContainingDirectory();

            for (int i = 0; i < arg.length; i++) {
                psiDirectory = psiDirectory.getParent();
                if (psiDirectory == null) {
                    break;
                }
            }
        }
        return psiDirectory;
    }

    public static File getPackageFile(PsiFile psiFile, String packageName) {
        PsiDirectory psiDirectory = getJavaSrc(psiFile);
        if (psiDirectory == null || psiDirectory.getVirtualFile().getCanonicalPath() == null) {
            return null;
        }

        if (packageName == null) {
            return new File(psiDirectory.getVirtualFile().getCanonicalPath());
        }
        File file = new File(psiDirectory.getVirtualFile().getCanonicalPath().concat("/")
                .concat(packageName.trim().replace(".", "/")));
        if (file.exists()) {
            return file;
        }
        return null;
    }


    public static PsiClass getPsiClass(PsiFile psiFile, Project project, String generateClass) throws Throwable {

        PsiClass psiClass = null;
        PsiDirectory psiDirectory = getJavaSrc(psiFile);

        if (psiDirectory == null || psiDirectory.getVirtualFile().getCanonicalPath() == null) {
            return null;
        }

        File file = new File(psiDirectory.getVirtualFile().getCanonicalPath().concat("/")
                .concat(generateClass.trim().replace(".", "/")).concat(".java"));

        String[] strArray = generateClass.replace(" ", "").split("\\.");
        if (TextUtils.isEmpty(generateClass)) {
            return null;
        }
        String className = strArray[strArray.length - 1];
        String packName = generateClass.substring(0, generateClass.length() - className.length());
        if (file.exists()) {
            for (int i = 0; i < strArray.length - 1; i++) {
                psiDirectory = psiDirectory.findSubdirectory(strArray[i]);
                if (psiDirectory == null) {
                    return null;
                }
            }
            PsiFile psiFile1 = psiDirectory.findFile(className + ".java");
            if ((psiFile1 instanceof PsiJavaFile) && ((PsiJavaFile) psiFile1).getClasses().length > 0) {
                psiClass = ((PsiJavaFile) psiFile1).getClasses()[0];
            }
            if (psiClass != null) {
                FileEditorManager manager = FileEditorManager.getInstance(project);
                manager.openFile(psiClass.getContainingFile().getVirtualFile(), true, true);
            }

        } else {
            if (!file.getParentFile().exists() && !TextUtils.isEmpty(packName)) {
                psiDirectory = createPackageInSourceRoot(packName, psiDirectory);

            } else {
                for (int i = 0; i < strArray.length - 1; i++) {
                    psiDirectory = psiDirectory.findSubdirectory(strArray[i]);
                    if (psiDirectory == null) {
                        return null;
                    }
                }
            }

            psiClass = JavaDirectoryService.getInstance().createClass(psiDirectory, className);
            FileEditorManager manager = FileEditorManager.getInstance(project);
            manager.openFile(psiClass.getContainingFile().getVirtualFile(), true, true);
        }

        return psiClass;
    }

    public static PsiDirectory createPackageInSourceRoot(String packageName, PsiDirectory sourcePackageRoot) {
        return DirectoryUtil.createSubdirectories(packageName, sourcePackageRoot, ".");
    }

    private PsiClass getPsiClassByName(Project project, String cls) {
        GlobalSearchScope searchScope = GlobalSearchScope.allScope(project);
        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
        return javaPsiFacade.findClass(cls, searchScope);
    }
}