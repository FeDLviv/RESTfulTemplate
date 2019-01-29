package net.omisoft.rest.exception

class ResourceNotFoundException : RuntimeException {

    constructor() : super()

    constructor(message: String) : super(message)

}