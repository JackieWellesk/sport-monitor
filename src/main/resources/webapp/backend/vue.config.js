const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  publicPath: process.env.NODE_ENV === 'production'
      ? '/admin/'
      : '/',
  devServer: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    },
    '/upload': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
})
