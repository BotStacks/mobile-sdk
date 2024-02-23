package ai.botstacks.sdk.utils


actual typealias DataMock = Any

actual fun DataMock.companyName(): String = "Acme Financial"
actual fun DataMock.emoji(): String = ":)"
actual fun DataMock.funnyName(): String = "Beaniebee Boomball"
actual fun DataMock.loremParagraph(): String = """
    Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras bibendum in risus ut iaculis. Quisque quis rutrum massa. Pellentesque ut magna sit amet ligula tristique ullamcorper non nec urna. In nunc neque, efficitur sit amet placerat blandit, fringilla at lacus. Nam rhoncus a urna sed scelerisque. Nam molestie dolor sit amet massa sollicitudin, a iaculis metus tempor. Aenean volutpat, diam vel placerat ornare, quam ipsum mattis diam, eu consequat est neque et lorem. Aenean lobortis sed sem sed auctor. Nam leo nisi, feugiat accumsan sollicitudin ut, blandit vel metus. Morbi non sollicitudin nisl. Nulla ac massa vel risus commodo feugiat vitae non purus. Ut nec diam eu felis imperdiet laoreet a ac velit.
""".trimIndent()
actual fun DataMock.loremSentence(): String = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum bibendum elit eu velit imperdiet euismod. Aliquam non."
actual fun DataMock.name(): String = "John Doe"
actual fun DataMock.smileyEmoji(): String = ":)"
actual fun DataMock.username(): String = "user123"