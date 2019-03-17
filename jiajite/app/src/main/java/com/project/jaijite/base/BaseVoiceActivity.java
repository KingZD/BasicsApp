package com.project.jaijite.base;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.project.jaijite.bean.Task;
import com.project.jaijite.entity.LightInfo;
import com.project.jaijite.service.MainService;
import com.project.jaijite.util.LogUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public abstract class BaseVoiceActivity extends BaseTitleActivity {
    private int sleep = 100;
    protected LightInfo lightInfo;
    String mr = "01";
    String mg = "01";
    String mb = "01";
    String mp = "00";
    Disposable sendTaskOpenSubscribe;
    boolean isSendOk = true;
    private int Db = 90;
    private int maxRGB = 99;
    private int dbRange = (int) (Db * 0.8f);

    protected void cancel() {
        if (sendTaskOpenSubscribe != null && !sendTaskOpenSubscribe.isDisposed()) {
            sendTaskOpenSubscribe.dispose();
            sendTaskOpenSubscribe = null;
        }
    }

    protected void sendTask(String r, String g, String b, String p) {
        if (!TextUtils.isEmpty(r))
            this.mr = r;
        if (!TextUtils.isEmpty(g))
            this.mg = g;
        if (!TextUtils.isEmpty(b))
            this.mb = b;
        if (!TextUtils.isEmpty(p))
            this.mp = p;
        if (sendTaskOpenSubscribe == null) {
            sendTaskOpenSubscribe = Observable
                    .interval(0, sleep, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            if (isSendOk) {
                                isSendOk = false;
                                sendTaskOpen(mr, mg, mb, mp);
                            }
                        }
                    });
        }
    }


    private void sendTaskOpen(String r, String g, String b, String p) {
        Task task = new Task();
        task.setLedID(lightInfo.getLedId());
        task.setFunction(Task.LIGHT_RGB);
        String ledCGroup = lightInfo.getLedCGroup();
        String ledMGroup = lightInfo.getLedMGroup();
        String groupId = "1000";
        String attribute = "0000010101";
        switch (ledMGroup + ledCGroup) {
            case "00"://单灯+彩灯 灯组只选中A组
                task.setFunction(Task.LIGHT_C);
                //根据高音R，中音G，低音B，一一对应发送RGB值：
                attribute = "0000" + r + g + b;
                setTask(task, groupId, attribute);
                isSendOk = true;
                break;
            case "01"://单灯+白灯 灯组只选中A组
                //直接根据音量大小发送：
                task.setFunction(Task.LIGHT_DB);
                if (TextUtils.equals(p, "0")) p = "1";
                attribute = p;
                setTask(task, groupId, attribute);
                isSendOk = true;
                break;
            case "10"://多灯+彩灯
                if (dbTranserRgb(g) > dbRange) {
                    //当中音超过80%音量时，只发给B组灯：L:01,010100,4,100 0D
                    task.setFunction(Task.LIGHT_OPEN);
                    groupId = "0100";
                    attribute = "100";
                    setTask(task, groupId, attribute);
                    try {
                        Thread.sleep(sleep);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //当中音降低80%以下时，发给B组灯：L:01,010100,4,0 0D
                    task.setFunction(Task.LIGHT_OPEN);
                    groupId = "0100";
                    attribute = "0";
                    setTask(task, groupId, attribute);
                }


                if (dbTranserRgb(r) > dbRange) {
                    //当高音超过80%音量时，只发给C组灯：L:01,010010,4,100 0D
                    task.setFunction(Task.LIGHT_OPEN);
                    groupId = "0010";
                    attribute = "100";
                    setTask(task, groupId, attribute);
                    try {
                        Thread.sleep(sleep);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //当高音降低80%以下时，发给C组灯：L:01,010010,4,0 0D
                    task.setFunction(Task.LIGHT_OPEN);
                    groupId = "0010";
                    attribute = "0";
                    setTask(task, groupId, attribute);
                }


                if (dbTranserRgb(b) > dbRange) {
                    //当低音超过80%音量时，只发给D组灯：L:01,010001,4,100 0D
                    task.setFunction(Task.LIGHT_OPEN);
                    groupId = "0001";
                    attribute = "100";
                    setTask(task, groupId, attribute);
                    try {
                        Thread.sleep(sleep);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //当低音降低80%以下时，发给D组灯：L:01,010001,4,0 0D
                    task.setFunction(Task.LIGHT_OPEN);
                    groupId = "0001";
                    attribute = "0";
                    setTask(task, groupId, attribute);
                }

                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //根据高音R，中音G，低音B，一一对应发送RGB值（只发A组）：
                groupId = "1000";
                task.setFunction(Task.LIGHT_C);
                //根据高音R，中音G，低音B，一一对应发送RGB值：
                attribute = "0000" + r + g + b;
                setTask(task, groupId, attribute);

                isSendOk = true;
                break;
            case "11"://多灯+白灯
                task.setFunction(Task.LIGHT_OPEN);
                if (dbTranserRgb(p) < 25) {
                    //1-25%：L:01,011000,4,100 0D
                    groupId = "1000";
                    attribute = "100";
                } else if (dbTranserRgb(p) < 50) {
                    //26-50%：L:01,011100,4,100 0D
                    groupId = "1100";
                    attribute = "100";
                } else if (dbTranserRgb(p) < 75) {
                    //51-75%：L:01,011110,4,100 0D
                    groupId = "1110";
                    attribute = "100";
                } else if (dbTranserRgb(p) < 100) {
                    //76-100%：L:01,011111,4,100 0D
                    groupId = "1111";
                    attribute = "100";
                }
                setTask(task, groupId, attribute);
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (dbTranserRgb(p) < 1) {
                    //音量小于1%则发：L:01,011111,4,0 0D
                    groupId = "1111";
                    attribute = "0";
                } else if (dbTranserRgb(p) < 26) {
                    //音量小于26%则发：L:01,010111,4,0 0D
                    groupId = "0111";
                    attribute = "0";
                } else if (dbTranserRgb(p) < 51) {
                    //音量小于51%则发：L:01,010011,4,0 0D
                    groupId = "0011";
                    attribute = "0";
                } else if (dbTranserRgb(p) < 76) {
                    //音量小于76%则发：L:01,010001,4,0 0D
                    groupId = "0001";
                    attribute = "0";
                }

                setTask(task, groupId, attribute);
                isSendOk = true;
                break;
        }
    }

//    private void sendTaskOpen(String r, String g, String b, String p) {
//        Task task = new Task();
//        task.setLedID(lightInfo.getLedId());
//        task.setFunction(Task.LIGHT_RGB);
//        String ledCGroup = lightInfo.getLedCGroup();
//        String ledMGroup = lightInfo.getLedMGroup();
//        String groupId = "1000";
//        String attribute = "0000010101";
//        switch (ledMGroup + ledCGroup) {
//            case "00"://单灯+彩灯 灯组只选中A组
//                task.setFunction(Task.LIGHT_C);
//                //根据高音R，中音G，低音B，一一对应发送RGB值：
//                attribute = "0000" + r + g + b;
//                setTask(task, groupId, attribute);
//                break;
//            case "01"://单灯+白灯 灯组只选中A组
//                //直接根据音量大小发送：
//                task.setFunction(Task.LIGHT_OPEN);
//                if (TextUtils.equals(p, "0")) p = "1";
//                attribute = p;
//                setTask(task, groupId, attribute);
//                break;
//            case "10"://多灯+彩灯
//                if (Integer.valueOf(g) > Integer.valueOf(p) * 0.8) {
//                    //当中音超过80%音量时，只发给B组灯：L:01,010100,4,100 0D
//                    task.setFunction(Task.LIGHT_OPEN);
//                    groupId = "0100";
//                    attribute = "100";
//                    setTask(task, groupId, attribute);
//                } else {
//                    //当中音降低80%以下时，发给B组灯：L:01,010100,4,0 0D
//                    task.setFunction(Task.LIGHT_OPEN);
//                    groupId = "0100";
//                    attribute = "0";
//                    setTask(task, groupId, attribute);
//                }
//
//                try {
//                    Thread.sleep(sleep);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                if (Integer.valueOf(r) > Integer.valueOf(p) * 0.8) {
//                    //当高音超过80%音量时，只发给C组灯：L:01,010010,4,100 0D
//                    task.setFunction(Task.LIGHT_OPEN);
//                    groupId = "0010";
//                    attribute = "100";
//                    setTask(task, groupId, attribute);
//                } else {
//                    //当高音降低80%以下时，发给C组灯：L:01,010010,4,0 0D
//                    task.setFunction(Task.LIGHT_OPEN);
//                    groupId = "0010";
//                    attribute = "0";
//                    setTask(task, groupId, attribute);
//                }
//                try {
//                    Thread.sleep(sleep);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                if (Integer.valueOf(b) > Integer.valueOf(p) * 0.8) {
//                    //当低音超过80%音量时，只发给D组灯：L:01,010001,4,100 0D
//                    task.setFunction(Task.LIGHT_OPEN);
//                    groupId = "0001";
//                    attribute = "100";
//                    setTask(task, groupId, attribute);
//                } else {
//                    //当低音降低80%以下时，发给D组灯：L:01,010001,4,0 0D
//                    task.setFunction(Task.LIGHT_OPEN);
//                    groupId = "0001";
//                    attribute = "0";
//                    setTask(task, groupId, attribute);
//                }
//
//                try {
//                    Thread.sleep(sleep);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                //根据高音R，中音G，低音B，一一对应发送RGB值（只发A组）：
//                groupId = "1000";
//                task.setFunction(Task.LIGHT_C);
//                //根据高音R，中音G，低音B，一一对应发送RGB值：
//                attribute = "0000" + r + g + b;
//                setTask(task, groupId, attribute);
//                break;
//            case "11"://多灯+白灯
//                task.setFunction(Task.LIGHT_OPEN);
//                if (Integer.valueOf(p) < 25) {
//                    //1-25%：L:01,011000,4,100 0D
//                    groupId = "1000";
//                    attribute = "100";
//                } else if (Integer.valueOf(p) < 50) {
//                    //26-50%：L:01,011100,4,100 0D
//                    groupId = "1100";
//                    attribute = "100";
//                } else if (Integer.valueOf(p) < 75) {
//                    //51-75%：L:01,011110,4,100 0D
//                    groupId = "1110";
//                    attribute = "100";
//                } else if (Integer.valueOf(p) < 100) {
//                    //76-100%：L:01,011111,4,100 0D
//                    groupId = "1111";
//                    attribute = "100";
//                }
//                setTask(task, groupId, attribute);
//                try {
//                    Thread.sleep(sleep);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                if (Integer.valueOf(p) < 1) {
//                    //音量小于1%则发：L:01,011111,4,0 0D
//                    groupId = "1111";
//                    attribute = "0";
//                } else if (Integer.valueOf(p) < 26) {
//                    //音量小于26%则发：L:01,010111,4,0 0D
//                    groupId = "0111";
//                    attribute = "0";
//                } else if (Integer.valueOf(p) < 51) {
//                    //音量小于51%则发：L:01,010011,4,0 0D
//                    groupId = "0011";
//                    attribute = "0";
//                } else if (Integer.valueOf(p) < 76) {
//                    //音量小于76%则发：L:01,010001,4,0 0D
//                    groupId = "0001";
//                    attribute = "0";
//                }
//                setTask(task, groupId, attribute);
//                break;
//        }
//    }

    private void setTask(Task task, String groupId, String attribute) {
        task.setGroupID(groupId);
        task.setAttribute(attribute);
        task.startCommand(new MainService.TaskListener() {
            @Override
            public void taskCallback(Object... obj) {

            }

            @Override
            public void taskFailed(String msg) {
            }
        });
        Log.i("voice__", task.getCommand());
        LogUtils.i("voiceRGB", mr, mg, mb, mp, dbRange);
    }

    private void sendTaskOpenOld(String r, String g, String b, String p) {
        Task task = new Task();
        task.setLedID(lightInfo.getLedId());
        task.setFunction(Task.LIGHT_RGB);
        String ledCGroup = lightInfo.getLedCGroup();
        String ledMGroup = lightInfo.getLedMGroup();
        String groupId = "1000";
        String attribute = "001001001";
        switch (ledMGroup + ledCGroup) {
            case "00"://单灯+彩灯 灯组只选中A组
                if (Integer.valueOf(r) > Integer.valueOf(p) * 0.8) {
                    groupId = "1000";
                    task.setFunction(Task.LIGHT_OPEN);
                    //当高音超过80%-100%音量时，直接发送：
                    attribute = "40";
                } else if (Integer.valueOf(r) > Integer.valueOf(p) * 0.8) {
                    groupId = "1000";
                    task.setFunction(Task.LIGHT_OPEN);
                    //当低音超过80%-100%音量时，直接发送：
                    attribute = "80";
                } else {
                    task.setFunction(Task.LIGHT_RGB);
                    //根据高音R，中音G，低音B，一一对应发送RGB值：
                    attribute = r + g + b;
                }
                break;
            case "01"://单灯+白灯 灯组只选中A组
                //直接根据音量大小发送：
                task.setFunction(Task.LIGHT_OPEN);
                attribute = p;
                break;
            case "10"://多灯+彩灯
                if (Integer.valueOf(p) < 50) {
                    task.setFunction(Task.LIGHT_RGB);
                    //当音量为1%-50%时，根据高音R，中音G，低音B，一一对应发送RGB值（只发送给A组灯）：
                    attribute = r + g + b;
                } else if (Integer.valueOf(p) < 80) {
                    task.setFunction(Task.LIGHT_RGB);
                    //当音量为50%-80%时，根据高音R，中音G，低音B，一一对应发送RGB值（只发送给A组和B组灯）：
                    groupId = "1100";
                    attribute = r + g + b;
                } else if (Integer.valueOf(r) > Integer.valueOf(p) * 0.8 &&
                        Integer.valueOf(r) < Integer.valueOf(p) * 0.9) {
                    task.setFunction(Task.LIGHT_OPEN);
                    groupId = "0010";
                    //当高音超过80%-90%音量时，只发给C组灯：
                    attribute = "40";
                } else if (Integer.valueOf(r) < Integer.valueOf(p) * 0.8) {
                    task.setFunction(Task.LIGHT_OPEN);
                    groupId = "0010";
                    //当高音降低80%以下时，发给C组灯：
                    attribute = "0";
                } else if (Integer.valueOf(b) > Integer.valueOf(p) * 0.8 &&
                        Integer.valueOf(b) < Integer.valueOf(p) * 0.9) {
                    task.setFunction(Task.LIGHT_OPEN);
                    //当低音超过80%-90%音量时，只发给D组灯：
                    groupId = "0001";
                    attribute = "80";
                } else if (Integer.valueOf(b) < Integer.valueOf(p) * 0.8) {
                    task.setFunction(Task.LIGHT_OPEN);
                    groupId = "0001";
                    //当低音降低80%以下时，发给D组灯：
                    attribute = "0";
                } else if (Integer.valueOf(r) > Integer.valueOf(p) * 0.9 &&
                        Integer.valueOf(b) > Integer.valueOf(p) * 0.9) {
                    task.setFunction(Task.LIGHT_OPEN);
                    groupId = "1111";
                    //当高低音超过90%-100%音量时，发给ABCD组灯
                    attribute = "100";
                } else if (Integer.valueOf(r) < Integer.valueOf(p) * 0.9 &&
                        Integer.valueOf(g) < Integer.valueOf(p) * 0.9) {
                    task.setFunction(Task.LIGHT_OPEN);
                    groupId = "1111";
                    //当高低音降低90%以下时，发给ABCD组灯：
                    attribute = "0";
                }
                break;
            case "11"://多灯+白灯
                task.setFunction(Task.LIGHT_OPEN);
                attribute = p;
                if (Integer.valueOf(p) < 25) {
                    groupId = "1000";
                } else if (Integer.valueOf(p) < 50) {
                    groupId = "0100";
                } else if (Integer.valueOf(p) < 75) {
                    groupId = "0010";
                } else if (Integer.valueOf(p) < 100) {
                    groupId = "0001";
                }
                break;
        }
        task.setGroupID(groupId);
        task.setAttribute(attribute);
        task.startCommand(new MainService.TaskListener() {
            @Override
            public void taskCallback(Object... obj) {

            }

            @Override
            public void taskFailed(String msg) {
            }
        });
        LogUtils.i("voice", JSON.toJSONString(task));
        //发送 function == 4之后发一条0 只有多灯模式下才执行
        if (task.getFunction() == Task.LIGHT_OPEN
                && !TextUtils.equals("0", task.getAttributes())
                && TextUtils.equals(ledMGroup, "1")) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            task.setGroupID("1111");
            task.setAttribute("0");
            task.startCommand(new MainService.TaskListener() {
                @Override
                public void taskCallback(Object... obj) {

                }

                @Override
                public void taskFailed(String msg) {
                }
            });
        }

        LogUtils.i("voice", JSON.toJSONString(task));
    }

    private int dbTranserRgb(String vl) {
        int v = Integer.valueOf(vl);
        return v * Db / maxRGB;
    }
}
