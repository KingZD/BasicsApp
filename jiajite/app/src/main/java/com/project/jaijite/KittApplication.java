package com.project.jaijite;


import android.support.multidex.MultiDexApplication;

import com.alibaba.wireless.security.jaq.JAQException;
import com.alibaba.wireless.security.jaq.SecurityInit;
import com.aliyun.alink.linksdk.channel.mobile.api.IMobileConnectListener;
import com.aliyun.alink.linksdk.channel.mobile.api.MobileChannel;
import com.aliyun.alink.linksdk.channel.mobile.api.MobileConnectConfig;
import com.aliyun.alink.linksdk.channel.mobile.api.MobileConnectState;
import com.aliyun.alink.linksdk.tmp.TmpSdk;
import com.aliyun.alink.linksdk.tmp.api.TmpInitConfig;
import com.aliyun.alink.linksdk.tools.ThreadTools;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientImpl;
import com.aliyun.iot.aep.sdk.apiclient.emuns.Env;
import com.aliyun.iot.aep.sdk.apiclient.hook.IoTAuthProvider;
import com.aliyun.iot.aep.sdk.credential.IoTCredentialProviderImpl;
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialManageImpl;
import com.aliyun.iot.aep.sdk.log.ALog;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.aliyun.iot.aep.sdk.login.oa.OALoginAdapter;
import com.project.jaijite.activity.OALoginActivity;
import com.project.jaijite.util.LogUtils;
import com.tencent.bugly.crashreport.CrashReport;

public class KittApplication extends MultiDexApplication {
    private static KittApplication application;
    String TAG = this.getClass().getName();

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        // 其他 SDK, 仅在 主进程上初始化
        String packageName = this.getPackageName();
        if (!packageName.equals(ThreadTools.getProcessName(this, android.os.Process.myPid()))) {
            return;
        }
        initIoT();
        initSocketChannel();
        initAuth();
        TmpSdk.init(this, new TmpInitConfig(TmpInitConfig.ONLINE));
        CrashReport.initCrashReport(getApplicationContext(), "80d4ac8245", true);
    }

    private void initIoT(){
        // 初始化无线保镖
        try {
            SecurityInit.Initialize(this);
        } catch (JAQException ex) {
            LogUtils.e(TAG, "security-sdk-initialize-failed");
        } catch (Exception ex) {
            LogUtils.e(TAG, "security-sdk-initialize-failed");
        }
        // 初始化 IoTAPIClient
        IoTAPIClientImpl.InitializeConfig config = new IoTAPIClientImpl.InitializeConfig();
        // 国内环境
        config.host = "api.link.aliyun.com";
        // 海外环境，请参考如下设置
        //config.host = “api-iot.ap-southeast-1.aliyuncs.com”;
        config.apiEnv = Env.RELEASE; //只支持RELEASE
        config.authCode =  "114d";

        IoTAPIClientImpl impl = IoTAPIClientImpl.getInstance();
        impl.init(this, config);
        impl.setLanguage("zh-CN");
    }

    private void initSocketChannel() {
        //打开Log 输出
        ALog.setLevel(ALog.LEVEL_DEBUG);

        MobileConnectConfig config = new MobileConnectConfig();
        // 设置 appKey 和 authCode(必填)
        config.appkey = "25358331";//{25358331}
        config.securityGuardAuthcode = "114d";


        // 设置验证服务器（默认不填，SDK会自动使用“API通道SDK“的Host设定）
        config.authServer = "";

        // 指定长连接服务器地址。 （默认不填，SDK会使用默认的地址及端口。默认为国内华东节点。）
        config.channelHost = "";

        // 开启动态选择Host功能。 (默认false，海外环境建议设置为true。此功能前提为ChannelHost 不特殊指定。）
        config.autoSelectChannelHost = false;

        MobileChannel.getInstance().startConnect(this, config, new IMobileConnectListener() {
            @Override
            public void onConnectStateChange(MobileConnectState state) {
                ALog.d(TAG, "onConnectStateChange(), state = " + state.toString());
            }
        });
    }

    private void initAuth(){
        //使用系统默认OA
        OALoginAdapter loginAdapter = new OALoginAdapter(this);
        //如果需要切换到海外环境，请执行下面setDefaultOAHost方法，默认为大陆环境
        //adapter.setDefaultOAHost("sgp-sdk.openaccount.aliyun.com");
        loginAdapter.setDefaultOAHost(null);
        loginAdapter.setDefaultLoginClass(OALoginActivity.class);
        loginAdapter.init("online", "114d");
        LoginBusiness.init(this, loginAdapter, "online");


        IoTCredentialManageImpl.init("25358331");
        IoTAuthProvider provider = new IoTCredentialProviderImpl(IoTCredentialManageImpl.getInstance(this));
        IoTAPIClientImpl.getInstance().registerIoTAuthProvider("iotAuth", provider);
    }

    public static KittApplication getApplication() {
        return application;
    }

}
