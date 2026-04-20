import { Link } from 'react-router-dom'

export default function Landing() {
  return (
    <div className="flex flex-col items-center justify-center min-h-[calc(100vh-56px)] text-center px-6">
      <img src="/logo.png" alt="Pong to Rich 로고" className="w-48 h-48 rounded-full shadow-2xl mb-8" />
      <h1 className="text-5xl font-bold text-white mb-4">Pong to Rich</h1>
      <p className="text-muted text-lg mb-2">
        AI 기반 주식 자동매매 플랫폼<br />
        한국투자증권 API 연동 · 실시간 시세 조회 · 자동 매매 전략
      </p>
      <span className="inline-block bg-green-500 text-white text-xs font-semibold px-3 py-1 rounded-full mb-10">
        🚧 개발 중
      </span>
      <div className="flex gap-4 flex-wrap justify-center">
        <Link to="/stocks" className="bg-primary text-white px-6 py-3 rounded-lg font-semibold hover:opacity-85 transition-opacity">
          주식 시세 보기
        </Link>
        <Link to="/login" className="bg-card text-muted border border-border px-6 py-3 rounded-lg font-semibold hover:text-white transition-colors">
          로그인
        </Link>
        <a href="/swagger-ui/index.html" className="bg-card text-muted border border-border px-6 py-3 rounded-lg font-semibold hover:text-white transition-colors">
          API 문서 (Swagger)
        </a>
      </div>
      <blockquote className="mt-16 border-l-4 border-primary pl-6 text-left max-w-xl">
        <p className="text-muted italic leading-relaxed">
          "If you don't find a way to make money while you sleep, you will work until you die."
        </p>
        <cite className="block mt-3 text-sm text-border not-italic">— Warren Buffett</cite>
      </blockquote>
      <p className="mt-10 text-xs text-border">
        Built with Spring Boot · Docker · Cloudflare Tunnel
      </p>
    </div>
  )
}
