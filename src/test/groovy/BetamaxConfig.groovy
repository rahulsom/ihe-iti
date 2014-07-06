betamax {
  tapeRoot = new File('src/test/resources/betamax/tapes')
  useProxy = true
  proxyPort = 5555
  proxyTimeout = 60000
  defaultMode = TapeMode.READ_WRITE
  ignoreHosts = ['localhost', '127.0.0.1']
  ignoreLocalhost = false
  sslSupport = false
}
