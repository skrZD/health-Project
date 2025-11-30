import request from './request'

export interface LoginParams {
  username: string
  password: string
}

export interface LoginResult {
  token: string
  user: any
  expiresIn: number
}

// 管理员登录
export function adminLogin(params: LoginParams) {
  return request.post<LoginResult>('/admin/auth/login', params)
}

// 获取当前管理员信息
export function getCurrentAdmin() {
  return request.get('/admin/auth/me')
}

// 刷新token
export function refreshToken() {
  return request.post('/admin/auth/refresh')
}

// 退出登录
export function logout() {
  return request.post('/admin/auth/logout')
}

