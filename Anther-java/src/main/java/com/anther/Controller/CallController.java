package com.anther.Controller;

import com.anther.annotation.GlobalInterceptor;
import com.anther.entity.dto.*;
import com.anther.entity.vo.ResponseVO;
import com.anther.service.CallService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@RestController("callController")
@RequestMapping("/call")
@Validated
public class CallController extends ABaseController {

    @Resource
    private CallService callService;

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    @GlobalInterceptor
    public ResponseVO createJson(@Valid @RequestBody CallCreateDto dto) {
        return getSuccessResponseVO(callService.createCall(dto, getTokenUserInfo()));
    }

    @PostMapping(value = "/create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    @GlobalInterceptor
    public ResponseVO createForm(@Valid CallCreateDto dto) {
        return getSuccessResponseVO(callService.createCall(dto, getTokenUserInfo()));
    }

    @PostMapping(value = "/accept", consumes = MediaType.APPLICATION_JSON_VALUE)
    @GlobalInterceptor
    public ResponseVO acceptJson(@Valid @RequestBody CallAcceptDto dto) {
        return getSuccessResponseVO(callService.acceptCall(dto, getTokenUserInfo()));
    }

    @PostMapping(value = "/accept", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    @GlobalInterceptor
    public ResponseVO acceptForm(@Valid CallAcceptDto dto) {
        return getSuccessResponseVO(callService.acceptCall(dto, getTokenUserInfo()));
    }

    @PostMapping(value = "/reject", consumes = MediaType.APPLICATION_JSON_VALUE)
    @GlobalInterceptor
    public ResponseVO rejectJson(@Valid @RequestBody CallRejectDto dto) {
        return getSuccessResponseVO(callService.rejectCall(dto, getTokenUserInfo()));
    }

    @PostMapping(value = "/reject", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    @GlobalInterceptor
    public ResponseVO rejectForm(@Valid CallRejectDto dto) {
        return getSuccessResponseVO(callService.rejectCall(dto, getTokenUserInfo()));
    }

    @PostMapping(value = "/cancel", consumes = MediaType.APPLICATION_JSON_VALUE)
    @GlobalInterceptor
    public ResponseVO cancelJson(@Valid @RequestBody CallCancelDto dto) {
        return getSuccessResponseVO(callService.cancelCall(dto, getTokenUserInfo()));
    }

    @PostMapping(value = "/cancel", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    @GlobalInterceptor
    public ResponseVO cancelForm(@Valid CallCancelDto dto) {
        return getSuccessResponseVO(callService.cancelCall(dto, getTokenUserInfo()));
    }

    @PostMapping(value = "/hangup", consumes = MediaType.APPLICATION_JSON_VALUE)
    @GlobalInterceptor
    public ResponseVO hangupJson(@Valid @RequestBody CallHangupDto dto) {
        return getSuccessResponseVO(callService.hangupCall(dto, getTokenUserInfo()));
    }

    @PostMapping(value = "/hangup", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    @GlobalInterceptor
    public ResponseVO hangupForm(@Valid CallHangupDto dto) {
        return getSuccessResponseVO(callService.hangupCall(dto, getTokenUserInfo()));
    }

    @GetMapping("/getCallInfo")
    @GlobalInterceptor
    public ResponseVO getCallInfo(@NotBlank String callId) {
        return getSuccessResponseVO(callService.getCallInfo(callId, getTokenUserInfo().getUserId()));
    }

    @GetMapping("/getRtcConfig")
    @GlobalInterceptor
    public ResponseVO getRtcConfig() {
        return getSuccessResponseVO(callService.getRtcConfig());
    }
}
