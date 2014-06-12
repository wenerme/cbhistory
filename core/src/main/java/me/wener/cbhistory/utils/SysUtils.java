package me.wener.cbhistory.utils;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import com.google.common.io.Closeables;
import com.google.gson.internal.Streams;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SysUtils
{
    public static String getCurrentDirectory()
    {
        return System.getProperty("user.dir");
    }

    public static String tryGetResourceAsString(String path)
    {
        return tryGetResourceAsString(path, Charsets.UTF_8);
    }
    public static String tryGetResourceAsString(String path, Charset charset)
    {
        String string = null;
        InputStream is = SysUtils.tryGetResource(path);
        if (is != null)
        {
            try
            {
                string = CharStreams.toString(new InputStreamReader(is, charset));
            } catch (IOException e)
            {
                log.error("加载属性文件 " + path + " 出现异常", e);
            } finally
            {
                Closeables.closeQuietly(is);
            }
        }
        return string;
    }
    public static InputStream tryGetResource(String path)
    {
        InputStream is = SysUtils.class.getClassLoader().getResourceAsStream(path);
        log.debug("加载文件 {} {}", path, is == null ? "文件未找到!" : "发现文件.");

        if (is == null)
        {
            try
            {
                File file = new File(path);
                if (file.exists() || (file = file.getAbsoluteFile()).exists())
                {
                    log.debug("在目录中发现文件 {}, 尝试加载", file.getPath());
                    try
                    {
                        is = new FileInputStream(file);
                    } catch (FileNotFoundException ignored)
                    {
                        // 已经判断文件存在
                        log.error("加载文件异常", ignored);
                    }
                }
            } catch (SecurityException ex)
            {
                log.warn("当前环境无法操作本地文件, 加载 {} 失败", path);
            }
        }
        return is;
    }
}
