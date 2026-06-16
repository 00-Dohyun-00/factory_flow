const TOKEN_KEY = 'factory_flow_token'

export const authService = {
  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY)
  },

  setToken(token: string): void {
    localStorage.setItem(TOKEN_KEY, token)
  },

  removeToken(): void {
    localStorage.removeItem(TOKEN_KEY)
  },

  isLoggedIn(): boolean {
    return !!this.getToken()
  },

  async login(username: string, password: string): Promise<void> {
    const res = await fetch(`${import.meta.env.VITE_API_URL}/api/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password })
    })

    if (!res.ok) throw new Error('아이디 또는 비밀번호가 틀렸습니다.')

    const data = await res.json()
    this.setToken(data.token)
  },

  logout(): void {
    this.removeToken()
  }
}
