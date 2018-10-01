package com.demo.securitydemo.auth

class AuthenticationToken {

    String tokenValue
    String username

    Date refreshed = new Date()

    def afterLoad() {
        // if being accessed and it is more than a day since last marked as refreshed
        // and it hasn't been wiped out by Quartz job (it exists, duh)
        // then refresh it
        if (refreshed < new Date() -1) {
            refreshed = new Date()
            this.save()
        }
    }

    static constraints = {
    }

    static mapping = {
        tokenValue index: true

        version false
    }
}
