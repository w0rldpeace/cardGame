package org.cardgame.util

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.cardgame.service.UserDetailsServiceImpl
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter

class JwtFilter(
    private val jwtUtils: JwtUtils,
    private val userDetailsService: UserDetailsServiceImpl
) : OncePerRequestFilter() {

    public override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val jwt = parseJwt(request)
            if (jwt != null && jwtUtils.validateToken(jwt)) {
                val username = jwtUtils.getUserNameFromToken(jwt)
                val userDetails = userDetailsService.loadUserByUsername(username)
                val authentication = UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.authorities
                )
                SecurityContextHolder.getContext().authentication = authentication
            }
        } catch (e: Exception) {
            logger.error("Cannot set user authentication: {}", e)
        }
        filterChain.doFilter(request, response)
    }

    private fun parseJwt(request: HttpServletRequest): String? {
        val headerAuth = request.getHeader("Authorization")
        return if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            headerAuth.substring(7)
        } else null
    }
}
