package com.parolisoft.dbquerywatch.internal;

import com.jayway.jsonpath.JsonPath;
import com.parolisoft.dbquerywatch.internal.jdbc.JdbcClient;
import net.ttddyy.dsproxy.proxy.ParameterSetOperation;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringJoiner;
import java.util.stream.Collectors;

class PostgresExecutionPlanAnalyzer extends AbstractExecutionPlanAnalyzer {

    private static final String EXPLAIN_PLAN_QUERY = "EXPLAIN (FORMAT JSON) ";
    private static final List<String> NODE_TYPES = Collections.singletonList("Seq Scan");

    private static final JsonPath JSON_PATH;

    static {
        StringJoiner sj = new StringJoiner("','", "$..[?(@['Node Type'] in ['", "'])]");
        for (String nodeType : NODE_TYPES) {
            sj.add(nodeType);
        }
        JSON_PATH = JsonPath.compile(sj.toString());
    }

    PostgresExecutionPlanAnalyzer(JdbcClient jdbcClient) {
        super(jdbcClient);
    }

    @Override
    public AnalysisResult analyze(String querySql, List<ParameterSetOperation> operations) {
        String planJson = jdbcClient.queryForString(EXPLAIN_PLAN_QUERY + querySql, operations)
            .orElseThrow(NoSuchElementException::new);
        List<Map<String, Object>> plan = JsonPath.parse(planJson).read(JSON_PATH);
        List<Issue> issues = plan.stream()
            .map(p -> {
                String objectName = getString(p, "Relation Name");
                String predicate = getString(p, "Filter");
                return new Issue(IssueType.FULL_ACCESS, objectName, predicate);
            })
            .collect(Collectors.toList());
        return new AnalysisResult(compactJson(planJson), issues);
    }
}
