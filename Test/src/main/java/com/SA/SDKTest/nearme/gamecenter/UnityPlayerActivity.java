package com.SA.SDKTest.nearme.gamecenter;

import com.unity3d.player.*;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.nearme.game.sdk.GameCenterSDK;
import com.nearme.game.sdk.callback.*;
import com.nearme.game.sdk.common.util.AppUtil;
import com.nearme.game.sdk.common.model.biz.PayInfo;
import android.widget.Toast;
import java.util.Random;
import com.nearme.platform.opensdk.pay.PayResponse;
import android.app.Activity;
import com.oppo.mobad.api.InitParams;
import com.oppo.mobad.api.MobAdManager;
import com.oppo.mobaddemo.utils.Constants;

public class UnityPlayerActivity extends Activity
{
    protected UnityPlayer mUnityPlayer; // don't change the name of this variable; referenced from native code

    // Setup activity layout
    @Override protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        mUnityPlayer = new UnityPlayer(this);
        setContentView(mUnityPlayer);
        mUnityPlayer.requestFocus();

        //GameCenter
        String appSecret = "f8bf74abbb7740a9a98af51ba2fe5545";
        GameCenterSDK.init(appSecret, this);

        //AdMob
        InitParams initParams = new InitParams.Builder()
                .setDebug(true)//true打开SDK日志，当应用发布Release版本时，必须注释掉这行代码的调用，或者设为false
                .build();
        /**
         * 调用这行代码初始化广告SDK
         */
        MobAdManager.getInstance().init(this, Constants.APP_ID, initParams);
    }

    public void Button1()
    {
        doPay();
        UnityPlayer.UnitySendMessage("GameManager", "Test", "1");
    }

    public void Button2()
    {
        UnityPlayer.UnitySendMessage("GameManager", "Test", "2");
    }

    public void Exit()
    {
        MobAdManager.getInstance().exit(this);

        AppUtil.exitGameProcess(this);
    }

    private void doPay() {
        // cp支付参数
        int amount = 1; // 支付金额，单位分
        PayInfo payInfo = new PayInfo(System.currentTimeMillis()
                + new Random().nextInt(1000) + "", "自定义字段", amount);
        payInfo.setProductDesc("商品描述");
        payInfo.setProductName("商品名称");
        // 支付结果服务器回调地址，不通过服务端回调发货的游戏可以不用填写~
        payInfo.setCallbackUrl("http://gamecenter.wanyol.com:8080/gamecenter/callback_test_url");

        GameCenterSDK.getInstance().doSinglePay(this, payInfo,
                new SinglePayCallback() {

                    @Override
                    public void onSuccess(String resultMsg) {
                        // add OPPO 支付成功处理逻辑~

                    }

                    @Override
                    public void onFailure(String resultMsg, int resultCode) {
                        // add OPPO 支付失败处理逻辑~
                        if (PayResponse.CODE_CANCEL != resultCode) {

                        } else {
                            // 取消支付处理

                        }
                    }
                    @Override
                    public void onCallCarrierPay(PayInfo payInfo, boolean bySelectSMSPay) {
                        // TODO Auto-generated method stub

                    }
                });
    }

    private PayInfo createTestPayInfo(int amount){
        PayInfo payInfo = new PayInfo(System.currentTimeMillis() +
                new Random().nextInt(1000) + "", "自定义字段", amount);
        payInfo.setProductDesc("商品描述");
        payInfo.setProductName("300符石");
// payInfo.setShowCpSmsChannel(true);//sdk支付界面是否显示运营商短信入口，true显示，false不显示
// payInfo.setUseCachedChannel(true);//设置是否使用上次使用过的支付方式
// 支付结果服务器回调地址，不通过服务端回调发货的游戏可以不用填写~
        payInfo.setCallbackUrl(
                "http://gamecenter.wanyol.com:8080/gamecenter/callback_test_url");
        return payInfo;
    }

    @Override protected void onNewIntent(Intent intent)
    {
        // To support deep linking, we need to make sure that the client can get access to
        // the last sent intent. The clients access this through a JNI api that allows them
        // to get the intent set on launch. To update that after launch we have to manually
        // replace the intent with the one caught here.
        setIntent(intent);
    }

    // Quit Unity
    @Override protected void onDestroy ()
    {
        mUnityPlayer.quit();
        super.onDestroy();
    }

    // Pause Unity
    @Override protected void onPause()
    {
        super.onPause();
        mUnityPlayer.pause();
    }

    // Resume Unity
    @Override protected void onResume()
    {
        super.onResume();
        mUnityPlayer.resume();
    }

    @Override protected void onStart()
    {
        super.onStart();
        mUnityPlayer.start();
    }

    @Override protected void onStop()
    {
        super.onStop();
        mUnityPlayer.stop();
    }

    // Low Memory Unity
    @Override public void onLowMemory()
    {
        super.onLowMemory();
        mUnityPlayer.lowMemory();
    }

    // Trim Memory Unity
    @Override public void onTrimMemory(int level)
    {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_RUNNING_CRITICAL)
        {
            mUnityPlayer.lowMemory();
        }
    }

    // This ensures the layout will be correct.
    @Override public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        mUnityPlayer.configurationChanged(newConfig);
    }

    // Notify Unity of the focus change.
    @Override public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        mUnityPlayer.windowFocusChanged(hasFocus);
    }

    // For some reason the multiple keyevent type is not supported by the ndk.
    // Force event injection by overriding dispatchKeyEvent().
    @Override public boolean dispatchKeyEvent(KeyEvent event)
    {
        if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
            return mUnityPlayer.injectEvent(event);
        return super.dispatchKeyEvent(event);
    }

    // Pass any events not handled by (unfocused) views straight to UnityPlayer
    @Override public boolean onKeyUp(int keyCode, KeyEvent event)     { return mUnityPlayer.injectEvent(event); }
    @Override public boolean onKeyDown(int keyCode, KeyEvent event)   { return mUnityPlayer.injectEvent(event); }
    @Override public boolean onTouchEvent(MotionEvent event)          { return mUnityPlayer.injectEvent(event); }
    /*API12*/ public boolean onGenericMotionEvent(MotionEvent event)  { return mUnityPlayer.injectEvent(event); }
}
