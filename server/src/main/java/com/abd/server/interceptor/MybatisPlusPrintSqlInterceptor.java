package com.abd.server.interceptor;


import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 基于Mybatis Plus的SQL输出拦截器。
 * 完美的输出打印 SQL 及执行时长、statement。
 * 注意：该插件打印复杂长sql性能消耗只有不到30毫秒，是不是很快
 */
@Slf4j
@Intercepts(value = {
        //prepare方法似乎更加靠后执行，因此获得的sql更加准确（目前用来解决某些情况下比如既有更新又有查询的情况下不打印sql的问题）
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}),
        //更新sql也需要打印
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        //加上这个，避免mybatis以为我没代理这个方法,不执行我的拦截器
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        //代理最底层的方法，避免遗漏sql
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
})
//@Component用于注入到spring中，后续会被mybatis-plus-boot-starter中的代码从spring中取出并注入到mybatis中
@Component
public class MybatisPlusPrintSqlInterceptor implements Interceptor {


    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        //1. 执行sql
        long proceedStart = System.currentTimeMillis();
        Object returnValue = null;
        Exception proceedException = null;
        //执行sql时catch下异常，即使sql语法报错，也要打印完整sql
        try {
//            System.out.println("即将执行sql");

            returnValue = invocation.proceed();
        } catch (Exception e) {
            proceedException = e;
        }
        long proceedCost = System.currentTimeMillis() - proceedStart;


        //2.打印sql
        long printBegin = System.currentTimeMillis();

        //MappedStatement这个对象后续要用，这里判断以下避免后面强行getArgs()[0]转MappedStatement失败
        if (!(invocation.getArgs()[0] instanceof MappedStatement)) {
            return returnValue;
        }
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        //部分mybatisplus拦截器内部可能会对Args中的sql进行修改，因此从Args中获取boundSql更接近与真实执行sql
        BoundSql boundSql = null;
        for (int i = invocation.getArgs().length - 1; i >= 0; i--) {
            if (invocation.getArgs()[i] instanceof BoundSql) {
                boundSql = (BoundSql) invocation.getArgs()[i];
            }
        }
        //routingStatementHandler在责任链中更加靠后，获得的sql更加准确
        if (invocation.getTarget() instanceof RoutingStatementHandler) {
            RoutingStatementHandler routingStatementHandler = (RoutingStatementHandler) invocation.getTarget();
            BoundSql boundSql1 = routingStatementHandler.getBoundSql();
            boundSql = boundSql1;
        }
        if (boundSql == null) {
            Object parameter = null;
            if (invocation.getArgs().length > 1) {
                parameter = invocation.getArgs()[1];
            }
            boundSql = mappedStatement.getBoundSql(parameter);
        }
        String statement = mappedStatement.getId();
        Configuration configuration = mappedStatement.getConfiguration();
        showSql(configuration, boundSql, proceedCost, statement);
        long printEnd = System.currentTimeMillis();
//        System.out.println("打印耗时：" + (printEnd - printBegin));

