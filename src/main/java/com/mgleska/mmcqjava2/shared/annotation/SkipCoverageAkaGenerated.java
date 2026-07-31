package com.mgleska.mmcqjava2.shared.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE, METHOD, CONSTRUCTOR})
public @interface SkipCoverageAkaGenerated {
}
