package com.payroll.config;

import com.payroll.service.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;


import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_setsAuthentication_whenTokenIsValid() throws Exception {
        String token = "valid.token.here";
        String username = "user";

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(username);
        when(userDetails.getAuthorities()).thenReturn(null);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(jwtUtil.validateToken(token, username)).thenReturn(true);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        filter.doFilterInternal(request, response, filterChain);

        // Verifica se a autenticação foi setada no SecurityContext
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(userDetails, SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        // Verifica que o filterChain continuou
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_doesNotSetAuthentication_whenTokenIsInvalid() throws Exception {
        String token = "invalid.token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenThrow(new RuntimeException("Token inválido"));

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_passesThrough_whenNoAuthorizationHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }
}
