plugins {
    id("com.github.null2264.preprocess")
}

preprocess {
    val forge11800 = createNode("1.18-forge", 11800, "yarn")
    val fabric11800 = createNode("1.18-fabric", 11800, "yarn")
    // In case CHASM released
    //val quilt11800 = createNode("1.18-quilt", 11800, "yarn")

    forge11800.link(fabric11800)
}