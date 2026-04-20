import { useEffect, useState } from 'react'
import api from '../api/axios'

const fmt = (n) => Number(n).toLocaleString('ko-KR')

export default function Watchlist() {
  const [list, setList] = useState([])
  const [loading, setLoading] = useState(true)
  const [form, setForm] = useState({ stockCode: '', alertPrice: '' })
  const [error, setError] = useState('')
  const [editId, setEditId] = useState(null)
  const [editPrice, setEditPrice] = useState('')

  useEffect(() => { fetchList() }, [])

  async function fetchList() {
    try {
      const { data } = await api.get('/watchlist')
      setList(data.data || [])
    } catch {
      setError('관심 종목을 불러오지 못했습니다.')
    } finally {
      setLoading(false)
    }
  }

  async function handleAdd(e) {
    e.preventDefault()
    setError('')
    try {
      await api.post('/watchlist', { stockCode: form.stockCode, alertPrice: form.alertPrice ? Number(form.alertPrice) : null })
      setForm({ stockCode: '', alertPrice: '' })
      fetchList()
    } catch (err) {
      setError(err.response?.data?.message || '등록에 실패했습니다.')
    }
  }

  async function handleDelete(id) {
    try {
      await api.delete(`/watchlist/${id}`)
      fetchList()
    } catch {
      setError('삭제에 실패했습니다.')
    }
  }

  async function handleEditSave(id) {
    try {
      await api.patch(`/watchlist/${id}`, { alertPrice: editPrice ? Number(editPrice) : null })
      setEditId(null)
      fetchList()
    } catch {
      setError('수정에 실패했습니다.')
    }
  }

  return (
    <div className="max-w-3xl mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold text-white mb-6">관심 종목</h1>

      {/* 등록 폼 */}
      <form onSubmit={handleAdd} className="bg-card border border-border rounded-xl p-6 mb-6 flex gap-3 flex-wrap">
        <input
          placeholder="종목 코드 (예: 005930)"
          value={form.stockCode}
          onChange={(e) => setForm({ ...form, stockCode: e.target.value })}
          className="bg-surface border border-border rounded-lg px-4 py-2.5 text-white placeholder-muted focus:outline-none focus:border-primary flex-1 min-w-40"
          required
        />
        <input
          type="number"
          placeholder="알림가 (선택)"
          value={form.alertPrice}
          onChange={(e) => setForm({ ...form, alertPrice: e.target.value })}
          className="bg-surface border border-border rounded-lg px-4 py-2.5 text-white placeholder-muted focus:outline-none focus:border-primary w-44"
        />
        <button type="submit" className="bg-primary text-white px-5 py-2.5 rounded-lg font-semibold hover:opacity-85 transition-opacity">
          등록
        </button>
      </form>

      {error && <p className="text-up text-sm mb-4">{error}</p>}

      {loading && <p className="text-muted text-center py-12">불러오는 중...</p>}

      {!loading && list.length === 0 && (
        <p className="text-muted text-center py-12">등록된 관심 종목이 없습니다.</p>
      )}

      {list.length > 0 && (
        <div className="bg-card border border-border rounded-xl overflow-hidden">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-border text-muted">
                <th className="text-left px-4 py-3">종목코드</th>
                <th className="text-right px-4 py-3">알림가</th>
                <th className="text-right px-4 py-3">등록일</th>
                <th className="text-right px-4 py-3">관리</th>
              </tr>
            </thead>
            <tbody>
              {list.map((item) => (
                <tr key={item.id} className="border-b border-surface hover:bg-surface transition-colors">
                  <td className="text-left px-4 py-3 text-white font-medium">{item.stockCode}</td>
                  <td className="text-right px-4 py-3">
                    {editId === item.id ? (
                      <div className="flex gap-2 justify-end">
                        <input
                          type="number"
                          value={editPrice}
                          onChange={(e) => setEditPrice(e.target.value)}
                          className="bg-surface border border-border rounded px-2 py-1 text-white w-32 text-right focus:outline-none focus:border-primary"
                        />
                        <button onClick={() => handleEditSave(item.id)} className="text-primary hover:underline text-xs">저장</button>
                        <button onClick={() => setEditId(null)} className="text-muted hover:underline text-xs">취소</button>
                      </div>
                    ) : (
                      <span className="text-muted">
                        {item.alertPrice ? fmt(item.alertPrice) + ' 원' : '-'}
                      </span>
                    )}
                  </td>
                  <td className="text-right px-4 py-3 text-muted">{item.createdAt?.slice(0, 10)}</td>
                  <td className="text-right px-4 py-3">
                    <div className="flex gap-3 justify-end">
                      <button
                        onClick={() => { setEditId(item.id); setEditPrice(item.alertPrice || '') }}
                        className="text-primary text-xs hover:underline"
                      >수정</button>
                      <button
                        onClick={() => handleDelete(item.id)}
                        className="text-up text-xs hover:underline"
                      >삭제</button>
                    </div>
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
