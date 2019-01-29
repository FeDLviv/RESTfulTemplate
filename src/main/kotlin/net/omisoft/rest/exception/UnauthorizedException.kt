package net.omisoft.rest.exception

class UnauthorizedException : RuntimeException {

    constructor() : super()

    constructor(message: String) : super(message)

}