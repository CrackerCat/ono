package moe.ono.util;


import static moe.ono.constants.Constants.PrekEnableLog;

import android.annotation.SuppressLint;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import moe.ono.config.ConfigManager;
import moe.ono.util.io.FileUtils;

public class LogUtils {
    private static final String LOG_ROOT_DIRECTORY = PathTool.getModuleDataPath() + "/log/";

    private static String getRunLogDirectory() {
        return LOG_ROOT_DIRECTORY + "RunLog" + File.separator;
    }

    private static String getErrorLogDirectory() {
        return LOG_ROOT_DIRECTORY + "ErrorLog" + File.separator;
    }


    /**
     * @return 获取调用此方法的调用栈
     */
    public static String getCallStack() {
        Throwable throwable = new Throwable();
        return getStackTrace(throwable);
    }

    /**
     * 获取堆栈跟踪
     *
     * @param throwable new Throwable || Exception
     * @return 堆栈跟踪
     */
    public static String getStackTrace(Throwable throwable) {
        StringBuilder result = new StringBuilder();
        result.append(throwable).append("\n");
        StackTraceElement[] stackTraceElements = throwable.getStackTrace();
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            //不把当前类加入结果中
            if (stackTraceElement.getClassName().equals(LogUtils.class.getName())) continue;
            result.append(stackTraceElement).append("\n");
        }
        return result.toString();
    }

    public static void addError(Throwable e) {
        addError("Error Log", e.toString(), e);
    }

    public static void addRunLog(Object content) {
        addRunLog("Run Log", content);
    }

    /**
     * 记录运行日志 确保能走到那一行代码
     *
     * @param TAG(文件名) 内容
     */
    public static void addRunLog(String TAG, Object content) {
        addLog(TAG, String.valueOf(content), content, false);
    }

    /**
     * 记录异常
     */
    public static void addError(String TAG, Throwable e) {
        addLog(TAG, e.toString(), e, true);
    }

    /**
     * 记录异常
     *
     * @param TAG         (标签 文件名)
     * @param Description 错误的相关描述
     * @param e     Exception
     */
    public static void addError(String TAG, String Description, Throwable e) {
        addLog(TAG, Description, e, true);
    }


    private static void addLog(String fileName, String Description, Object content, boolean isError) {
        try {
            if (!ConfigManager.getDefaultConfig().getBooleanOrFalse(PrekEnableLog)){
                return;
            }
        } catch (Exception ignored) {}

        String path = (isError ? getErrorLogDirectory() : getRunLogDirectory()) + fileName + ".log";
        StringBuilder stringBuffer = new StringBuilder(getTime());
        stringBuffer.append("\n").append(Description);
        if (content instanceof Exception) {
            stringBuffer.append("\n").append(getStackTrace((Exception) content));
        }
        stringBuffer.append("\n\n");
        FileUtils.writeTextToFile(path, stringBuffer.toString(), true);
    }

    public static String getTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("[yyyy/MM/dd HH:mm:ss]");
        Calendar calendar = Calendar.getInstance();
        return df.format(calendar.getTime());
    }

    public static void addError(String TAG, String msg) {
        addLog(TAG, msg, null, true);
    }
}
