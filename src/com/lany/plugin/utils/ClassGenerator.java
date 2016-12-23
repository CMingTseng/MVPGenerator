package com.lany.plugin.utils;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.vfs.VirtualFile;
import com.lany.plugin.Constants;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 类生产器
 */
public class ClassGenerator {

    public static void createInterface(String path, String className, int mode) throws IOException {
        String type = null;
        if (mode == Constants.MODEL) {
            type = "Model";
        } else if (mode == Constants.PRESENTER) {
            type = "Presenter";
        } else if (mode == Constants.VIEW) {
            type = "View";
        }
        String dir = path + type.toLowerCase() + "/";
        path = dir + className + type + ".java";
        File dirs = new File(dir);
        System.out.println("dirs = " + dir);
        File file = new File(path);
        if (!dirs.exists()) {
            dirs.mkdir();
        }
        file.createNewFile();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Writer w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
        BufferedWriter writer = new BufferedWriter(w);
        String packageName = getPackageName(path);

        writer.write("package " + packageName + type.toLowerCase() + ";");
        writer.newLine();
        writer.newLine();
        writer.write("/**\n* Created by " + System.getProperty("user.name") + " on " + sdf.format(date) + "\n*/");
        writer.newLine();
        writer.newLine();
        writer.write("public interface " + className + type + "{");
        writer.newLine();
        writer.newLine();
        writer.write("}");

        writer.flush();
        writer.close();
    }

    /**
     * 用于contract 模式文件 以及presenter模式 实现类 的创建
     *
     * @param path
     * @param className
     * @param classFullName
     * @param mode
     * @param tag
     * @throws IOException
     */
    public static void createImplClass(String path, String className, String classFullName, int mode, int tag) throws IOException {
        String type = null;
        if (mode == Constants.MODEL) {
            type = "Model";
        } else if (mode == Constants.PRESENTER) {
            type = "Presenter";
        }
        //开始创建实现文件
        String dir = path + type.toLowerCase() + "/impl/";

        String interfaceName = className + type;
        String implClassName = interfaceName + "Impl";

        path = dir + implClassName + ".java";
        File dirs = new File(dir);
        File file = new File(path);
        String packageName = getPackageName(path);
        System.out.println(packageName + "   path:" + path);
        if (!dirs.exists()) {
            dirs.mkdirs();
        }
        file.createNewFile();

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

        Writer w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
        BufferedWriter writer = new BufferedWriter(w);
        writer.write("package " + packageName + "impl;");
        writer.newLine();
        if (tag == Constants.CONTRACT) {
            writer.write("import " + packageName + "contract." + classFullName + ";");
        }
        writer.write("import " + packageName + interfaceName + ";");
        writer.newLine();
        writer.newLine();
        writer.write("/**\n* Created by " + System.getProperty("user.name") + " on " + sdf.format(date) + "\n*/");
        writer.newLine();
        writer.newLine();
        if (tag == Constants.CONTRACT) {
            writer.write("public class " + className + type + "Impl implements " +
                    classFullName + "." + type + "{");
        } else if (tag == Constants.PRESENTER) {

            writer.write("public class " + className + type + "Impl implements " +
                    className + type + "{");
        }
        writer.newLine();
        writer.newLine();
        writer.write("}");
        writer.flush();
        writer.close();
    }

    private static String getPackageName(String path) {
        String[] strings = path.split("/");
        StringBuilder packageName = new StringBuilder();
        int index = 0;
        int length = strings.length;
        for (int i = 0; i < strings.length; i++) {
            if (strings[i].equals("com")
                    || strings[i].equals("org")
                    || strings[i].equals("cn")
                    || strings[i].equals("net")
                    || strings[i].equals("me")
                    || strings[i].equals("io")
                    || strings[i].equals("tech")
                    ) {
                index = i;
                break;
            }
        }
        for (int j = index; j < length - 2; j++) {
            packageName.append(strings[j] + ".");
        }
        return packageName.toString();
    }

    public static String getCurrentPath(AnActionEvent e, String classFullName) {
        VirtualFile currentFile = DataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        String path = currentFile.getPath().replace(classFullName + ".java", "");
        return path;
    }
}
