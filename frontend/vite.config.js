import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      '/bundles': 'http://localhost:8080',
      '/events': 'http://localhost:8080',
      '/timeline': 'http://localhost:8080',
      '/ingest': 'http://localhost:8080',
    },
  },
});
