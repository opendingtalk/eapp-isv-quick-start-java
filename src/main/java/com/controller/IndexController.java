package com.controller;

import com.config.Constant;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiServiceGetCorpTokenRequest;
import com.dingtalk.api.request.OapiUserGetuserinfoRequest;
import com.dingtalk.api.response.OapiServiceGetCorpTokenResponse;
import com.dingtalk.api.response.OapiUserGetuserinfoResponse;
import com.sdk.DingTalkSignatureUtil;
import com.sdk.URLConstant;
import com.taobao.api.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * DEMO
 * 实现了最简单的免登功能
 */
@RestController
public class IndexController {
	private static final Logger bizLogger = LoggerFactory.getLogger(IndexController.class);

	/**
	 * 欢迎页面
	 */
	@RequestMapping(value = "/welcome", method = RequestMethod.GET)
	public String welcome() {
		return "welcome";
	}


	/**
	 * 钉钉用户登录，显示当前登录的企业和用户
	 * @param corpId			授权企业的CorpId
	 * @param requestAuthCode	免登临时code
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> login(@RequestParam(value = "corpId") String corpId,
									@RequestParam(value = "requestAuthCode") String requestAuthCode) {
		Long start = System.currentTimeMillis();
		OapiServiceGetCorpTokenResponse oapiServiceGetCorpTokenResponse = getOapiServiceGetCorpToken(corpId);

		OapiUserGetuserinfoResponse oapiUserGetuserinfoResponse = getOapiUserGetuserinfo(oapiServiceGetCorpTokenResponse.getAccessToken(),requestAuthCode);

		//3.查询得到当前用户的userId
		String userId = oapiUserGetuserinfoResponse.getUserid();
		//返回结果
		Map<String,Object> resultMap = new HashMap<>();
		resultMap.put("userId",userId);
		resultMap.put("corpId",corpId);
		resultMap.put("errcode",'0');
		bizLogger.info("cost:"+(System.currentTimeMillis()-start));
		return resultMap;
	}

	/**
	 * ISV获取企业访问凭证
	 * @param corpId	授权企业的corpId
	 */
	private OapiServiceGetCorpTokenResponse getOapiServiceGetCorpToken(String corpId) {
		if (corpId == null || corpId.isEmpty()) {
			return null;
		}

		long timestamp = System.currentTimeMillis();
		//正式应用应该由钉钉通过开发者的回调地址动态获取到
		String suiteTicket = "";
		String canonicalString = DingTalkSignatureUtil.getCanonicalString(timestamp, suiteTicket);
		String signature = DingTalkSignatureUtil.computeSignature(Constant.SUITE_SECRET, canonicalString.toString());

		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("timestamp", String.valueOf(timestamp));
		params.put("suiteTicket", suiteTicket);
		params.put("accessKey", Constant.SUITE_KEY);
		params.put("signature", signature);
		String queryString = DingTalkSignatureUtil.paramToQueryString(params, "utf-8");
		DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_GET_CORP_TOKEN + "?" + queryString);
		OapiServiceGetCorpTokenRequest request = new OapiServiceGetCorpTokenRequest();
		OapiServiceGetCorpTokenResponse response;
		try {
			response = client.execute(request);
		} catch (ApiException e) {
			bizLogger.info(e.toString(),e);
			return null;
		}
		if (response == null || !response.isSuccess()) {
			return null;
		}
		return response;
	}



	/**
	 * 通过钉钉服务端API获取用户在当前企业的userId
	 * @param accessToken	企业访问凭证Token
	 * @param code
	 * @return
	 */
	private OapiUserGetuserinfoResponse getOapiUserGetuserinfo(String accessToken, String code) {
		DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_GET_USER_INFO);
		OapiUserGetuserinfoRequest request = new OapiUserGetuserinfoRequest();
		request.setCode(code);
		request.setHttpMethod("GET");

		OapiUserGetuserinfoResponse response = null;
		try {
			response = client.execute(request, accessToken);
		} catch (ApiException e) {
			e.printStackTrace();
			return null;
		}
		if (response == null || !response.isSuccess()) {
			return null;
		}
		return response;
	}
}


