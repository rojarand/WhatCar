package whatcar.andro.eu

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform