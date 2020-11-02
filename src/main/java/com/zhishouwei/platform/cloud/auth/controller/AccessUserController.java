package com.zhishouwei.platform.cloud.auth.controller;

import com.alibaba.fastjson.JSONObject;
import com.zhishouwei.platform.cloud.auth.server.AccountService;
import com.zhishouwei.platform.cloud.auth.entity.OauthClientDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("uaa")
public class AccessUserController {
    
    private static final int RESPCODE_SECCESS = 200;
    private static final int RESPCODE_ERROR_DETAILS = 400;
    private static final int RESPCODE_ERROR_CLIENTIDLENGTH = 401;
    private static final int RESPCODE_ERROR_CLIENTIDEXIST = 300;
    private static final int RESPCODE_ERROR_CLIENTSECRET = 402;
    private static final int RESPCODE_ERROR_REDIRECTURI = 403;
    private static final int RESPCODE_ERROR_INSERT = 404;

    private final int maxLength = 20;
    private final int minLength = 5;

    @Resource
    private AccountService service;

    /**
     * 认证企业的clientId是否存在
     * @param clientId 需要认证的clientId
     * @return 是否存在
     */
    @RequestMapping(value = "checkClientId", method = {RequestMethod.GET, RequestMethod.POST})
    public JSONObject checkClientId(@RequestParam("clientId") String clientId) {
        JSONObject resp = new JSONObject();
        if (clientId == null || clientId.length() > maxLength || clientId.length() < minLength) {
            resp.put("code", RESPCODE_ERROR_CLIENTIDLENGTH);
            resp.put("message", "clientId不合法，请上传5-20位的clientId");
        }
        boolean chencked = service.checkClientId(clientId);
        if (chencked) {
            resp.put("code", RESPCODE_SECCESS);
            resp.put("message", "clientId可以使用");
        } else {
            resp.put("code", RESPCODE_ERROR_CLIENTIDEXIST);
            resp.put("message", "clientId已存在，请尝试使用新的clientId");
        }
        return resp;
    }

    /**
     * 添加开发者信息
     * @param details 用户信息
     * @return 返回是否添加成功
     */
    @RequestMapping(value = "applyDeveloper", method = {RequestMethod.POST})
    public JSONObject applyDeveloper(@RequestBody OauthClientDetails details) {
        JSONObject resp = new JSONObject();
        if (details != null) {
            if (details.getClientId() == null
                    || details.getClientId().length() > maxLength
                    || details.getClientId().length() < minLength) {
                resp.put("code", RESPCODE_ERROR_CLIENTIDLENGTH);
                resp.put("message", "clientId不合法，请上传5-20位的clientId");
            } else if (details.getClientSecret() == null
                    || details.getClientSecret().length() == 0) {
                resp.put("code", RESPCODE_ERROR_CLIENTSECRET);
                resp.put("message", "clientSecret不合法，请上传clientSecret");
            } else if (details.getWebServerRedirectUri() == null
                    || details.getWebServerRedirectUri().length() == 0
                    || !details.getWebServerRedirectUri().startsWith("http")) {
                resp.put("code", RESPCODE_ERROR_REDIRECTURI);
                resp.put("message", "RedirectUri不合法，RedirectUri 必需是一个合法的http或https请求。以http://或https://开头");
            } else {
                boolean isOK = service.insert(details);
                if (isOK) {
                    resp.put("code", RESPCODE_SECCESS);
                    resp.put("message", "数据申请成功");
                } else {
                    resp.put("code", RESPCODE_ERROR_INSERT);
                    resp.put("message", "数据申请失败");
                }
            }
        } else {
            resp.put("code", RESPCODE_ERROR_DETAILS);
            resp.put("message", "无效的数据请求");
        }
        return resp;
    }
}
