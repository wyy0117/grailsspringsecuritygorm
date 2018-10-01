// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'com.demo.securitydemo.auth.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'com.demo.securitydemo.auth.UserRole'
grails.plugin.springsecurity.authority.className = 'com.demo.securitydemo.auth.Role'

def userPermission = ["ROLE_USER", "ROLE_ADMIN"]
def adminPermission = ["ROLE_USER"]
def allPermission = ["permitAll"]


grails.plugin.springsecurity.controllerAnnotations.staticRules = [
        [pattern: '/', access: ['permitAll']],
        [pattern: '/error', access: ['permitAll']],
        [pattern: '/index', access: ['permitAll']],
        [pattern: '/index.gsp', access: ['permitAll']],
        [pattern: '/shutdown', access: ['permitAll']],
        [pattern: '/assets/**', access: ['permitAll']],
        [pattern: '/**/js/**', access: ['permitAll']],
        [pattern: '/**/css/**', access: ['permitAll']],
        [pattern: '/**/images/**', access: ['permitAll']],
        [pattern: '/**/favicon.ico', access: ['permitAll']],

        [pattern: '/test/user', access: userPermission],
        [pattern: '/test/admin', access: adminPermission],
        [pattern: '/test/noCheck', access: allPermission],
]

grails.plugin.springsecurity.filterChain.chainMap = [
        [pattern: '/assets/**', filters: 'none'],
        [pattern: '/**/js/**', filters: 'none'],
        [pattern: '/**/css/**', filters: 'none'],
        [pattern: '/**/images/**', filters: 'none'],
        [pattern: '/**/favicon.ico', filters: 'none'],
        [pattern: '/**', filters: 'JOINED_FILTERS']
]

grails.plugin.springsecurity.rest.token.storage.useGorm = true // since using gorm for token storage
grails.plugin.springsecurity.rest.token.storage.gorm.tokenDomainClassName = 'com.demo.securitydemo.auth.AuthenticationToken'
grails.plugin.springsecurity.rest.login.failureStatusCode = 401
grails.plugin.springsecurity.rest.login.active = true


