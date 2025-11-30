import request from './request'

export interface WeightRecord {
  id?: number
  userId: number
  weight: number
  bmi?: number
  recordedAt: string
  note?: string
  createdAt?: string
  user?: {
    id: number
    nickname: string
    openid: string
  }
}

export interface WeightRecordQueryParams {
  page?: number
  size?: number
  keyword?: string
  userId?: number
  startDate?: string
  endDate?: string
}

export interface WeightRecordListResult {
  records: WeightRecord[]
  total: number
  page: number
  size: number
}

// 获取体重记录列表
export function getWeightRecordList(params: WeightRecordQueryParams) {
  return request.get<WeightRecordListResult>('/admin/weight-records', { params })
}

// 获取体重记录详情
export function getWeightRecordDetail(id: number) {
  return request.get<WeightRecord>(`/admin/weight-records/${id}`)
}

// 更新体重记录
export function updateWeightRecord(id: number, data: Partial<WeightRecord>) {
  return request.put<WeightRecord>(`/admin/weight-records/${id}`, data)
}

// 删除体重记录
export function deleteWeightRecord(id: number) {
  return request.delete(`/admin/weight-records/${id}`)
}

// 获取体重记录统计
export function getWeightRecordStats() {
  return request.get('/admin/weight-records/stats')
}

