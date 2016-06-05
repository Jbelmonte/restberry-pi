package org.company.core.helper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.digester3.annotations.utils.AnnotationUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.company.core.jaxrs.annotations.Path;
import org.company.core.jaxrs.annotations.Permissions;
import org.company.techtest.api.resources.Resource;

public class IntrospectionHelper {

	public IntrospectionHelper() {
	}

	public static List<String> getRequiredPermissions(Method method) {
		String[] value = (String[]) getMethodAnnotationValue(method, Permissions.class);
		return Arrays.asList(value);
	}

	public static List<String> getRequiredPermissions(Class<?> entityClass) {
		Class<?> resourceClass = entityClass;
		Collection<Class<?>> inspect = getClassesAndInterfaces(resourceClass);

		for (Class<?> clazz : inspect) {
			Annotation path = clazz.getAnnotation(Permissions.class);
			if (path != null) {
				return Arrays.asList((String[]) AnnotationUtils.getAnnotationValue(path));
			}
		}
		return new ArrayList<String>();
	}

	public static String getResourcePath(Class<?> entityClass) {
		Class<?> resourceClass = entityClass;
		Collection<Class<?>> inspect = getClassesAndInterfaces(resourceClass);

		for (Class<?> clazz : inspect) {
			Annotation path = clazz.getAnnotation(Path.class);
			if (path != null) {
				return (String) AnnotationUtils.getAnnotationValue(path);
			}
		}
		return null;
	}

	public static List<Method> getAllMethods(Class<? extends Resource> resourceClass) {
		Collection<Class<?>> inspect = getClassesAndInterfaces(resourceClass);
		inspect.remove(Object.class);

		List<Method> methods = new LinkedList<Method>();
		for (Class<?> clazz : inspect) {
			Method[] classMethods = clazz.getDeclaredMethods();
			for (Method m : classMethods) {
				if (Modifier.isPublic(m.getModifiers())) {
					methods.add(m);
				}
			}
		}
		return methods;
	}

	public static List<Method> filterMethodsWithAnnotationValue(List<Method> methods,
			Class<? extends Annotation> annotationClass, String annotationValue, boolean annotationRequired) {
		List<Method> result = new ArrayList<>();
		for (Method m : methods) {
			Annotation ann = m.getAnnotation(annotationClass);
			if (ann != null) {
				String value = (String) AnnotationUtils.getAnnotationValue(ann);
				if (StringUtils.isEmpty(annotationValue) || annotationValue.equals(value)) {
					result.add(m);
				}
			} else if (!annotationRequired) {
				result.add(m);
			}
		}
		return result;
	}

	public static List<Method> filterMethodsByPath(List<Method> methods, String path) {
		List<Method> result = new ArrayList<Method>(methods.size());
		for (Method m : methods) {
			Annotation ann = m.getAnnotation(Path.class);
			if (ann != null) {
				String methodPath = (String) AnnotationUtils.getAnnotationValue(ann);
				String pattern = methodPath.replaceAll("\\/", "\\\\/").replaceAll("\\:\\w+", "[^\\/]+");
				if (StringUtils.isEmpty(methodPath) && StringUtils.isEmpty(path)
						|| StringUtils.isEmpty(methodPath) && "/".equals(path)
						|| "/".equals(methodPath) && StringUtils.isEmpty(path) || Pattern.matches(pattern, path)) {
					result.add(m);
				}
			} else if (StringUtils.isEmpty(path) || "/".equals(path)) {
				result.add(m);
			}
		}
		return result;
	}

	public static Collection<Class<?>> getClassesAndInterfaces(Class<?> resourceClass) {
		Set<Class<?>> inspect = new HashSet<Class<?>>();
		inspect.add(resourceClass);
		inspect.addAll(ClassUtils.getAllInterfaces(resourceClass));
		inspect.addAll(ClassUtils.getAllSuperclasses(resourceClass));
		return inspect;
	}

	public static Object getMethodAnnotationValue(Method m, Class<? extends Annotation> annotationClass) {
		Annotation ann = m.getAnnotation(annotationClass);
		return AnnotationUtils.getAnnotationValue(ann);
	}

}
