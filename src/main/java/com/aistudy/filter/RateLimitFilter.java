package com.aistudy.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Rate-limiting filter that protects AI endpoints and auth endpoints.
 *
 * AI endpoints (/summary, /mcq, /flashcards):
 *   - 10 POST requests per minute per session
 *
 * Auth endpoints (/login, /register):
 *   - 5 POST requests per minute per IP
 *
 * GET requests are not rate-limited.
 * Returns HTTP 429 when the limit is exceeded.
 */
@WebFilter(urlPatterns = {"/summary", "/mcq", "/flashcards", "/login", "/register"})
public class RateLimitFilter implements Filter {

    private static final int AI_LIMIT = 10;        // max AI requests per window
    private static final int AUTH_LIMIT = 5;        // max auth requests per window
    private static final long WINDOW_MS = 60_000;   // 1-minute sliding window
    private static final long CLEANUP_INTERVAL_MS = 5 * 60_000; // clean stale entries every 5 min

    // Session-based buckets for AI endpoints
    private final Map<String, Deque<Long>> aiBuckets = new ConcurrentHashMap<>();
    // IP-based buckets for auth endpoints
    private final Map<String, Deque<Long>> authBuckets = new ConcurrentHashMap<>();

    private long lastCleanup = System.currentTimeMillis();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Only rate-limit POST requests (form submissions / API calls)
        if (!"POST".equalsIgnoreCase(req.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        String path = req.getServletPath();
        boolean isAuthEndpoint = "/login".equals(path) || "/register".equals(path);

        String bucketKey;
        Map<String, Deque<Long>> buckets;
        int limit;

        if (isAuthEndpoint) {
            // Rate limit by IP for auth endpoints
            bucketKey = getClientIp(req);
            buckets = authBuckets;
            limit = AUTH_LIMIT;
        } else {
            // Rate limit by session ID for AI endpoints
            HttpSession session = req.getSession(false);
            if (session == null) {
                res.sendRedirect(req.getContextPath() + "/login");
                return;
            }
            bucketKey = session.getId();
            buckets = aiBuckets;
            limit = AI_LIMIT;
        }

        // Periodic cleanup of stale entries
        long now = System.currentTimeMillis();
        if (now - lastCleanup > CLEANUP_INTERVAL_MS) {
            lastCleanup = now;
            cleanupStaleEntries(aiBuckets, now);
            cleanupStaleEntries(authBuckets, now);
        }

        // Sliding window check
        Deque<Long> timestamps = buckets.computeIfAbsent(bucketKey, k -> new ConcurrentLinkedDeque<>());
        long windowStart = now - WINDOW_MS;

        // Remove timestamps outside the current window
        Iterator<Long> it = timestamps.iterator();
        while (it.hasNext()) {
            if (it.next() < windowStart) {
                it.remove();
            } else {
                break; // timestamps are in order, no need to check further
            }
        }

        if (timestamps.size() >= limit) {
            // Rate limit exceeded
            res.setStatus(429);
            req.setAttribute("error", "Too many requests. Please wait a minute before trying again.");
            String forwardPage;
            if (isAuthEndpoint) {
                forwardPage = "/login".equals(path) ? "/jsp/login.jsp" : "/jsp/register.jsp";
            } else {
                forwardPage = "/jsp" + path + ".jsp";
            }
            req.getRequestDispatcher(forwardPage).forward(req, res);
            return;
        }

        timestamps.addLast(now);
        chain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void cleanupStaleEntries(Map<String, Deque<Long>> buckets, long now) {
        long windowStart = now - WINDOW_MS;
        buckets.entrySet().removeIf(entry -> {
            Deque<Long> deque = entry.getValue();
            deque.removeIf(ts -> ts < windowStart);
            return deque.isEmpty();
        });
    }
}
