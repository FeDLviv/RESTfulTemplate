package net.omisoft.rest.exception

class BadRequestException : RuntimeException {

    constructor() : super()

    constructor(message: String) : super(message)

}