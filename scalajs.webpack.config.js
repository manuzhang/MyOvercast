module.exports = {
  "entry": {
    "frontend-opt": ["/home/runner/work/MyOvercast/MyOvercast/js/target/scala-2.13/scalajs-bundler/main/frontend-opt.js"]
  },
  "output": {
    "path": "/home/runner/work/MyOvercast/MyOvercast/js/target/scala-2.13/scalajs-bundler/main",
    "filename": "[name]-bundle.js"
  },
  "mode": "production",
  "devServer": {
    "port": 8080
  },
  "devtool": "source-map",
  "module": {
    "rules": [{
      "test": new RegExp("\\.js$"),
      "enforce": "pre",
      "use": ["source-map-loader"]
    }]
  }
}