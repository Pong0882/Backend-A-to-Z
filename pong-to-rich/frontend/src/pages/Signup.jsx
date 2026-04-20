import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import api from '../api/axios'

export default function Signup() {
  const navigate = useNavigate()
  const [form, setForm] = useState({ email: '', password: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function handleSubmit(e) {
    e.preventDefault()
    setLoading(true)
    setError('')
    try {
      await api.post('/auth/signup', form)
      navigate('/login')
    } catch (err) {
      setError(err.response?.data?.message || '회원가입에 실패했습니다.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="flex items-center justify-center min-h-[calc(100vh-56px)] px-4">
      <div className="bg-card border border-border rounded-xl p-8 w-full max-w-sm">
        <h2 className="text-2xl font-bold text-white mb-6">회원가입</h2>
        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          <input
            type="email"
            placeholder="이메일"
            value={form.email}
            onChange={(e) => setForm({ ...form, email: e.target.value })}
            className="bg-surface border border-border rounded-lg px-4 py-3 text-white placeholder-muted focus:outline-none focus:border-primary"
            required
          />
          <input
            type="password"
            placeholder="비밀번호 (8자 이상)"
            value={form.password}
            onChange={(e) => setForm({ ...form, password: e.target.value })}
            className="bg-surface border border-border rounded-lg px-4 py-3 text-white placeholder-muted focus:outline-none focus:border-primary"
            required
            minLength={8}
          />
          {error && <p className="text-up text-sm">{error}</p>}
          <button
            type="submit"
            disabled={loading}
            className="bg-primary text-white py-3 rounded-lg font-semibold hover:opacity-85 transition-opacity disabled:opacity-50"
          >
            {loading ? '가입 중...' : '회원가입'}
          </button>
        </form>
        <p className="text-muted text-sm text-center mt-4">
          이미 계정이 있으신가요?{' '}
          <Link to="/login" className="text-primary hover:underline">로그인</Link>
        </p>
      </div>
    </div>
  )
}
