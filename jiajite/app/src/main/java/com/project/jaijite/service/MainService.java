package com.project.jaijite.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.project.jaijite.R;
import com.project.jaijite.bean.Info;
import com.project.jaijite.bean.Task;

import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

//老代码
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
        createNotificationChannel();
    }

    public static void newTask(Task t, boolean flag, TaskListener ls) {
        listener = ls;
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
                                    if (!TextUtils.isEmpty(task_result) && task_result.startsWith("传输错误：")) {
                                        Message msg = new Message();
                                        msg.what = 5;
                                        msg.obj = task_result;
                                        handler.sendMessage(msg);
                                        break;
                                    }
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
                                        Message msg = new Message();
                                        msg.what = 3;
                                        msg.obj = e.getLocalizedMessage();
                                        handler.sendMessage(msg);
                                    }
                                }

                            } else {
                                task_result = doTask(task);
                                if (!TextUtils.isEmpty(task_result) && task_result.startsWith("传输错误：")) {
                                    Message msg = new Message();
                                    msg.what = 5;
                                    msg.obj = task_result;
                                    handler.sendMessage(msg);
                                    return;
                                }
                            }
                            if (try_count >= 3 && is_retry) {
                                //重连3次失败
                                closeSocket(client_socket);
                                try_count = 0;
                                Message msg = new Message();
                                msg.what = 2;
                                msg.obj = "发送指令失败";
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
            String IP = Info.SERVER_ADDR;
            if (!TextUtils.isEmpty(IP))
                IP = IP.replace("IP:", "");
            client_socket = new Socket(IP, Info.PORT);
        } catch (IOException e) {
            e.printStackTrace();
            Message msg = new Message();
            msg.what = 4;
            msg.obj = " 连接错误：" + e.getLocalizedMessage() + " - " + Info.SERVER_ADDR + " - " + Info.PORT;
            handler.sendMessage(msg);
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
                    if (listener != null)
                        listener.taskCallback(JSON.toJSONString(task));
                    break;
                case 2:
                    if (listener != null)
                        listener.taskFailed("status:2 " + msg.obj.toString());
                    break;
                case 3:
                    if (listener != null)
                        listener.taskFailed("status:3 " + msg.obj.toString());
                    break;
                case 4:
                    if (listener != null)
                        listener.taskFailed("status:4 " + msg.obj.toString());
                    break;
                case 5:
                    if (listener != null)
                        listener.taskFailed("status:5 " + msg.obj.toString());
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
            out.print(command);
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
                    if (!resultStr.equals("") && resultStr.equals(command)) {
                        closeSocket(client_socket);
                        return resultStr;
                    }
                    if (!resultStr.equals("")) {
                        closeSocket(client_socket);
                        return resultStr;
                    }
                    read_counts++;
                    Thread.sleep(1 * 1000);
                }
            }
            return "传输错误：数据为空";
        } catch (Exception ex) {
            return "传输错误：" + ex.getLocalizedMessage();
        }
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

    private static TaskListener listener;

    public interface TaskListener {
        void taskCallback(Object... obj);

        void taskFailed(String msg);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // 通知渠道的id
            String id = "my_channel_01";
            // 用户可以看到的通知渠道的名字.
            CharSequence name = getString(R.string.app_name);
//         用户可以看到的通知渠道的描述
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
//         配置通知渠道的属性
            mChannel.setDescription("程序正在运行");
//         最后在notificationmanager中创建该通知渠道 //
            mNotificationManager.createNotificationChannel(mChannel);

            // 为该通知设置一个id
            int notifyID = 1;
            // 通知渠道的id
            String CHANNEL_ID = "my_channel_01";
            // Create a notification and set the notification channel.
            Notification notification = new Notification.Builder(this)
                    .setContentTitle(name).setContentText("程序正在运行")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setChannelId(CHANNEL_ID)
                    .build();
            startForeground(1, notification);
        }
    }

}
