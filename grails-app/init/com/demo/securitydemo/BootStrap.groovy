package com.demo.securitydemo

import com.demo.securitydemo.auth.Role
import com.demo.securitydemo.auth.User
import com.demo.securitydemo.auth.UserRole

class BootStrap {

    def init = { servletContext ->
        def roleUser = Role.findByAuthority("ROLE_USER") ?: new Role(authority: "ROLE_USER").save(flush: true)
        def roleAdmin = Role.findByAuthority("ROLE_ADMIN") ?: new Role(authority: "ROLE_ADMIN").save(flush: true)

        def user = User.findByUsername("a") ?: new User(username: "a", password: "a").save(flush: true)
        def admin = User.findByUsername("admin") ?: new User(username: "admin", password: "admin").save(flush: true)

        UserRole.create(user, roleUser, true)
        UserRole.create(admin, roleAdmin, true)
    }
    def destroy = {
    }
}
