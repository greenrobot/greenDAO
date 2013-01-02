package de.greenrobot.daoexample.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ValidationAnnotation {
	String key();
	int val();
}
