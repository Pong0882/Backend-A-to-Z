import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Navbar from './components/Navbar'
import Landing from './pages/Landing'
import Login from './pages/Login'
import Signup from './pages/Signup'
import Stocks from './pages/Stocks'
import Watchlist from './pages/Watchlist'
import Portfolio from './pages/Portfolio'
import Strategies from './pages/Strategies'
import Orders from './pages/Orders'
import BrokerAccounts from './pages/BrokerAccounts'

export default function App() {
  return (
    <BrowserRouter>
      <Navbar />
      <Routes>
        <Route path="/" element={<Landing />} />
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />
        <Route path="/stocks" element={<Stocks />} />
        <Route path="/watchlist" element={<Watchlist />} />
        <Route path="/portfolio" element={<Portfolio />} />
        <Route path="/strategies" element={<Strategies />} />
        <Route path="/orders" element={<Orders />} />
        <Route path="/broker-accounts" element={<BrokerAccounts />} />
      </Routes>
    </BrowserRouter>
  )
}
