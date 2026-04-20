import { useEffect, useRef, useState } from 'react'
import { createChart, CandlestickSeries, HistogramSeries } from 'lightweight-charts'
import api from '../api/axios'

const fmt = (n) => Number(n).toLocaleString('ko-KR')

export default function Stocks() {
  const [stocks, setStocks] = useState([])
  const [selected, setSelected] = useState(null)
  const [currentPrice, setCurrentPrice] = useState(null)
  const [prices, setPrices] = useState([])
  const [loading, setLoading] = useState(false)
  const [fetchForm, setFetchForm] = useState({ startDate: '', endDate: '' })
  const [fetching, setFetching] = useState(false)
  const [fetchMsg, setFetchMsg] = useState('')

  const candleRef = useRef(null)
  const volumeRef = useRef(null)
  const candleChart = useRef(null)
  const volumeChart = useRef(null)

  useEffect(() => {
    api.get('/stocks').then(({ data }) => {
      const list = data.data || []
      setStocks(list)
      if (list.length > 0) setSelected(list[0])
    }).catch(() => {})
  }, [])

  useEffect(() => {
    if (!selected) return
    setLoading(true)
    setCurrentPrice(null)
    setPrices([])

    // 현재가 (KIS 실시간)
    api.get(`/stocks/${selected.code}`).then(({ data }) => {
      setCurrentPrice(data.data)
    }).catch(() => {})

    // 일봉 데이터
    api.get(`/stocks/${selected.code}/prices`).then(({ data }) => {
      setPrices(data.data || [])
    }).catch(() => {}).finally(() => setLoading(false))
  }, [selected])

  // 차트 렌더링
  useEffect(() => {
    if (!prices.length || !candleRef.current || !volumeRef.current) return

    if (candleChart.current) { candleChart.current.remove(); candleChart.current = null }
    if (volumeChart.current) { volumeChart.current.remove(); volumeChart.current = null }

    const sorted = [...prices].reverse()
    const commonOpts = {
      layout: { background: { color: '#263445' }, textColor: '#a0b0c0' },
      grid: { vertLines: { color: '#2c3e50' }, horzLines: { color: '#2c3e50' } },
      timeScale: { borderColor: '#2c3e50', timeVisible: true, secondsVisible: false },
      rightPriceScale: { borderColor: '#2c3e50' },
    }

    const cc = createChart(candleRef.current, { ...commonOpts, width: candleRef.current.offsetWidth, height: 360 })
    const cs = cc.addSeries(CandlestickSeries, { upColor: '#e74c3c', downColor: '#3498db', borderUpColor: '#e74c3c', borderDownColor: '#3498db', wickUpColor: '#e74c3c', wickDownColor: '#3498db' })
    cs.setData(sorted.map(d => ({ time: String(d.tradeDate), open: d.openPrice, high: d.highPrice, low: d.lowPrice, close: d.closePrice })))

    const vc = createChart(volumeRef.current, { ...commonOpts, width: volumeRef.current.offsetWidth, height: 100, timeScale: { ...commonOpts.timeScale, visible: false } })
    const vs = vc.addSeries(HistogramSeries, { priceFormat: { type: 'volume' }, priceScaleId: '', scaleMargins: { top: 0.1, bottom: 0 } })
    vs.setData(sorted.map(d => ({ time: String(d.tradeDate), value: d.volume, color: d.closePrice >= d.openPrice ? 'rgba(231,76,60,0.5)' : 'rgba(52,152,219,0.5)' })))

    cc.timeScale().subscribeVisibleLogicalRangeChange(r => r && vc.timeScale().setVisibleLogicalRange(r))
    vc.timeScale().subscribeVisibleLogicalRangeChange(r => r && cc.timeScale().setVisibleLogicalRange(r))
    cc.timeScale().fitContent()

    const onResize = () => {
      cc.applyOptions({ width: candleRef.current.offsetWidth })
      vc.applyOptions({ width: volumeRef.current.offsetWidth })
    }
    window.addEventListener('resize', onResize)

    candleChart.current = cc
    volumeChart.current = vc

    return () => window.removeEventListener('resize', onResize)
  }, [prices])

  const latest = prices[0]
  const prev = prices[1]
  const diff = latest && prev ? latest.closePrice - prev.closePrice : 0
  const rate = prev ? ((diff / prev.closePrice) * 100).toFixed(2) : '0.00'

  function priceColor(v) {
    return v > 0 ? 'text-up' : v < 0 ? 'text-down' : 'text-muted'
  }

  function signIcon(v) {
    return v > 0 ? '▲' : v < 0 ? '▼' : '-'
  }

  async function handleFetch(e) {
    e.preventDefault()
    if (!selected) return
    setFetching(true)
    setFetchMsg('')
    try {
      const { data } = await api.post(`/stocks/${selected.code}/fetch`, null, {
        params: { startDate: fetchForm.startDate.replace(/-/g, ''), endDate: fetchForm.endDate.replace(/-/g, '') }
      })
      setFetchMsg(`${data.data.savedCount}건 저장 완료`)
      // 데이터 새로고침
      const res = await api.get(`/stocks/${selected.code}/prices`)
      setPrices(res.data.data || [])
    } catch (err) {
      setFetchMsg(err.response?.data?.message || '수집 실패')
    } finally {
      setFetching(false)
    }
  }

  return (
    <div className="max-w-4xl mx-auto px-4 py-8">

      {/* 종목 선택 + 일봉 수집 */}
      <div className="flex flex-wrap gap-3 items-end mb-6">
        <select
          className="bg-card border border-border text-white rounded-lg px-4 py-2.5 min-w-64 focus:outline-none focus:border-primary cursor-pointer"
          value={selected?.code || ''}
          onChange={(e) => {
            const s = stocks.find(s => s.code === e.target.value)
            if (s) setSelected(s)
            setFetchMsg('')
          }}
        >
          {stocks.length === 0 && <option value="">종목 불러오는 중...</option>}
          {stocks.map(s => (
            <option key={s.code} value={s.code}>{s.name} ({s.code})</option>
          ))}
        </select>

        {/* 일봉 수집 폼 */}
        <form onSubmit={handleFetch} className="flex gap-2 items-end flex-wrap">
          <div className="flex flex-col gap-1">
            <label className="text-muted text-xs">시작일</label>
            <input
              type="date"
              value={fetchForm.startDate}
              onChange={(e) => setFetchForm({ ...fetchForm, startDate: e.target.value })}
              className="bg-card border border-border text-white rounded-lg px-3 py-2 text-sm focus:outline-none focus:border-primary"
              required
            />
          </div>
          <div className="flex flex-col gap-1">
            <label className="text-muted text-xs">종료일</label>
            <input
              type="date"
              value={fetchForm.endDate}
              onChange={(e) => setFetchForm({ ...fetchForm, endDate: e.target.value })}
              className="bg-card border border-border text-white rounded-lg px-3 py-2 text-sm focus:outline-none focus:border-primary"
              required
            />
          </div>
          <button
            type="submit"
            disabled={fetching || !selected}
            className="bg-card border border-border text-muted px-4 py-2 rounded-lg text-sm hover:text-white transition-colors disabled:opacity-50"
          >
            {fetching ? '수집 중...' : '일봉 수집'}
          </button>
          {fetchMsg && <span className="text-sm text-green-400">{fetchMsg}</span>}
        </form>
      </div>

      {/* 현재가 카드 */}
      <div className="bg-card border border-border rounded-xl p-6 mb-6">
        {!selected && <p className="text-muted text-center">종목을 선택해주세요.</p>}
        {selected && !currentPrice && !latest && loading && <p className="text-muted text-center">불러오는 중...</p>}
        {selected && (currentPrice || latest) && (() => {
          const price = currentPrice?.stck_prpr ? Number(currentPrice.stck_prpr) : latest?.closePrice
          const isRealtime = !!currentPrice?.stck_prpr
          return (
            <>
              <p className="text-lg font-bold text-white">{selected.name}</p>
              <p className="text-sm text-muted mb-3">{selected.code} · {isRealtime ? '실시간' : `기준일: ${latest?.tradeDate}`}</p>
              <p className={`text-3xl font-bold ${priceColor(diff)}`}>{fmt(price)} <span className="text-base">원</span></p>
              <div className="flex gap-6 mt-3 text-sm text-muted">
                <span className={priceColor(diff)}>{signIcon(diff)} {fmt(Math.abs(diff))} ({rate}%)</span>
                {isRealtime ? (
                  <>
                    <span>고가 {fmt(currentPrice.stck_hgpr)}</span>
                    <span>저가 {fmt(currentPrice.stck_lwpr)}</span>
                    <span>거래량 {fmt(currentPrice.acml_vol)}</span>
                  </>
                ) : latest && (
                  <>
                    <span>고가 {fmt(latest.highPrice)}</span>
                    <span>저가 {fmt(latest.lowPrice)}</span>
                    <span>거래량 {fmt(latest.volume)}</span>
                  </>
                )}
              </div>
            </>
          )
        })()}
      </div>

      {/* 캔들차트 */}
      {prices.length > 0 && (
        <div className="bg-card border border-border rounded-xl overflow-hidden mb-6">
          <div className="px-6 py-4 border-b border-border text-sm text-muted font-semibold">
            캔들차트 · {selected?.name} ({selected?.code})
          </div>
          <div ref={candleRef} />
          <div ref={volumeRef} className="border-t border-border" />
        </div>
      )}

      {/* 일봉 테이블 */}
      <div className="bg-card border border-border rounded-xl overflow-hidden">
        <div className="px-6 py-4 border-b border-border text-sm text-muted font-semibold">
          일봉 데이터 · {selected?.name || ''}
        </div>
        {loading && <p className="text-muted text-center py-12">불러오는 중...</p>}
        {!loading && prices.length === 0 && <p className="text-muted text-center py-12">저장된 데이터가 없습니다.</p>}
        {prices.length > 0 && (
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-border text-muted">
                <th className="text-left px-4 py-3">날짜</th>
                <th className="text-right px-4 py-3">시가</th>
                <th className="text-right px-4 py-3">고가</th>
                <th className="text-right px-4 py-3">저가</th>
                <th className="text-right px-4 py-3">종가</th>
                <th className="text-right px-4 py-3">거래량</th>
              </tr>
            </thead>
            <tbody>
              {prices.map((d) => (
                <tr key={d.tradeDate} className="border-b border-surface hover:bg-surface transition-colors">
                  <td className="text-left px-4 py-2.5 text-muted">{d.tradeDate}</td>
                  <td className="text-right px-4 py-2.5">{fmt(d.openPrice)}</td>
                  <td className="text-right px-4 py-2.5 text-up">{fmt(d.highPrice)}</td>
                  <td className="text-right px-4 py-2.5 text-down">{fmt(d.lowPrice)}</td>
                  <td className={`text-right px-4 py-2.5 font-medium ${d.closePrice >= d.openPrice ? 'text-up' : 'text-down'}`}>{fmt(d.closePrice)}</td>
                  <td className="text-right px-4 py-2.5 text-muted">{fmt(d.volume)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  )
}
