package com.icube.sim.tichu.auth.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class JwtIssueResult {
    private Jwt accessToken;
    private Jwt refreshToken;
}
