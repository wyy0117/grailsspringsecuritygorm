package com.demo.securitydemo.auth

class AuthenticationToken {

    String tokenValue
    String username

    static constraints = {
    }

    static mapping = {
        tokenValue index: true

        version false
    }
}
