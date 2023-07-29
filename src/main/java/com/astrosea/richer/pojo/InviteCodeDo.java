package com.astrosea.richer.pojo;

import com.astrosea.richer.utils.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


/**
 *  DDL :
 * CREATE TABLE `invite_code` (
 *   `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
 *   `share_url` varchar(64) NOT NULL COMMENT '发起分享链接用户的 url',
 *   `invited_url` varchar(64) NOT NULL COMMENT '被邀请用户的 url',
 *   `invite_code` varchar(64) DEFAULT NULL COMMENT '邀请码',
 *   `status` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否使用，0：未使用，1：已使用',
 *   `other_json` text COMMENT '其他字段',
 *   `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
 *   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
 *   `is_deleted` bit(1) NOT NULL COMMENT '是否删除，0：正常，1：删除',
 *   PRIMARY KEY (`id`) USING BTREE,
 *   UNIQUE KEY `uk_share_url` (`share_url`) USING BTREE
 * ) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='主页链接分享邀请码';
 */
@Data
@TableName("invite_code")
public class InviteCodeDo extends BaseEntity {

    private String shareUrl;

    private String invitedUrl;

    private Long inviteCode;

    private Integer status;

    private String otherJson;

}
