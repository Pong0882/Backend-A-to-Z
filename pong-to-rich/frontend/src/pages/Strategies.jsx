import { useEffect, useState } from 'react'
import api from '../api/axios'

const STATUS_LABEL = { ACTIVE: '실행중', INACTIVE: '중지', PAUSED: '일시정지' }
const STATUS_COLOR = { ACTIVE: 'text-green-400', INACTIVE: 'text-muted', PAUSED: 'text-yellow-400' }

const INIT_FORM = { brokerAccountId: '', stockCode: '', name: '', orderQuantity: '' }

export default function Strategies() {
  const [list, setList] = useState([])
  const [accounts, setAccounts] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [showForm, setShowForm] = useState(false)
  const [form, setForm] = useState(INIT_FORM)
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    fetchList()
    api.get('/broker-accounts').then(({ data }) => setAccounts(data.data || [])).catch(() => {})
  }, [])

  async function fetchList() {
    try {
      const { data } = await api.get('/strategies')
      setList(data.data || [])
    } catch {
      setError('전략 목록을 불러오지 못했습니다.')
    } finally {
      setLoading(false)
    }
  }

  async function handleCreate(e) {
    e.preventDefault()
    setSubmitting(true)
    setError('')
    try {
      await api.post('/strategies', {
        brokerAccountId: Number(form.brokerAccountId),
        stockCode: form.stockCode,
        name: form.name,
        orderQuantity: Number(form.orderQuantity),
      })
      setForm(INIT_FORM)
      setShowForm(false)
      fetchList()
    } catch (err) {
      setError(err.response?.data?.message || '전략 생성에 실패했습니다.')
    } finally {
      setSubmitting(false)
    }
  }

  async function changeStatus(id, action) {
    try {
      await api.patch(`/strategies/${id}/${action}`)
      fetchList()
    } catch (err) {
      setError(err.response?.data?.message || '상태 변경에 실패했습니다.')
    }
  }

  async function handleDelete(id) {
    if (!confirm('전략을 삭제하시겠습니까?')) return
    try {
      await api.delete(`/strategies/${id}`)
      fetchList()
    } catch (err) {
      setError(err.response?.data?.message || '삭제에 실패했습니다.')
    }
  }

  return (
    <div className="max-w-3xl mx-auto px-4 py-8">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-white">자동매매 전략</h1>
        <button
          onClick={() => setShowForm(!showForm)}
          className="bg-primary text-white px-4 py-2 rounded-lg text-sm font-semibold hover:opacity-85 transition-opacity"
        >
          {showForm ? '취소' : '+ 전략 생성'}
        </button>
      </div>

      {/* 전략 생성 폼 */}
      {showForm && (
        <form onSubmit={handleCreate} className="bg-card border border-border rounded-xl p-6 mb-6 flex flex-col gap-4">
          <div>
            <label className="text-muted text-sm mb-1 block">증권사 계좌</label>
            <select
              value={form.brokerAccountId}
              onChange={(e) => setForm({ ...form, brokerAccountId: e.target.value })}
              className="w-full bg-surface border border-border rounded-lg px-4 py-2.5 text-white focus:outline-none focus:border-primary"
              required
            >
              <option value="">계좌 선택</option>
              {accounts.map(a => (
                <option key={a.id} value={a.id}>{a.broker} · {a.accountType}</option>
              ))}
            </select>
          </div>
          <div>
            <label className="text-muted text-sm mb-1 block">종목 코드</label>
            <input
              placeholder="예: 005930"
              value={form.stockCode}
              onChange={(e) => setForm({ ...form, stockCode: e.target.value })}
              className="w-full bg-surface border border-border rounded-lg px-4 py-2.5 text-white placeholder-muted focus:outline-none focus:border-primary"
              required
            />
          </div>
          <div>
            <label className="text-muted text-sm mb-1 block">전략 이름</label>
            <input
              placeholder="예: 삼성전자 RSI 전략"
              value={form.name}
              onChange={(e) => setForm({ ...form, name: e.target.value })}
              className="w-full bg-surface border border-border rounded-lg px-4 py-2.5 text-white placeholder-muted focus:outline-none focus:border-primary"
              required
            />
          </div>
          <div>
            <label className="text-muted text-sm mb-1 block">주문 수량</label>
            <input
              type="number"
              placeholder="예: 10"
              min="1"
              value={form.orderQuantity}
              onChange={(e) => setForm({ ...form, orderQuantity: e.target.value })}
              className="w-full bg-surface border border-border rounded-lg px-4 py-2.5 text-white placeholder-muted focus:outline-none focus:border-primary"
              required
            />
          </div>
          {error && <p className="text-up text-sm">{error}</p>}
          <button
            type="submit"
            disabled={submitting}
            className="bg-primary text-white py-2.5 rounded-lg font-semibold hover:opacity-85 transition-opacity disabled:opacity-50"
          >
            {submitting ? '생성 중...' : '전략 생성'}
          </button>
        </form>
      )}

      {error && !showForm && <p className="text-up text-sm mb-4">{error}</p>}
      {loading && <p className="text-muted text-center py-12">불러오는 중...</p>}
      {!loading && list.length === 0 && (
        <p className="text-muted text-center py-12">등록된 전략이 없습니다.</p>
      )}

      <div className="flex flex-col gap-4">
        {list.map((s) => (
          <div key={s.id} className="bg-card border border-border rounded-xl p-5">
            <div className="flex items-start justify-between mb-3">
              <div>
                <p className="text-white font-semibold">{s.name}</p>
                <p className="text-muted text-sm mt-0.5">{s.stockCode} · {s.orderQuantity}주</p>
              </div>
              <span className={`text-sm font-semibold ${STATUS_COLOR[s.status]}`}>
                {STATUS_LABEL[s.status]}
              </span>
            </div>
            <div className="flex gap-2 flex-wrap">
              {s.status !== 'ACTIVE' && (
                <button onClick={() => changeStatus(s.id, 'activate')} className="text-xs bg-green-500 text-white px-3 py-1.5 rounded-lg hover:opacity-85 transition-opacity">
                  실행
                </button>
              )}
              {s.status === 'ACTIVE' && (
                <button onClick={() => changeStatus(s.id, 'pause')} className="text-xs bg-yellow-500 text-white px-3 py-1.5 rounded-lg hover:opacity-85 transition-opacity">
                  일시정지
                </button>
              )}
              {s.status !== 'INACTIVE' && (
                <button onClick={() => changeStatus(s.id, 'deactivate')} className="text-xs bg-surface text-muted border border-border px-3 py-1.5 rounded-lg hover:text-white transition-colors">
                  중지
                </button>
              )}
              {s.status !== 'ACTIVE' && (
                <button onClick={() => handleDelete(s.id)} className="text-xs text-up hover:underline px-2 py-1.5">
                  삭제
                </button>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
