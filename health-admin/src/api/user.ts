import request from './request'

export interface User {
  id: number
  openid?: string
  unionid?: string
  nickname: string
  name?: string
  avatar?: string
  gender?: string
  age?: number
  height?: number
  weight?: number
  dailyCalorieGoal?: number
  dailyExerciseGoal?: number
  dailyStepGoal?: number
  createdAt: string
  updatedAt: string
}

export interface UserQueryParams {
  page?: number
  size?: number
  keyword?: string
  gender?: string
  startDate?: string
  endDate?: string
}

export interface UserListResult {
  records: User[]
  total: number
  page: number
  size: number
}

// 获取用户列表
export function getUserList(params: UserQueryParams) {
  return request.get<UserListResult>('/admin/users', { params })
}

// 获取用户详情
export function getUserDetail(id: number) {
  return request.get<User>(`/admin/users/${id}`)
}

// 更新用户信息
export function updateUser(id: number, data: Partial<User>) {
  return request.put<User>(`/admin/users/${id}`, data)
}

// 删除用户
export function deleteUser(id: number) {
  return request.delete(`/admin/users/${id}`)
}

// 获取用户统计
export function getUserStats() {
  return request.get('/admin/users/stats')
}

