import axios from 'axios'

// 로컬: Vite proxy로 /api → localhost:8080 (상대경로)
// 프로덕션: VITE_API_BASE_URL=https://api.pongtrader.pro (빌드 시 환경변수 주입)
const BASE_URL = import.meta.env.VITE_API_BASE_URL
  ? `${import.meta.env.VITE_API_BASE_URL}/api`
  : '/api'

const api = axios.create({
  baseURL: BASE_URL,
})

// 인터셉터 없는 인스턴스 — 로그인/회원가입 전용
// 401을 refresh 로직으로 가로채지 않고 컴포넌트에서 직접 에러 처리
export const authApi = axios.create({
  baseURL: BASE_URL,
})

// 요청 인터셉터 — 모든 요청에 Access Token 자동 첨부
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 응답 인터셉터 — 401 시 Refresh Token으로 재발급 후 재시도
api.interceptors.response.use(
  (res) => res,
  async (err) => {
    const original = err.config

    if (err.response?.status === 401 && !original._retry) {
      original._retry = true
      try {
        const refreshToken = localStorage.getItem('refreshToken')
        const { data } = await axios.post(`${BASE_URL}/auth/refresh`, { refreshToken })
        localStorage.setItem('accessToken', data.data.accessToken)
        original.headers.Authorization = `Bearer ${data.data.accessToken}`
        return api(original)
      } catch {
        localStorage.removeItem('accessToken')
        localStorage.removeItem('refreshToken')
        window.location.href = '/login'
      }
    }

    return Promise.reject(err)
  }
)

export default api
