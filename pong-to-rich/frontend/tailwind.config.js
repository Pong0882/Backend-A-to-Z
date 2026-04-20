/** @type {import('tailwindcss').Config} */
export default {
  content: [
    './index.html',
    './src/**/*.{js,jsx}',
  ],
  theme: {
    extend: {
      colors: {
        // pong-to-rich 다크 테마 기존 HTML과 동일한 색상 체계 유지
        surface: '#1e2a3a',
        card: '#263445',
        border: '#2c3e50',
        muted: '#a0b0c0',
        up: '#e74c3c',
        down: '#3498db',
        primary: '#3498db',
      },
    },
  },
  plugins: [],
}

