import { useEffect, useState } from 'react'
import api from '../api/axios'

const fmt = (n) => Number(n).toLocaleString('ko-KR')
const STATUS_LABEL = { PENDING: '대기중', PARTIAL: '부분체결', FILLED: '체결', CANCELLED: '취소', FAILED: '실패' }
const STATUS_COLOR = { PENDING: 'text-yellow-400', PARTIAL: 'text-blue-400', FILLED: 'text-green-400', CANCELLED: 'text-muted', FAILED: 'text-up' }
const INIT_FORM = { brokerAccountId: '', stockCode: '', orderType: 'BUY', priceType: 'LIMIT', quantity: '', price: '' }

export default function Orders() {
  const [orders, setOrders] = useState([])
  const [accounts, setAccounts] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [showForm, setShowForm] = useState(false)
  const [form, setForm] = useState(INIT_FORM)
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    fetchOrders()
    api.get('/broker-accounts').then(({ data }) => setAccounts(data.data || [])).catch(() => {})
  }, [])

  async function fetchOrders() {
    try {
      const { data } = await api.get('/orders')
      setOrders(data.data || [])
    } catch {
      setError('주문 목록을 불러오지 못했습니다.')
    } finally {
      setLoading(false)
    }
  }

  async function handleCreate(e) {
    e.preventDefault()
    setSubmitting(true)
    setError('')
    try {
      await api.post('/orders', {
        brokerAccountId: Number(form.brokerAccountId),
        stockCode: form.stockCode,
        orderType: form.orderType,
        priceType: form.priceType,
        quantity: Number(form.quantity),
        price: form.priceType === 'LIMIT' ? Number(form.price) : null,
      })
      setForm(INIT_FORM)
      setShowForm(false)
      fetchOrders()
    } catch (err) {
      setError(err.response?.data?.message || '주문 생성에 실패했습니다.')
    } finally {
      setSubmitting(false)
    }
  }

  async function handleCancel(id) {
    try {
      await api.patch(`/orders/${id}/cancel`)
      fetchOrders()
    } catch (err) {
      setError(err.response?.data?.message || '취소에 실패했습니다.')
    }
  }

  return (
    <div className="max-w-4xl mx-auto px-4 py-8">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-white">주문 내역</h1>
        <button
          onClick={() => setShowForm(!showForm)}
          className="bg-primary text-white px-4 py-2 rounded-lg text-sm font-semibold hover:opacity-85 transition-opacity"
        >
          {showForm ? '취소' : '+ 수동 주문'}
        </button>
      </div>

      {/* 주문 생성 폼 */}
      {showForm && (
        <form onSubmit={handleCreate} className="bg-card border border-border rounded-xl p-6 mb-6 flex flex-col gap-4">
          <div className="grid grid-cols-2 gap-4">
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
              <label className="text-muted text-sm mb-1 block">매수/매도</label>
              <select
                value={form.orderType}
                onChange={(e) => setForm({ ...form, orderType: e.target.value })}
                className="w-full bg-surface border border-border rounded-lg px-4 py-2.5 text-white focus:outline-none focus:border-primary"
              >
                <option value="BUY">매수</option>
                <option value="SELL">매도</option>
              </select>
            </div>
            <div>
              <label className="text-muted text-sm mb-1 block">주문 유형</label>
              <select
                value={form.priceType}
                onChange={(e) => setForm({ ...form, priceType: e.target.value })}
                className="w-full bg-surface border border-border rounded-lg px-4 py-2.5 text-white focus:outline-none focus:border-primary"
              >
                <option value="LIMIT">지정가</option>
                <option value="MARKET">시장가</option>
              </select>
            </div>
            <div>
              <label className="text-muted text-sm mb-1 block">수량</label>
              <input
                type="number"
                placeholder="예: 10"
                min="1"
                value={form.quantity}
                onChange={(e) => setForm({ ...form, quantity: e.target.value })}
                className="w-full bg-surface border border-border rounded-lg px-4 py-2.5 text-white placeholder-muted focus:outline-none focus:border-primary"
                required
              />
            </div>
            {form.priceType === 'LIMIT' && (
              <div>
                <label className="text-muted text-sm mb-1 block">지정가 (원)</label>
                <input
                  type="number"
                  placeholder="예: 70000"
                  min="1"
                  value={form.price}
                  onChange={(e) => setForm({ ...form, price: e.target.value })}
                  className="w-full bg-surface border border-border rounded-lg px-4 py-2.5 text-white placeholder-muted focus:outline-none focus:border-primary"
                  required
                />
              </div>
            )}
          </div>
          {error && <p className="text-up text-sm">{error}</p>}
          <button
            type="submit"
            disabled={submitting}
            className="bg-primary text-white py-2.5 rounded-lg font-semibold hover:opacity-85 transition-opacity disabled:opacity-50"
          >
            {submitting ? '주문 중...' : '주문 생성'}
          </button>
        </form>
      )}

      {error && !showForm && <p className="text-up text-sm mb-4">{error}</p>}
      {loading && <p className="text-muted text-center py-12">불러오는 중...</p>}
      {!loading && orders.length === 0 && (
        <p className="text-muted text-center py-12">주문 내역이 없습니다.</p>
      )}

      {orders.length > 0 && (
        <div className="bg-card border border-border rounded-xl overflow-hidden">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-border text-muted">
                <th className="text-left px-4 py-3">종목</th>
                <th className="text-right px-4 py-3">구분</th>
                <th className="text-right px-4 py-3">수량</th>
                <th className="text-right px-4 py-3">가격</th>
                <th className="text-right px-4 py-3">상태</th>
                <th className="text-right px-4 py-3">일시</th>
                <th className="text-right px-4 py-3"></th>
              </tr>
            </thead>
            <tbody>
              {orders.map((o) => (
                <tr key={o.id} className="border-b border-surface hover:bg-surface transition-colors">
                  <td className="text-left px-4 py-3 text-white font-medium">{o.stockCode}</td>
                  <td className={`text-right px-4 py-3 font-semibold ${o.orderType === 'BUY' ? 'text-up' : 'text-down'}`}>
                    {o.orderType === 'BUY' ? '매수' : '매도'}
                  </td>
                  <td className="text-right px-4 py-3">{fmt(o.quantity)}주</td>
                  <td className="text-right px-4 py-3">{o.price ? fmt(o.price) + '원' : '시장가'}</td>
                  <td className={`text-right px-4 py-3 font-semibold ${STATUS_COLOR[o.status]}`}>
                    {STATUS_LABEL[o.status]}
                  </td>
                  <td className="text-right px-4 py-3 text-muted">{o.createdAt?.slice(0, 16).replace('T', ' ')}</td>
                  <td className="text-right px-4 py-3">
                    {o.status === 'PENDING' && (
                      <button onClick={() => handleCancel(o.id)} className="text-xs text-up hover:underline">
                        취소
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
