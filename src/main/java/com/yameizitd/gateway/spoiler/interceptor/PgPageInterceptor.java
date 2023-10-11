package com.yameizitd.gateway.spoiler.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@SuppressWarnings({"rawtypes", "unchecked"})
@Slf4j
@Intercepts({
        @Signature(type = Executor.class, method = PgPageInterceptor.PG_PAGE_INTERCEPTOR_METHOD, args = {
                MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class
        })
})
public class PgPageInterceptor implements Interceptor {
    public static final String PG_PAGE_INTERCEPTOR_METHOD = "query";
    public static final String PG_PAGE_PARAM_NAME = "pgPage";
    public static final String PG_PAGE_COUNT_SUFFIX = "_count";
    private static final List<ResultMapping> EMPTY_RESULT_MAPPING = new ArrayList<>(0);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        log.debug("Into pg page interceptor");
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        Object parameterObject = args[1];
        BoundSql boundSql = mappedStatement.getBoundSql(parameterObject);
        String sql = boundSql.getSql();
        PgPage page = null;
        Object methodParams = boundSql.getParameterObject();
        if (methodParams instanceof Map) {
            // if contains more than one parameter
            Map<String, Object> paramMap = (Map<String, Object>) methodParams;
            page = paramMap.containsKey(PG_PAGE_PARAM_NAME) ? (PgPage) paramMap.get(PG_PAGE_PARAM_NAME) : null;
        } else if (methodParams instanceof PgPage) {
            // one parameter and the parameter is PgPage
            page = (PgPage) methodParams;
        }
        // if PgPage not null, process page logic
        if (page != null) {
            Executor executor = (Executor) invocation.getTarget();
            ResultHandler resultHandler = (ResultHandler) args[3];
            // new a MappedStatement to query count of data
            MappedStatement countMappedStatement = copyMappedStatement(mappedStatement, null, Long.class);
            // new a CacheKey
            CacheKey countCacheKey = executor.createCacheKey(countMappedStatement, parameterObject,
                    RowBounds.DEFAULT, boundSql);
            // generate count sql by the original sql
            String countSql = "select count(*) from (" + sql + ") count_table";
            log.debug("Generated count sql: \n{}", countSql);
            BoundSql countBoundSql = new BoundSql(mappedStatement.getConfiguration(), countSql,
                    boundSql.getParameterMappings(), parameterObject);
            // copy dynamical parameters
            copyAdditionalParams(boundSql, countBoundSql);
            // query count of data
            List<Object> countList = executor.query(countMappedStatement, parameterObject, RowBounds.DEFAULT,
                    resultHandler, countCacheKey, countBoundSql);
            Long total = countList.get(0) != null ? (Long) countList.get(0) : null;
            if (total != null) {
                // init the PgPage
                page.init(page.getNum(), page.getSize(), total);
                // generate the pageable sql by the original sql
                String pageSql = sql + " limit " + page.getSize() + " offset " + (page.getNum() - 1) * page.getSize();
                log.debug("Generated pageable sql: \n{}", pageSql);
                BoundSql pageBoundSql = new BoundSql(mappedStatement.getConfiguration(), pageSql,
                        boundSql.getParameterMappings(), parameterObject);
                // copy dynamical parameters
                copyAdditionalParams(boundSql, pageBoundSql);
                // new a MappedStatement instance and copy original props to the new instance
                MappedStatement pageMappedStatement = copyMappedStatement(mappedStatement,
                        new InnerSqlSource(pageBoundSql), null);
                // replace the first parameter
                invocation.getArgs()[0] = pageMappedStatement;
            }
        }
        // process original method
        return invocation.proceed();
    }

    // new a MappedStatement instance, copy original props to the new instance
    private MappedStatement copyMappedStatement(MappedStatement mappedStatement,
                                                SqlSource sqlSource,
                                                Class<?> resultType) {
        MappedStatement.Builder builder;
        if (sqlSource != null)
            // if sqlSource is not null, this is a modified pageable sql
            builder = new MappedStatement.Builder(mappedStatement.getConfiguration(), mappedStatement.getId(),
                    sqlSource, mappedStatement.getSqlCommandType());
        else
            // if sqlSource is null, this is a count sql
            builder = new MappedStatement.Builder(mappedStatement.getConfiguration(), mappedStatement.getId() +
                    PG_PAGE_COUNT_SUFFIX, mappedStatement.getSqlSource(), mappedStatement.getSqlCommandType());
        if (resultType != null) {
            // if resultType is not null, this is a count sql, usually may generate Long ResultMap
            List<ResultMap> resultMaps = new ArrayList<>();
            ResultMap resultMap = new ResultMap.Builder(mappedStatement.getConfiguration(), mappedStatement.getId(),
                    resultType, EMPTY_RESULT_MAPPING)
                    .build();
            resultMaps.add(resultMap);
            builder.resultMaps(resultMaps);
        } else {
            // if resultType is null, use the original resultType
            builder.resultMaps(mappedStatement.getResultMaps());
        }
        // populate other props
        builder.resource(mappedStatement.getResource());
        builder.fetchSize(mappedStatement.getFetchSize());
        builder.statementType(mappedStatement.getStatementType());
        builder.keyGenerator(mappedStatement.getKeyGenerator());
        builder.timeout(mappedStatement.getTimeout());
        builder.parameterMap(mappedStatement.getParameterMap());
        builder.resultSetType(mappedStatement.getResultSetType());
        builder.cache(mappedStatement.getCache());
        builder.flushCacheRequired(mappedStatement.isFlushCacheRequired());
        builder.useCache(mappedStatement.isUseCache());
        return builder.build();
    }

    // copy dynamical parameters
    private void copyAdditionalParams(BoundSql origin, BoundSql target) {
        origin.getParameterMappings()
                .stream()
                .map(ParameterMapping::getProperty)
                .filter(origin::hasAdditionalParameter)
                .forEach(prop -> target.setAdditionalParameter(prop, origin.getAdditionalParameter(prop)));
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        Interceptor.super.setProperties(properties);
    }

    public static class InnerSqlSource implements SqlSource {
        private final BoundSql boundSql;

        public InnerSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        @Override
        public BoundSql getBoundSql(Object paramObject) {
            return boundSql;
        }
    }
}
