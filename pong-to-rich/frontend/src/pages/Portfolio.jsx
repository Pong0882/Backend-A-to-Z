import { useEffect, useState } from 'react'
import api from '../api/axios'

const fmt = (n) => Number(n).toLocaleString('ko-KR')

export default function Portfolio() {
  const [portfolio, setPortfolio] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [showHidden, setShowHidden] = useState(false)

  useEffect(() => { fetchPortfolio() }, [])

  async function fetchPortfolio() {
    try {
      const { data } = await api.get('/portfolio')
      setPortfolio(data.data)
    } catch {
      setError('포트폴리오를 불러오지 못했습니다.')
    } finally {
      setLoading(false)
    }
  }

  async function toggleHidden(holdingId) {
    try {
      await api.patch(`/portfolio/holdings/${holdingId}/toggle-hidden`)
      fetchPortfolio()
    } catch {
      setError('변경에 실패했습니다.')
    }
  }

  const allHoldings = portfolio?.holdings || []
  const visible = allHoldings.filter(h => !h.hidden)
  const hidden = allHoldings.filter(h => h.hidden)
  const displayed = showHidden ? allHoldings : visible

  return (
    <div className="max-w-3xl mx-auto px-4 py-8">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-white">포트폴리오</h1>
        {hidden.length > 0 && (
          <button
            onClick={() => setShowHidden(!showHidden)}
            className="text-sm text-muted hover:text-white transition-colors"
          >
            {showHidden ? '숨긴 종목 감추기' : `숨긴 종목 보기 (${hidden.length})`}
          </button>
        )}
      </div>

      {error && <p className="text-up text-sm mb-4">{error}</p>}
      {loading && <p className="text-muted text-center py-12">불러오는 중...</p>}

      {!loading && portfolio && (
        <>
          {displayed.length === 0 && (
            <p className="text-muted text-center py-12">보유 종목이 없습니다.</p>
          )}

          {displayed.length > 0 && (
            <div className="bg-card border border-border rounded-xl overflow-hidden">
              <table className="w-full text-sm">
                <thead>
                  <tr className="border-b border-border text-muted">
                    <th className="text-left px-4 py-3">종목코드</th>
                    <th className="text-right px-4 py-3">보유수량</th>
                    <th className="text-right px-4 py-3">평균매수가</th>
                    <th className="text-right px-4 py-3">상태</th>
                    <th className="text-right px-4 py-3">관리</th>
                  </tr>
                </thead>
                <tbody>
                  {displayed.map((h) => (
                    <tr key={h.id} className={`border-b border-surface hover:bg-surface transition-colors ${h.hidden ? 'opacity-50' : ''}`}>
                      <td className="text-left px-4 py-3 text-white font-medium">{h.stockCode}</td>
                      <td className="text-right px-4 py-3">{fmt(h.quantity)} 주</td>
                      <td className="text-right px-4 py-3">{fmt(h.averagePrice)} 원</td>
                      <td className="text-right px-4 py-3">
                        <span className={h.hidden ? 'text-muted text-xs' : 'text-green-400 text-xs'}>
                          {h.hidden ? '숨김' : '표시중'}
                        </span>
                      </td>
                      <td className="text-right px-4 py-3">
                        <button
                          onClick={() => toggleHidden(h.id)}
                          className="text-xs text-primary hover:underline"
                        >
                          {h.hidden ? '표시' : '숨김'}
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </>
      )}
    </div>
  )
}
