package com.parolisoft.dbquerywatch.junit5;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enable checking if any query executed triggered by all test methods was detected as potentially slow.
 *
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(CatchSlowQueriesExtension.class)
public @interface CatchSlowQueries {
}