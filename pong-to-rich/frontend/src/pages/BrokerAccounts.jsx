import { useEffect, useState } from 'react'
import api from '../api/axios'

const INIT_FORM = { broker: 'KIS', accountType: 'MOCK', appkey: '', appsecret: '' }

export default function BrokerAccounts() {
  const [list, setList] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [showForm, setShowForm] = useState(false)
  const [form, setForm] = useState(INIT_FORM)
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => { fetchList() }, [])

  async function fetchList() {
    try {
      const { data } = await api.get('/broker-accounts')
      setList(data.data || [])
    } catch {
      setError('계좌 목록을 불러오지 못했습니다.')
    } finally {
      setLoading(false)
    }
  }

  async function handleCreate(e) {
    e.preventDefault()
    setSubmitting(true)
    setError('')
    try {
      await api.post('/broker-accounts', form)
      setForm(INIT_FORM)
      setShowForm(false)
      fetchList()
    } catch (err) {
      setError(err.response?.data?.message || '계좌 등록에 실패했습니다.')
    } finally {
      setSubmitting(false)
    }
  }

  async function handleDeactivate(id) {
    if (!confirm('계좌를 비활성화하시겠습니까?')) return
    try {
      await api.delete(`/broker-accounts/${id}`)
      fetchList()
    } catch (err) {
      setError(err.response?.data?.message || '비활성화에 실패했습니다.')
    }
  }

  return (
    <div className="max-w-3xl mx-auto px-4 py-8">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-white">증권사 계좌</h1>
        <button
          onClick={() => setShowForm(!showForm)}
          className="bg-primary text-white px-4 py-2 rounded-lg text-sm font-semibold hover:opacity-85 transition-opacity"
        >
          {showForm ? '취소' : '+ 계좌 등록'}
        </button>
      </div>

      {/* 계좌 등록 폼 */}
      {showForm && (
        <form onSubmit={handleCreate} className="bg-card border border-border rounded-xl p-6 mb-6 flex flex-col gap-4">
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="text-muted text-sm mb-1 block">증권사</label>
              <select
                value={form.broker}
                onChange={(e) => setForm({ ...form, broker: e.target.value })}
                className="w-full bg-surface border border-border rounded-lg px-4 py-2.5 text-white focus:outline-none focus:border-primary"
              >
                <option value="KIS">한국투자증권 (KIS)</option>
                <option value="KIWOOM">키움증권</option>
                <option value="SAMSUNG">삼성증권</option>
              </select>
            </div>
            <div>
              <label className="text-muted text-sm mb-1 block">계좌 유형</label>
              <select
                value={form.accountType}
                onChange={(e) => setForm({ ...form, accountType: e.target.value })}
                className="w-full bg-surface border border-border rounded-lg px-4 py-2.5 text-white focus:outline-none focus:border-primary"
              >
                <option value="MOCK">모의투자</option>
                <option value="REAL">실전투자</option>
              </select>
            </div>
          </div>
          <div>
            <label className="text-muted text-sm mb-1 block">App Key</label>
            <input
              placeholder="KIS Developers App Key"
              value={form.appkey}
              onChange={(e) => setForm({ ...form, appkey: e.target.value })}
              className="w-full bg-surface border border-border rounded-lg px-4 py-2.5 text-white placeholder-muted focus:outline-none focus:border-primary font-mono text-sm"
              required
            />
          </div>
          <div>
            <label className="text-muted text-sm mb-1 block">App Secret</label>
            <input
              type="password"
              placeholder="KIS Developers App Secret"
              value={form.appsecret}
              onChange={(e) => setForm({ ...form, appsecret: e.target.value })}
              className="w-full bg-surface border border-border rounded-lg px-4 py-2.5 text-white placeholder-muted focus:outline-none focus:border-primary font-mono text-sm"
              required
            />
          </div>
          <p className="text-muted text-xs">App Key / Secret은 AES-256으로 암호화되어 저장됩니다.</p>
          {error && <p className="text-up text-sm">{error}</p>}
          <button
            type="submit"
            disabled={submitting}
            className="bg-primary text-white py-2.5 rounded-lg font-semibold hover:opacity-85 transition-opacity disabled:opacity-50"
          >
            {submitting ? '등록 중...' : '계좌 등록'}
          </button>
        </form>
      )}

      {error && !showForm && <p className="text-up text-sm mb-4">{error}</p>}
      {loading && <p className="text-muted text-center py-12">불러오는 중...</p>}
      {!loading && list.length === 0 && (
        <p className="text-muted text-center py-12">등록된 계좌가 없습니다.</p>
      )}

      <div className="flex flex-col gap-4">
        {list.map((a) => (
          <div key={a.id} className="bg-card border border-border rounded-xl p-5 flex items-center justify-between">
            <div>
              <p className="text-white font-semibold">{a.broker}</p>
              <p className="text-muted text-sm mt-0.5">
                {a.accountType === 'MOCK' ? '모의투자' : '실전투자'} ·{' '}
                <span className={a.active ? 'text-green-400' : 'text-up'}>
                  {a.active ? '활성' : '비활성'}
                </span>
              </p>
            </div>
            {a.active && (
              <button
                onClick={() => handleDeactivate(a.id)}
                className="text-xs text-up hover:underline"
              >
                비활성화
              </button>
            )}
          </div>
        ))}
      </div>
    </div>
  )
}
