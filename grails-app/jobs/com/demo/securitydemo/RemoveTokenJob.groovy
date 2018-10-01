package com.demo.securitydemo

import com.demo.securitydemo.auth.AuthenticationToken

class RemoveTokenJob {
    static triggers = {
        cron(name: 'remove token', cronExpression: '0 0 0/1 * * ?')  //everyday
    }

    def execute() {
        // execute job
        List<AuthenticationToken> authenticationTokens = AuthenticationToken.findAllByRefreshedGreaterThanEquals(new Date() - 1)
        log.debug("delete authenticationTokens.size() = ${authenticationTokens.size()}")
        authenticationTokens.each {
            it.delete()
        }
    }
}
