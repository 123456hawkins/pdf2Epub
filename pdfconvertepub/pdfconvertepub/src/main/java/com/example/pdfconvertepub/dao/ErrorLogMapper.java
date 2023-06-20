package com.example.pdfconvertepub.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.pdfconvertepub.domain.ErrorLog;
import org.apache.ibatis.annotations.Mapper;


/**
 * 定时任务日志 Mapper
 * @author songzhe
 * @date 2020/12/29 15:31
 */
@Mapper
public interface ErrorLogMapper extends BaseMapper<ErrorLog> {
}