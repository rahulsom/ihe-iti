plugins {
  id("com.gradle.develocity").version("4.1.1")
}

develocity {
  buildScan {
    termsOfUseUrl.set("https://gradle.com/terms-of-service")
    termsOfUseAgree.set("yes")
  }
}

rootProject.name = "ihe-iti"