        //3. sql执行异常的报错扔出去
        if (proceedException != null) {
            throw proceedException;
        }
        return returnValue;
    }

    private void showSql(Configuration configuration, BoundSql boundSql, long elapsed, String statement) {
        String logText = formatMessage(elapsed, getSqlWithValues(boundSql.getSql(), buildParameterValues(configuration, boundSql)), statement);
        if (Boolean.TRUE == false) {
            // 打印红色 SQL 日志
            System.err.println(logText);
        } else {
            log.info("\n{}", logText);
        }
    }


    // com.baomidou.mybatisplus.core.MybatisParameterHandler#setParameters
    private static Map<Integer, Object> buildParameterValues(Configuration configuration, BoundSql boundSql) {
        Object parameterObject = boundSql.getParameterObject();
        // ParameterMapping描述参数，包括属性、名称、表达式、javaType、jdbcType、typeHandler等信息
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings != null) {
            Map<Integer, Object> parameterValues = new HashMap<>();
            //类型处理器用于注册所有的 TypeHandler，并建立 Jdbc 类型、JDBC 类型与 TypeHandler 之间的对应关系
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    Object value;
                    String propertyName = parameterMapping.getProperty();
                    if (boundSql.hasAdditionalParameter(propertyName)) { // issue #448 ask first for additional params
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (parameterObject == null) {
                        value = null;
                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else {
                        MetaObject metaObject = configuration.newMetaObject(parameterObject);
                        value = metaObject.getValue(propertyName);
                    }
                    parameterValues.put(i, new Value(value));
                }
            }
            return parameterValues;
        }
        return Collections.emptyMap();
    }

    public static String formatMessage(long elapsed, String sql, String statement) {
        System.out.println();
        return StringUtils.isNotBlank(sql) ?
//                " Consume Time：" + elapsed + " ms "  + " (" + statement + ")" + "  Execute SQL：" + sql.replaceAll("[\\s]+", " ")
                //" Consume Time：" + elapsed + " ms ,  Execute SQL：" + sql.replaceAll("[\\s]+", " ")
//                " Consume Time：" + elapsed + " ms ,  Execute SQL：" + sql
                "======= Sql Logger ======================\n" +
                        statement + "\n" +
                        sql + "\n" +
                        "======= Sql Execute Time: "+elapsed+"ms =======\n"
                : "";
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties0) {
    }

    public static String getSqlWithValues(String statementQuery, Map<Integer, Object> parameterValues) {
        final StringBuilder sb = new StringBuilder();

        // iterate over the characters in the query replacing the parameter placeholders
        // with the actual values
        int currentParameter = 0;
        for (int pos = 0; pos < statementQuery.length(); pos++) {
            char character = statementQuery.charAt(pos);
            if (statementQuery.charAt(pos) == '?' && currentParameter <= parameterValues.size()) {
                // replace with parameter value
                Object value = parameterValues.get(currentParameter);
                sb.append(value != null ? value.toString() : new Value().toString());
                currentParameter++;
            } else {
                sb.append(character);
            }
        }

        return sb.toString();
    }

    /**
     * 基于p6spy的简易数据类型转换类。
     *
     * @author laiqi
     * @date 2023-4-4
     */
    public static class Value {
        public static final String NORM_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

        public static final String databaseDialectDateFormat = NORM_DATETIME_PATTERN;
        public static final String databaseDialectTimestampFormat = NORM_DATETIME_PATTERN;

        public static final String databaseDialectBooleanFormat = "numeric";

        private Object value;

        public Value(Object valueToSet) {
            this();
            this.value = valueToSet;
        }

        public Value() {
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return convertToString(this.value);
        }

        public String convertToString(Object value) {
            String result;

            if (value == null) {
                result = "NULL";
            } else {

                if (value instanceof byte[]) {
                    result = new String((byte[]) value);
                } else if (value instanceof Timestamp) {
                    result = new SimpleDateFormat(databaseDialectTimestampFormat).format(value);
                } else if (value instanceof Date) {
                    result = new SimpleDateFormat(databaseDialectDateFormat).format(value);
                } else if (value instanceof Boolean) {
                    if ("numeric".equals(databaseDialectBooleanFormat)) {
                        result = Boolean.FALSE.equals(value) ? "0" : "1";
                    } else {
                        result = value.toString();
                    }
                } else {
                    result = value.toString();
                }
                result = quoteIfNeeded(result, value);
            }

            return result;
        }

        private String quoteIfNeeded(String stringValue, Object obj) {
            if (stringValue == null) {
                return null;
            }
            if (Number.class.isAssignableFrom(obj.getClass()) || Boolean.class.isAssignableFrom(obj.getClass())) {
                return stringValue;
            } else {
                return "'" + escape(stringValue) + "'";
            }
        }

        private String escape(String stringValue) {
            return stringValue.replaceAll("'", "''");
        }

    }
}


