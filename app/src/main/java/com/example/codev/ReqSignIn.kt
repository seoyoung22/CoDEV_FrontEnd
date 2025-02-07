package com.example.codev

data class ReqSignIn(
    val co_email: String,
    val co_password: String
)

data class ReqGoogleSignIn(
    val co_loginType: String,
    val accessToken: String
)

data class ReqGithubSignIn(
    val co_loginType: String,
    val code: String
)