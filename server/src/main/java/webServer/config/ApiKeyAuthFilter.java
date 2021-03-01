package webServer.config;

import database.APIKeyDao;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;


public class ApiKeyAuthFilter extends GenericFilterBean {
    //This is used in security config when creating a FilterRegistrationBean can accept more endpoints in the future :)
    public static String[] pathsToFilterFor = {"/rest/twofactor/*"};

    private APIKeyDao apiKeyDao;

    public ApiKeyAuthFilter(APIKeyDao apiKeyDao) {
        this.apiKeyDao = apiKeyDao;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;

        String key = req.getHeader("GOREHAM-API-KEY");

        if (key == null) {
            HttpServletResponse resp = (HttpServletResponse) servletResponse;
            String error = "Unauthorized without API Key";

            resp.reset();
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            servletResponse.setContentLength(error .length());
            servletResponse.getWriter().write(error);
            return;
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(key);
        } catch (IllegalArgumentException e) {
            HttpServletResponse resp = (HttpServletResponse) servletResponse;
            String error = "API Key is not a UUID as expected";

            resp.reset();
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            servletResponse.setContentLength(error .length());
            servletResponse.getWriter().write(error);
            return;
        }

        if (apiKeyDao.doesApiKeyExist(uuid)) {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(new ApiKeyAuthentication(apiKeyDao.getUserIdFromApiKey(uuid), AuthorityUtils.NO_AUTHORITIES));

            filterChain.doFilter(servletRequest, servletResponse);

            if (((HttpServletResponse) servletResponse).getStatus() == 200) {
                long userId = (long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                apiKeyDao.increaseUsage(userId);
            }
        }
        else {
            HttpServletResponse resp = (HttpServletResponse) servletResponse;
            String error = "Invalid Api Key";

            resp.reset();
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            servletResponse.setContentLength(error .length());
            servletResponse.getWriter().write(error);
        }
    }
}
