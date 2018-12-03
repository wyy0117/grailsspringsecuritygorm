# this is a demo to use grails spring security
##### grails version:
```
| Grails Version: 3.3.0
| Groovy Version: 2.4.11
| JVM Version: 1.8.0_171
```
##### create project
1. create app
    ```
    grails create-app com.demo.securityDemo.securityDemo
    ``` 
2. add gradle dependency
    ```
    compile 'org.grails.plugins:spring-security-core:3.2.3'

    runtime 'mysql:mysql-connector-java:5.1.29'
    ```
    update your db connection,and I use mysql this time.
1. create user and role
    ```
    grails s2-quickstart com.demo.securitydemo.auth User Role
    ```

1. update BootStrap.groovy to create user
    ```
    def init = { servletContext ->
        def roleUser = Role.findByAuthority("ROLE_USER") ?: new Role(authority: "ROLE_USER").save(flush: true)
        def roleAdmin = Role.findByAuthority("ROLE_ADMIN") ?: new Role(authority: "ROLE_ADMIN").save(flush: true)
    
        def user = User.findByUsername("user") ?: new User(username: "user", password: "user").save(flush: true)
        def admin = User.findByUsername("admin") ?: new User(username: "admin", password: "admin").save(flush: true)
    
        UserRole.create(user, roleUser, true)
        UserRole.create(admin, roleAdmin, true)
    }
    ``` 
1. create a controller named TestController for test
    ```
    def user() {
        render("this is user method")
    }
    
    def admin() {
        render("this is admin method")
    }
    
    def noCheck() {
        render("this is no check method")
    }
    ```  
1. update Application.groovy and add some url filter
    ```
    def userPermission = ["ROLE_USER", "ROLE_ADMIN"]
    def adminPermission = ["ROLE_ADMIN"]
    def allPermission = ["permitAll"]
    
    grails.plugin.springsecurity.controllerAnnotations.staticRules = [
            ...
    
            [pattern: '/test/user', access: userPermission],
            [pattern: '/test/admin', access: adminPermission],
            [pattern: '/test/noCheck', access: allPermission],
    ]
    ```
1. start server.  
there are some url for you to test. localhost:8080/test/user,localhost:8080/test/admin and localhost:8080/test/noCheck,if you are not login or don't have permission,you will get some hint.
##### note  
if you login failed,server will response 302 and redirect to login page but 401,it's not cool,because in your front code may can't filter 302,so you may want get 401,it's ok,just follow these steps:
1. add dependency
    ```
    compile "org.grails.plugins:spring-security-rest:2.0.0.RC1"
    ```
1. add jwt secret(more than 256 bits) in application.groovy  
    grails.plugin.springsecurity.rest.token.storage.jwt.secret="xxxx"
1. login  
    ```
    curl -v -H "Content-Type: application/json" -d '{"username":"admin","password":"admin"}' http://localhost:8080/api/login
    ```    
    you will get a jwt token,it's key is "access_token"
1. send request with jwt token   
      curl -v -H "Authorization:Bearer=token value" http://localhost:8080/api/getMyInfo

##### if you want to use gorm，continue follow steps.    
1. add gradle dependency
     ```    
     compile "org.grails.plugins:spring-security-rest-gorm:2.0.0.M2"
       
     compile 'org.grails.plugins:quartz:2.0.13'
     ```       
1. add domain class to store token
    ```
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
    ```
1. add gorm config in Application.groovy
    ```
    grails.plugin.springsecurity.rest.token.storage.useGorm = true // since using gorm for token storage
    grails.plugin.springsecurity.rest.token.storage.gorm.tokenDomainClassName = 'com.demo.securitydemo.auth.AuthenticationToken'
    grails.plugin.springsecurity.rest.login.failureStatusCode = 401
    grails.plugin.springsecurity.rest.login.active = true
    ```
1. add url mapping to UrlMapping.groovy,if you want to auth with token just add 'rest' in your url
    ```
    "/rest/$controller/$action?/$id?(.$format)?" {
        constraints {
            // apply constraints here
        }
    }
    ```    
1. add filter for 'rest' url
    ```
    grails.plugin.springsecurity.filterChain.chainMap = [
           ...
            
           [pattern: '/rest/**', filters: 'JOINED_FILTERS,-anonymousAuthenticationFilter,-exceptionTranslationFilter,-authenticationProcessingFilter,-securityContextPersistenceFilter,-rememberMeAuthenticationFilter'],
   
           [pattern: '/**', filters: 'JOINED_FILTERS,-restTokenValidationFilter,-restExceptionTranslationFilter'],
    ]
    ```
1. add a job to remove invalid token
    ```
    grails create-job RemoveToken
    ```
    ```
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
    ```
1. start server.  
you can get token by 
    ```
    curl -v -H "Content-Type: application/json" -d '{"username":"user","password":"user"}'  'localhost:8080/api/login'
    ```
    ```
    curl -v -H "Authorization: Bearer s1vonctaoa547hc7pkhlrcepg12rd2om" 'localhost:8080/rest/test/user' 
    ```  
    ```
    curl -v -H "Authorization: Bearer hb97k83h1hjpl7q3cvhdahqgvo0e3u8i" 'localhost:8080/rest/test/admin'
    ```
###### if you want to get some more detail info,these may be help you
- https://grails-plugins.github.io/grails-spring-security-core/3.2.x/index.html#introduction
- http://alvarosanchez.github.io/grails-spring-security-rest/latest/docs/#_introduction_to_the_spring_security_rest_plugin
- http://plugins.grails.org/plugin/grails/quartz
- http://grails-plugins.github.io/grails-quartz/latest/guide/index.html    