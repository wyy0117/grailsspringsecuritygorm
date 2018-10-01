package com.demo.securitydemo

class TestController {

    def index() { }

    def user() {
        render("this is user method")
    }

    def admin() {
        render("this is admin method")
    }

    def noCheck() {
        render("this is no check method")
    }
}
