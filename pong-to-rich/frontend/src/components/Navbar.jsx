import { Link, useNavigate } from 'react-router-dom'

export default function Navbar() {
  const navigate = useNavigate()
  const isLoggedIn = !!localStorage.getItem('accessToken')

  function logout() {
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    navigate('/login')
  }

  return (
    <nav className="bg-card border-b border-border px-6 py-3 flex items-center justify-between">
      <Link to="/" className="text-white font-bold text-lg">
        🏓 Pong to Rich
      </Link>
      <div className="flex items-center gap-6 text-sm text-muted">
        <Link to="/stocks" className="hover:text-white transition-colors">시세</Link>
        {isLoggedIn && (
          <>
            <Link to="/watchlist" className="hover:text-white transition-colors">관심종목</Link>
            <Link to="/portfolio" className="hover:text-white transition-colors">포트폴리오</Link>
            <Link to="/strategies" className="hover:text-white transition-colors">전략</Link>
            <Link to="/orders" className="hover:text-white transition-colors">주문내역</Link>
            <Link to="/broker-accounts" className="hover:text-white transition-colors">계좌관리</Link>
          </>
        )}
        {isLoggedIn ? (
          <button onClick={logout} className="hover:text-white transition-colors">로그아웃</button>
        ) : (
          <Link to="/login" className="bg-primary text-white px-4 py-1.5 rounded-lg hover:opacity-85 transition-opacity">
            로그인
          </Link>
        )}
      </div>
    </nav>
  )
}
