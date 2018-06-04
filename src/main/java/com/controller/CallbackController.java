package com.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.config.Constant;
import com.dingtalk.oapi.lib.aes.DingTalkEncryptException;
import com.dingtalk.oapi.lib.aes.DingTalkEncryptor;
import com.dingtalk.oapi.lib.aes.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * ISV E应用回调信息处理
 */
@RestController
public class CallbackController {

    private static final Logger bizLogger = LoggerFactory.getLogger(IndexController.class);


    @RequestMapping(value = "/suite/callback/{suitekey}", method = RequestMethod.POST)
    @ResponseBody
    public Map<String,String> callback(@RequestParam(value = "signature", required = false) String signature,
                                       @RequestParam(value = "timestamp", required = false) String timestamp,
                                       @RequestParam(value = "nonce", required = false) String nonce,
                                       @RequestBody(required = false ) JSONObject json) {
        try {
            bizLogger.info("begin /suite/callback/");
            DingTalkEncryptor dingTalkEncryptor = new DingTalkEncryptor(Constant.TOKEN, Constant.ENCODING_AES_KEY, Constant.SUITE_KEY);

            // 从post请求的body中获取回调信息的加密数据进行解密处理
            String encryptMsg = json.getString("encrypt");
            String plainText = dingTalkEncryptor.getDecryptMsg(signature,timestamp,nonce,encryptMsg);
            JSONObject obj = JSON.parseObject(plainText);

            // 根据回调数据类型做不同的业务处理
            String eventType = obj.getString("EventType");
            if("check_create_suite_url".equals(eventType)){
                bizLogger.info("第一次设置回调地址检测推送: "+plainText);
            }else if("check_update_suite_url".equals(eventType)) {
                bizLogger.info("更新回调地址检测推送: "+plainText);
            }else if("suite_ticket".equals(eventType)){
                bizLogger.info("套件Ticket数据推送: "+plainText);
            }else if("tmp_auth_code".equals(eventType)){
                bizLogger.info("E应用企业开通数据推送通知: "+plainText);
            }else{
                // 其他类型数据处理
            }

            // 返回success的加密信息表示回调处理成功
            return dingTalkEncryptor.getEncryptedMap("success", System.currentTimeMillis(), Utils.getRandomStr(8));
        } catch (DingTalkEncryptException e) {
            e.printStackTrace();
            return null;
        }

    }
}
