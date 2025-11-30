import request from './request'

export interface DietRecord {
  id?: number
  userId: number
  mealType: string
  recordedAt: string
  planCalories?: number
  note?: string
  createdAt?: string
  user?: {
    id: number
    nickname: string
    openid: string
  }
  items?: DietRecordItem[]
}

export interface DietRecordItem {
  id?: number
  dietRecordId: number
  foodId: number
  foodName: string
  quantity: number
  unit: string
  calories: number
  createdAt?: string
}

export interface DietRecordQueryParams {
  page?: number
  size?: number
  keyword?: string
  userId?: number
  mealType?: string
  startDate?: string
  endDate?: string
}

export interface DietRecordListResult {
  records: DietRecord[]
  total: number
  page: number
  size: number
}

// 获取饮食记录列表
export function getDietRecordList(params: DietRecordQueryParams) {
  return request.get<DietRecordListResult>('/admin/diet-records', { params })
}

// 获取饮食记录详情
export function getDietRecordDetail(id: number) {
  return request.get<DietRecord>(`/admin/diet-records/${id}`)
}

// 更新饮食记录
export function updateDietRecord(id: number, data: Partial<DietRecord>) {
  return request.put<DietRecord>(`/admin/diet-records/${id}`, data)
}

// 删除饮食记录
export function deleteDietRecord(id: number) {
  return request.delete(`/admin/diet-records/${id}`)
}

// 获取饮食记录统计
export function getDietRecordStats() {
  return request.get('/admin/diet-records/stats')
}

