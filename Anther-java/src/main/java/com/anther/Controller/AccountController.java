package com.anther.Controller;

import com.anther.entity.vo.CheckCodeVO;
import com.anther.entity.vo.ResponseVO;
import com.anther.entity.vo.UserInfoVO;
import com.anther.exception.BusinessException;
import com.anther.redis.RedisComponent;
import com.anther.service.UserInfoService;
import com.wf.captcha.ArithmeticCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @param
 * @author 吴磊
 * @version 1.0
 * @description: TODO
 * @date 2025/12/4 22:24
 */
@RestController
@RequestMapping("/account")
@Validated
@Slf4j
public class AccountController extends ABaseController {

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private UserInfoService userInfoService;


    /**
     * @description: 验证码
     * @author 吴磊
     * @date 2025/12/4 22:26
     * @version 1.0
     */
    @RequestMapping("/checkCode")
    public ResponseVO checkCode(){
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 48);
        String code = captcha.text();
        String checkCodeKey =redisComponent.saveCheckCode(code);
        System.out.println( code + "  " + checkCodeKey);
        String checkCodeBase64 = captcha.toBase64();
        CheckCodeVO checkCodeVO = new CheckCodeVO();
        checkCodeVO.setCheckCode(checkCodeBase64);
        checkCodeVO.setCheckCodeKey(checkCodeKey);
        return getSuccessResponseVO(checkCodeVO);
    }

    /**
     * @description: 注册
     * @author 吴磊
     * @date 2025/12/4 22:26
     * @version 1.0
     */
    @RequestMapping("/register")
    public ResponseVO register(@NotEmpty String checkCodeKey,
                               @NotEmpty @Email String email,
                               @NotEmpty @Size(max = 20) String password,
                               @NotEmpty @Size(min = 2,max = 20) String nickName,
                               @NotNull Integer sex,
                               @NotEmpty String checkCode){
        try{
//            if (!redisComponent.getCheckCode(checkCodeKey).equals(checkCode)){
            if (!checkCode.equalsIgnoreCase(redisComponent.getCheckCode(checkCodeKey))){
                throw new BusinessException("验证码错误");
            }
            this.userInfoService.register(email,password,nickName,sex);
            return getSuccessResponseVO(null);
        }finally {
            redisComponent.cleanCheckCode(checkCodeKey);
        }
    }


    @RequestMapping("/login")
    public ResponseVO login(@NotEmpty String checkCodeKey,
                            @NotEmpty @Email String email,
                            @NotEmpty String password,
                            @NotEmpty String checkCode) {
        try{
            if (!checkCode.equalsIgnoreCase(redisComponent.getCheckCode(checkCodeKey))){
                throw new BusinessException("验证码错误");
            }
            UserInfoVO userInfoVO = this.userInfoService.login(email, password);
            return getSuccessResponseVO(userInfoVO);
        }finally {
            redisComponent.cleanCheckCode(checkCodeKey);
        }
    }


}
