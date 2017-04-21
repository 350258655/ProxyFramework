package com.example.mac.frameworklibrary.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by shake on 17-4-18.
 * 拷贝SO库管理器
 */
public class SoLibManager {

    private static SoLibManager sInstance = new SoLibManager();

    //线程池
    private ExecutorService mSoExecutor = Executors.newCachedThreadPool();

    private static String sNativeLibDir = "";

    private SoLibManager() {

    }

    public static SoLibManager getSoLoader() {
        return sInstance;
    }


    /**
     * 将插件程序的so库拷贝到我们的应用程序安装目录下
     *
     * @param context
     * @param dexPath      plugin path－－－当前目录(当前apk存放sdcard目录)
     * @param nativeLibDir nativeLibDir－－－指定目录（当前应用程序安装目录）
     */
    public void copyPluginSolib(Context context, String dexPath, String nativeLibDir) {


        /**
         * 第一步、获取CPU名字和架构，以及要拷贝的路径
         */
        //获取CPU名字
        String cpuName = getCpuName();
        //获取CPU架构
        String cpuArchitect = getCpuArch(cpuName);
        //获取当前应用指定目录
        sNativeLibDir = nativeLibDir;

        Log.i("TAG", "CPU架构是：" + cpuArchitect);
        long start = System.currentTimeMillis();


        /**
         * 第二步、开始解压插件，并且拷贝.so包
         */
        try {

            // apk本质就是一个压缩包
            // 以下是解压过程
            ZipFile zipFile = new ZipFile(dexPath);
            //获取APK文件的目录列表
            Enumeration<? extends ZipEntry> extries = zipFile.entries();

            while (extries.hasMoreElements()) {

                ZipEntry zipEntry = extries.nextElement();

                if (zipEntry.isDirectory()) {
                    continue;
                }
                //获取压缩文件中，子元素的名字
                String zipEntryName = zipEntry.getName();

                //假如这个子元素是.so库
                if (zipEntryName.endsWith(".so") && zipEntryName.contains(cpuArchitect)) {

                    //是so库
                    final long lastModify = zipEntry.getTime();
                    //判断这个so库是否存在
                    if (lastModify == PluginConfigs.getSoLastModifiedTime(context, zipEntryName)) {
                        Log.i("TAG", "so库已经存在，不需要再拷贝...");
                        continue;
                    }

                    // 拷贝so库是一个耗时操作，不应该放置在主线程，应该放置在子线程中执行
                    mSoExecutor.execute(new CopySoTask(context, zipFile,
                            zipEntry, lastModify));
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取CPU架构
     *
     * @return
     */
    private String getCpuArch(String cpuName) {

        String cpuArch = PluginConstants.CPU_ARMEABI;

        if (cpuName.toLowerCase().contains("arm")) {
            cpuArch = PluginConstants.CPU_ARMEABI;
        } else if (cpuName.toLowerCase().contains("x86")) {
            cpuArch = PluginConstants.CPU_X86;
        } else if (cpuName.toLowerCase().contains("mips")) {
            cpuArch = PluginConstants.CPU_MIPS;
        }
        return cpuArch;
    }

    /**
     * 获取CPU名字
     *
     * @return ARM、ARMV7、X86、MIPS
     */
    private String getCpuName() {

        try {
            FileReader fr = new FileReader("/proc/cpuinfo");
            BufferedReader br = new BufferedReader(fr);

            String text = br.readLine();
            br.close();

            String[] array = text.split(":\\s+", 2);

            if (array.length >= 2) {
                return array[1];
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    private class CopySoTask implements Runnable {

        //.so库名字
        private String mSoFileName;
        //插件文件
        private ZipFile mZipFile;
        //插件子元素
        private ZipEntry mZipEntry;

        private Context mContext;

        private long mLastModifyTime;


        CopySoTask(Context context, ZipFile zipFile, ZipEntry zipEntry,
                   long lastModify) {
            mZipFile = zipFile;
            mContext = context;
            mZipEntry = zipEntry;
            mSoFileName = parseSoFileName(zipEntry.getName());
            mLastModifyTime = lastModify;
        }

        private String parseSoFileName(String zipEntryName) {
            return zipEntryName.substring(zipEntryName.lastIndexOf("/") + 1);
        }


        @Override
        public void run() {

            try {

                /**
                 * 第一步、拷贝.so文件
                 */
                writeSoFileToLibDir();

                /**
                 * 第二步、缓存当前拷贝的so库基本信息
                 * 目的：防止应用程序出现异常，或者用户不合法操作，导致重复加载so库
                 */
                PluginConfigs.setSoLastModifiedTime(mContext,
                        mZipEntry.getName(), mLastModifyTime);


            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        /**
         * 拷贝so文件
         */
        private void writeSoFileToLibDir() throws IOException {
            //创建输入流以及输出流
            InputStream is = mZipFile.getInputStream(mZipEntry);
            FileOutputStream fos = new FileOutputStream(new File(sNativeLibDir, mSoFileName));

            //拷贝
            copy(is, fos);

            //关闭流
            mZipFile.close();

        }

        /**
         * 拷贝过程
         *
         * @param is
         * @param os
         * @throws IOException
         */
        private void copy(InputStream is, OutputStream os) throws IOException {

            if (is == null || os == null) {
                return;
            }


            BufferedInputStream bis = new BufferedInputStream(is);
            BufferedOutputStream bos = new BufferedOutputStream(os);

            //这个应该是获取每次能够读取的大小吧
            int size = getAvailableSize(bis);

            byte[] buf = new byte[size];
            int i = 0;
            while ((i = bis.read(buf, 0, size)) != -1) {
                bos.write(buf, 0, i);
            }


            bos.flush();
            bos.close();
            bis.close();
        }

        /**
         * 每次最大能够获取的大小
         *
         * @param is
         * @return
         * @throws IOException
         */
        private int getAvailableSize(InputStream is) throws IOException {
            if (is == null) {
                return 0;
            }
            int available = is.available();
            return available <= 0 ? 1024 : available;
        }
    }


}
