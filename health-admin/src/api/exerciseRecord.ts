import request from './request'

export interface ExerciseRecord {
  id?: number
  userId: number
  exerciseType: string
  exerciseName?: string
  duration: number
  calories: number
  exerciseTime: string
  note?: string
  createdAt?: string
  user?: {
    id: number
    nickname: string
    openid: string
  }
}

export interface ExerciseRecordQueryParams {
  page?: number
  size?: number
  keyword?: string
  userId?: number
  exerciseType?: string
  startDate?: string
  endDate?: string
}

export interface ExerciseRecordListResult {
  records: ExerciseRecord[]
  total: number
  page: number
  size: number
}

// 获取运动记录列表
export function getExerciseRecordList(params: ExerciseRecordQueryParams) {
  return request.get<ExerciseRecordListResult>('/admin/exercise-records', { params })
}

// 获取运动记录详情
export function getExerciseRecordDetail(id: number) {
  return request.get<ExerciseRecord>(`/admin/exercise-records/${id}`)
}

// 更新运动记录
export function updateExerciseRecord(id: number, data: Partial<ExerciseRecord>) {
  return request.put<ExerciseRecord>(`/admin/exercise-records/${id}`, data)
}

// 删除运动记录
export function deleteExerciseRecord(id: number) {
  return request.delete(`/admin/exercise-records/${id}`)
}

// 获取运动记录统计
export function getExerciseRecordStats() {
  return request.get('/admin/exercise-records/stats')
}

