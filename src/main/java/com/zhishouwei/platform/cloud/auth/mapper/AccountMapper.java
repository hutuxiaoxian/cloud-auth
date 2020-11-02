package com.zhishouwei.platform.cloud.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhishouwei.platform.cloud.auth.entity.OauthClientDetails;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AccountMapper extends BaseMapper<OauthClientDetails> {
    /**
     * 查询oauth 企业注册信息是否存在
     * @param clientId 用户需要验证的客户端id
     * @return 查询到的客户端企业信息数据
     */
    @Select("SELECT * FROM oauth_client_details WHERE client_id = #{clientId}")
    OauthClientDetails findByClientId(@Param("clientId") String clientId);

}
