package com.earth.libbase.network

import android.content.Context
import com.alibaba.sdk.android.oss.ClientConfiguration
import com.alibaba.sdk.android.oss.OSS
import com.alibaba.sdk.android.oss.OSSClient
import com.alibaba.sdk.android.oss.common.OSSLog
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider
import com.earth.libbase.Config

import com.earth.libbase.service.OssService

object OSSManager {

    fun getOss(){

    }

    fun initOSS(context: Context?,endpoint: String?): OssService? {
//        移动端是不安全环境，不建议直接使用阿里云主账号ak，sk的方式。建议使用STS方式。具体参
//        https://help.aliyun.com/document_detail/31920.html
//        注意：SDK 提供的 PlainTextAKSKCredentialProvider 只建议在测试环境或者用户可以保证阿里云主账号AK，SK安全的前提下使用。具体使用如下
//        主账户使用方式
//        String AK = "******";
//        String SK = "******";
//        credentialProvider = new PlainTextAKSKCredentialProvider(AK,SK)
//        以下是使用STS Sever方式。
//        如果用STS鉴权模式，推荐使用OSSAuthCredentialProvider方式直接访问鉴权应用服务器，token过期后可以自动更新。
//        详见：https://help.aliyun.com/document_detail/31920.html
//        OSSClient的生命周期和应用程序的生命周期保持一致即可。在应用程序启动时创建一个ossClient，在应用程序结束时销毁即可。
        val AK = "LTAI5t8dGqGakdcyGUexv5Ew"
        val SK = "q3doaODHWaIPpYDBYyTMAkZtCqsFg4"
        val credentialProvider = OSSPlainTextAKSKCredentialProvider(AK, SK)

        /*      OSSCredentialProvider credentialProvider;
        //使用自己的获取STSToken的类
        String stsServer = ((EditText) findViewById(R.id.stsserver)).getText().toString();
        if (TextUtils.isEmpty(stsServer)) {
            credentialProvider = new OSSAuthCredentialsProvider(Config.STS_SERVER_URL);
            ((EditText) findViewById(R.id.stsserver)).setText(Config.STS_SERVER_URL);
        } else {
            credentialProvider = new OSSAuthCredentialsProvider(stsServer);
        }
*/
        val conf = ClientConfiguration()
        conf.connectionTimeout = 15 * 1000 // 连接超时，默认15秒
        conf.socketTimeout = 15 * 1000 // socket超时，默认15秒
        conf.maxConcurrentRequest = 5 // 最大并发请求书，默认5个
        conf.maxErrorRetry = 2 // 失败后最大重试次数，默认2次
        val oss: OSS = OSSClient(context, endpoint, credentialProvider, conf)
        OSSLog.enableLog()
        return OssService(oss, Config.BUCKET_NAME)
    }

}