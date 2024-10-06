package com.cuctut.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cuctut.common.auth.JwtUtils;
import com.cuctut.common.constant.CommonConsts;
import com.cuctut.common.constant.DatabaseConsts;
import com.cuctut.common.constant.ErrorCodeEnum;
import com.cuctut.common.constant.SystemConfigConsts;
import com.cuctut.common.resp.RestResp;
import com.cuctut.config.exception.BusinessException;
import com.cuctut.user.dao.entity.UserBookshelf;
import com.cuctut.user.dao.entity.UserFeedback;
import com.cuctut.user.dao.entity.UserInfo;
import com.cuctut.user.dao.mapper.UserBookshelfMapper;
import com.cuctut.user.dao.mapper.UserFeedbackMapper;
import com.cuctut.user.dao.mapper.UserInfoMapper;
import com.cuctut.user.dto.req.UserInfoUptReqDto;
import com.cuctut.user.dto.req.UserLoginReqDto;
import com.cuctut.user.dto.req.UserRegisterReqDto;
import com.cuctut.user.dto.resp.UserInfoRespDto;
import com.cuctut.user.dto.resp.UserLoginRespDto;
import com.cuctut.user.dto.resp.UserRegisterRespDto;
import com.cuctut.user.manager.redis.VerifyCodeManager;
import com.cuctut.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 会员模块 服务实现类
 *
 * @author cuctut
 * @since 2024/10/05
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserInfoMapper userInfoMapper;

    private final VerifyCodeManager verifyCodeManager;

    private final UserFeedbackMapper userFeedbackMapper;

    private final UserBookshelfMapper userBookshelfMapper;

    @Override
    public RestResp<UserRegisterRespDto> register(UserRegisterReqDto dto) {
        // 校验图形验证码是否正确
        if (!verifyCodeManager.imgVerifyCodeOk(dto.getSessionId(), dto.getVelCode())) 
            throw new BusinessException(ErrorCodeEnum.USER_VERIFY_CODE_ERROR);
        // 校验手机号是否已注册
        LambdaQueryWrapper<UserInfo> query = new LambdaQueryWrapper<UserInfo>()
                .eq(UserInfo::getUsername, dto.getUsername())
                .last(DatabaseConsts.SqlEnum.LIMIT_1.getSql());
        if (userInfoMapper.selectCount(query) > 0)
            throw new BusinessException(ErrorCodeEnum.USER_NAME_EXIST);
        // 注册成功，保存用户信息
        String password = DigestUtils.md5DigestAsHex(dto.getPassword().getBytes(StandardCharsets.UTF_8));
        UserInfo userInfo = UserInfo.builder()
                .password(password)
                .username(dto.getUsername())
                .nickName(dto.getUsername())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .salt("0")
                .build();
        userInfoMapper.insert(userInfo);
        // 删除验证码
        verifyCodeManager.removeImgVerifyCode(dto.getSessionId());
        // 生成JWT并返回
        return RestResp.ok(
            UserRegisterRespDto.builder()
                .token(JwtUtils.generateToken(userInfo.getId(), SystemConfigConsts.NOVEL_FRONT_KEY))
                .uid(userInfo.getId())
                .build()
        );

    }

    @Override
    public RestResp<UserLoginRespDto> login(UserLoginReqDto dto) {
        // 查询用户信息
        LambdaQueryWrapper<UserInfo> query = new LambdaQueryWrapper<UserInfo>()
                .eq(UserInfo::getUsername, dto.getUsername())
                .last(DatabaseConsts.SqlEnum.LIMIT_1.getSql());
        UserInfo userInfo = userInfoMapper.selectOne(query);
        if (Objects.isNull(userInfo))
            throw new BusinessException(ErrorCodeEnum.USER_ACCOUNT_NOT_EXIST);
        // 判断密码是否正确
        String password = DigestUtils.md5DigestAsHex(dto.getPassword().getBytes(StandardCharsets.UTF_8));
        if (!Objects.equals(userInfo.getPassword(), password))
            throw new BusinessException(ErrorCodeEnum.USER_PASSWORD_ERROR);
        // 登录成功，生成JWT并返回
        return RestResp.ok(
            UserLoginRespDto.builder()
                .token(JwtUtils.generateToken(userInfo.getId(), SystemConfigConsts.NOVEL_FRONT_KEY))
                .uid(userInfo.getId())
                .nickName(userInfo.getNickName())
                .build()
        );
    }

    @Override
    public RestResp<UserInfoRespDto> getUserInfo(Long userId) {
        UserInfo userInfo = userInfoMapper.selectById(userId);
        return RestResp.ok(
                UserInfoRespDto.builder()
                        .nickName(userInfo.getNickName())
                        .userSex(userInfo.getUserSex())
                        .userPhoto(userInfo.getUserPhoto())
                        .build()
        );
    }

    @Override
    public RestResp<Void> updateUserInfo(UserInfoUptReqDto dto) {
        UserInfo userInfo = UserInfo.builder()
                .id(dto.getUserId())
                .nickName(dto.getNickName())
                .userPhoto(dto.getUserPhoto())
                .userSex(dto.getUserSex())
                .build();
        userInfoMapper.updateById(userInfo);
        return RestResp.ok();
    }

    @Override
    public RestResp<Void> saveFeedback(Long userId, String content) {
        if (!StringUtils.hasText(content))
            throw new BusinessException(ErrorCodeEnum.USER_REQUEST_PARAM_ERROR);
        UserFeedback userFeedback = UserFeedback.builder()
                .userId(userId)
                .content(content)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        userFeedbackMapper.insert(userFeedback);
        return RestResp.ok();
    }

    @Override
    public RestResp<Void> deleteFeedback(Long userId, Long id) {
        LambdaQueryWrapper<UserFeedback> query = new LambdaQueryWrapper<UserFeedback>()
                .eq(UserFeedback::getId, id)
                .eq(UserFeedback::getUserId, userId);
        userFeedbackMapper.delete(query);
        return RestResp.ok();
    }

    @Override
    public RestResp<Integer> getBookshelfStatus(Long userId, String bookId) {
        LambdaQueryWrapper<UserBookshelf> query = new LambdaQueryWrapper<UserBookshelf>()
                .eq(UserBookshelf::getUserId, userId)
                .eq(UserBookshelf::getBookId, bookId);
        return RestResp.ok(
            userBookshelfMapper.selectCount(query) > 0 ? CommonConsts.YES : CommonConsts.NO
        );
    }

    @Override
    public RestResp<List<UserInfoRespDto>> listUserInfoByIds(List<Long> userIds) {
        LambdaQueryWrapper<UserInfo> query = new LambdaQueryWrapper<UserInfo>()
                .in(UserInfo::getId, userIds);
        return RestResp.ok(
            userInfoMapper.selectList(query).stream().map(v -> UserInfoRespDto.builder()
                .id(v.getId())
                .username(v.getUsername())
                .userPhoto(v.getUserPhoto())
                .build()).collect(Collectors.toList())
        );
    }
}
