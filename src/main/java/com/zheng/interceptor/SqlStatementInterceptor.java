package com.zheng.interceptor;

import com.alibaba.fastjson.JSON;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;

/**
 * 实现Interceptor接口，在SQL执行过程前后进行打印SQL执行结果、SQL实行时间、SQL信息、Mapper信息进行日志打印输出
 *
 * @author wyj
 */
@Intercepts(value = {
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class SqlStatementInterceptor implements Interceptor {

    Logger logger = LoggerFactory.getLogger(SqlStatementInterceptor.class);
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object result = null;
        try {
            // SQL日志打印
            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0]; // 获取xml中的一个select/update/insert/delete节点，主要描述的是一条SQL语句
            Object parameter = "";
            if (invocation.getArgs().length > 1) {
                parameter = invocation.getArgs()[1];
            }
            String sqlId = mappedStatement.getId(); // 获取到节点的id,即sql语句的id
            BoundSql boundSql = mappedStatement.getBoundSql(parameter); // BoundSql就是封装myBatis最终产生的sql类
            Configuration configuration = mappedStatement.getConfiguration(); // 获取节点的配置

            // 打印执行SQL名称 And 带参SQL
            if(!"cn.com.esgcc.gwtrip.dttemplate.dao.BizDtRedisJobDao.findAll".equals(sqlId)) {
//                DtLogUtil.logoutInfo("调用{}接口方法 --> 执行的sql:{}", sqlId, getSql(configuration, boundSql));
                logger.info("调用{}接口方法 --> 执行的sql:{}", sqlId, getSql(configuration, boundSql));
            }
            // 执行SQL过程
            result = invocation.proceed();

            // 打印执行SQL名称 And 带参SQL And 执行SQL返回结果
            if(!"cn.com.esgcc.gwtrip.dttemplate.dao.BizDtRedisJobDao.findAll".equals(sqlId)) {
//                DtLogUtil.logoutInfo("调用{}接口方法 --> 执行的sql:{} --> 返回结果:{}", sqlId, getSql(configuration, boundSql), result == null ? null : JSON.toJSONString(result));
                logger.info("调用{}接口方法 --> 执行的sql:{} --> 返回结果:{}", sqlId, getSql(configuration, boundSql), result == null ? null : JSON.toJSONString(result));
            }
            return result;
        } catch (Exception e) {
//            DtLogUtil.logoutError("",e);
            logger.error("",e);
        }
        return null;
    }

    /**
     * 根据节点id获取执行的Mapper name和Mapper method
     *
     * @param sqlId 节点id
     * @return
     */
    public static String[] getMapper(String sqlId) {
        int index = sqlId.lastIndexOf(".");
        String mapper = sqlId.substring(0, index);
        String method = sqlId.substring(index + 1);
        return new String[]{mapper, method};
    }

    /**
     * 获取带参SQL
     *
     * @param configuration 节点的配置
     * @param boundSql      封装myBatis最终产生的sql类
     * @return
     */
    public static String getSql(Configuration configuration, BoundSql boundSql) {
        Object parameterObject = boundSql.getParameterObject();//获取参数
        List<ParameterMapping> parameterMappings = boundSql
                .getParameterMappings();
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");//sql语句中多个空格都用一个空格代替
        if (parameterMappings.size() > 0 && parameterObject != null) {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry(); //获取类型处理器注册器，类型处理器的功能是进行java类型和数据库类型的转换。如果根据parameterObject.getClass(）可以找到对应的类型，则替换
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(parameterObject)));
            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);//MetaObject主要是封装了originalObject对象，提供了get和set的方法用于获取和设置originalObject的属性值,主要支持对JavaBean、Collection、Map三种类型对象的操作
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(obj)));
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        Object obj = boundSql.getAdditionalParameter(propertyName);//该分支是动态sql
                        sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(obj)));
                    } else {
                        sql = sql.replaceFirst("\\?", "缺失");//打印出缺失，提醒该参数缺失并防止错位
                    }
                }
            }
        }
        return sql;
    }

    /**
     * 对SQL进行格式化
     *
     * @param obj
     * @return
     */
    private static String getParameterValue(Object obj) {
        String value = null;
        if (obj instanceof String) {
            value = "'" + obj.toString() + "'";
        } else if (obj instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            value = "'" + formatter.format(obj) + "'";
        } else {
            if (obj != null) {
                value = obj.toString();
            } else {
                value = "";
            }
        }
        return value;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
