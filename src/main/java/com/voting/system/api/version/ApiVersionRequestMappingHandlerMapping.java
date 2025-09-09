package com.voting.system.api.version;

import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

public class ApiVersionRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    @Override
    protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
        ApiVersion apiVersion = handlerType.getAnnotation(ApiVersion.class);
        return apiVersion != null ? new ApiVersionCondition(apiVersion.value()) : null;
    }

    @Override
    protected RequestCondition<?> getCustomMethodCondition(Method method) {
        ApiVersion apiVersion = method.getAnnotation(ApiVersion.class);
        return apiVersion != null ? new ApiVersionCondition(apiVersion.value()) : null;
    }

    public static class ApiVersionCondition implements RequestCondition<ApiVersionCondition> {
        private final String[] versions;

        public ApiVersionCondition(String[] versions) {
            this.versions = versions;
        }

        @Override
        public ApiVersionCondition combine(ApiVersionCondition other) {
            return new ApiVersionCondition(other.versions);
        }

        @Override
        public ApiVersionCondition getMatchingCondition(HttpServletRequest request) {
            String acceptVersion = request.getHeader("Accept-Version");
            if (acceptVersion == null) {
                acceptVersion = request.getParameter("version");
            }
            if (acceptVersion == null) {
                acceptVersion = "v1"; 
            }

            for (String version : versions) {
                if (version.equals(acceptVersion)) {
                    return this;
                }
            }
            return null;
        }

        @Override
        public int compareTo(ApiVersionCondition other, HttpServletRequest request) {
            return 0;
        }
    }
}
