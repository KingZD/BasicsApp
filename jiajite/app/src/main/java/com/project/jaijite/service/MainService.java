package com.project.jaijite.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.project.jaijite.bean.Info;
import com.project.jaijite.bean.Task;
import com.project.jaijite.util.ToastUtils;

import org.apache.http.util.ByteArrayBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class MainService extends Service implements Runnable {
    private static Socket client_socket = null;
    private static boolean isRun = false;
    // Task Queue
    private static Queue<Task> tasks = new LinkedList<Task>();
    private static final int READ_TIME_OUT = 5;
    private static ArrayList<Activity> appActivities = new ArrayList<Activity>();
    static String task_result = "";
    private static boolean is_retry = true;

    @Override
    public void onCreate() {
        super.onCreate();
        isRun = true;
        System.out.println("start server");
    }

    public static void newTask(Task t, boolean flag) {
        ToastUtils.showShortSafe(JSON.toJSONString(t));
        is_retry = flag;
        tasks.add(t);
        new Thread(new Runnable() {

            @Override
            public void run() {
                Task task = null;
                int try_count = 0;
                if (!tasks.isEmpty()) {
                    task = tasks.poll();// The change removes the task from the task
                    // queue after executing tasks
                    if (null != task) {
                        if (createrSocket()) {
                            if (is_retry) {
                                //连接3次
                                while (try_count < 3) {
                                    try_count++;
                                    task_result = doTask(task);
                                    if (!task_result.equals("")) {
                                        // 更新ui
                                        try_count = 0;
                                        Message msg = new Message();
                                        msg.what = 1;
                                        task.setTaskResult(task_result);
                                        msg.obj = task;
                                        handler.sendMessage(msg);
                                        break;
                                    }
                                    try {
                                        Thread.sleep(1000);
                                    } catch (Exception e) {
                                        closeSocket(client_socket);
                                        e.printStackTrace();
                                        Message msg = new Message();
                                        msg.what = 3;
                                        handler.sendMessage(msg);
                                    }
                                }

                            } else {
                                task_result = doTask(task);
                            }
                            if (try_count >= 3 && is_retry) {
                                //重连3次失败
                                closeSocket(client_socket);
                                try_count = 0;
                                Message msg = new Message();
                                msg.what = 2;
                                handler.sendMessage(msg);
                            }
                        }
                    }

                }
            }


        }).start();
    }

    private static void closeSocket(Socket socket) {
        if (socket != null) {
            try {
                socket.close();
                client_socket = null;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private static boolean createrSocket() {
        try {
            client_socket = new Socket(Info.SERVER_ADDR, Info.PORT);
        } catch (IOException e) {
            ToastUtils.showShortSafe("连接错误："+e.getLocalizedMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void run() {
    }


    @SuppressLint("HandlerLeak")
    static Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case 1:
                    Task task = (Task) msg.obj;
                    ToastUtils.showShortSafe(JSON.toJSONString(task));
                    break;
                case 2:
                    ToastUtils.showShortSafe("连接失败，请检查网络重新尝试操作");
                    break;
                case 3:
                    ToastUtils.showShortSafe("连接失败，请检查网络");
                    break;
                default:
                    break;
            }

        }
    };

    public static Activity getActivityByName(String name) {

        if (!appActivities.isEmpty()) {
            for (Activity activity : appActivities) {
                if (null != activity) {
                    if (activity.getClass().getName().indexOf(name) > 0) {
                        return activity;
                    }
                }
            }
        }

        return null;

    }

    private static String doTask(Task task) {
        String command = task.getCommand();
        PrintWriter out = null;
        InputStream reader = null;
        String resultStr = "";
        try {
            client_socket.setSoTimeout(5 * 1000);
            out = new PrintWriter(client_socket.getOutputStream());
            out.println(command);
            out.flush();
            if (is_retry) {
                reader = client_socket.getInputStream();
                byte[] buffer = new byte[256];
                int lenght;
                int read_counts = 0;
                while (read_counts < READ_TIME_OUT) {
                    if (reader.available() != 0) {
                        if ((lenght = reader.read(buffer)) != -1) {

                            ByteArrayBuffer byteBuffer = new ByteArrayBuffer(lenght);
                            byteBuffer.append(buffer, 0, lenght);
                            byte[] data = byteBuffer.buffer();

                            resultStr = new String(data, "UTF-8");
                            System.out.println("send command resultStr====[" + resultStr + "]");
                        }
                    }
                    if (!resultStr.equals("") && command.equals("GETLED")) {
                        closeSocket(client_socket);
                        return resultStr;
                    }
                    if (!resultStr.equals("") && resultStr.equals(command)) {
                        closeSocket(client_socket);
                        return resultStr;
                    }
                    read_counts++;
                    Thread.sleep(1 * 1000);
                }
            }

        } catch (Exception ex) {
            System.out.println("error:" + ex.toString());
            return "";
        }
        return "";
    }


    public static void addActivity(Activity activity) {
        appActivities.add(activity);

    }

    public static void removeActivity(Activity activity) {
        appActivities.remove(activity);

    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tasks.clear();
        isRun = false;
		/*try
		{
			if (client_socket != null)
			{
				client_socket.close();
				System.out.println("client Socket close");
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

    }

}
