import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import { resolve } from 'path'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],

  resolve: {
    alias: {
      // "@/components/Button" en lugar de "../../../components/Button"
      '@': resolve(__dirname, './src'),
    },
  },

  server: {
    proxy: {
      // Cualquier petición que empiece con /api la redirige al backend
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        // Spring Boot ya expone /api/... así que no reescribimos el path
      },
    },
  },
})
